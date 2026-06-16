package noshtek.back_pain_prototype.core.data.gamification

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import noshtek.back_pain_prototype.core.data.db.entity.AchievementUnlockEntity
import noshtek.back_pain_prototype.core.data.db.entity.DailyCheckInEntity
import noshtek.back_pain_prototype.core.data.repository.GamificationRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single entry point for the Aura engagement layer: logs assessment / ritual /
 * check-in events, advances streaks, recomputes the Living Spine vitality, and
 * unlocks recovery milestones. Durable state changes happen first inside
 * repository transactions; [events] fire only afterwards, so the UI never
 * celebrates a change that didn't commit. Callers in the medical flow must wrap
 * calls in runCatching — engagement may never block an assessment.
 */
@Singleton
class GamificationManager @Inject constructor(
    private val repo: GamificationRepository,
) {

    private val _events = MutableSharedFlow<GamificationEvent>(extraBufferCapacity = 32)

    /** Transient celebration events. No replay: a missed event loses an animation, never progress. */
    val events: SharedFlow<GamificationEvent> = _events.asSharedFlow()

    /** Reactive read model for the Home/Journey UI. */
    val snapshot: Flow<GamificationSnapshot> = combine(
        repo.observeState(),
        repo.observeCheckInHistory(),
        repo.observeRitualCompletions(),
        repo.observeLatestClinical(),
    ) { state, checkIns, completions, clinical ->
        val today = LocalDate.now()
        val todayEpoch = today.toEpochDay()
        val vitality = state?.latestVitality ?: SpineVitality.NEUTRAL_BASE_VITALITY
        val doneToday = completions.asSequence()
            .filter { it.completionDay == todayEpoch }
            .map { it.ritualId }
            .toSet()
        val statuses = RitualCatalog.personalizedFor(clinical)
            .map { RitualStatus(it, it.id in doneToday) }
        GamificationSnapshot(
            vitality = vitality,
            stage = SpineVitality.stageFor(vitality),
            peakVitality = state?.peakVitality ?: 0,
            effectiveStreakDays = StreakLogic.effectiveStreak(
                state?.lastActivityDay?.let(LocalDate::ofEpochDay), today, state?.currentStreakDays ?: 0,
            ),
            longestStreakDays = state?.longestStreakDays ?: 0,
            checkedInToday = checkIns.any { it.checkInDay == todayEpoch },
            rituals = statuses,
            ritualsDoneToday = statuses.count { it.done },
            ritualsTotalToday = statuses.size,
        )
    }

    val milestoneUnlocks: Flow<List<AchievementUnlockEntity>> = repo.observeUnlocks()
    val checkInHistory: Flow<List<DailyCheckInEntity>> = repo.observeCheckInHistory()

    // ── Assessment hooks ──────────────────────────────────────────────────────

    /** Idempotent per (assessment, step): re-saving a section via back-navigation logs nothing. */
    suspend fun onAssessmentStepCompleted(assessmentId: String, step: AssessmentStep) {
        if (assessmentId.isEmpty()) return
        if (repo.tryLogEvent(DedupeKeys.step(assessmentId, step), RewardType.STEP)) {
            evaluateMilestones()
        }
    }

    /** Completion event + streak advance + vitality recompute. Idempotent per assessment. */
    suspend fun onAssessmentCompleted(assessmentId: String) {
        if (assessmentId.isEmpty()) return
        repo.tryLogEvent(DedupeKeys.completion(assessmentId), RewardType.COMPLETION)
        advanceStreak()
        recomputeVitality()
        evaluateMilestones()
    }

    // ── Daily rituals ───────────────────────────────────────────────────────-

    suspend fun completeRitual(ritualId: String): RitualResult {
        val ritual = RitualCatalog.byId(ritualId) ?: return RitualResult.AlreadyDoneToday
        val today = LocalDate.now()
        if (!repo.tryCompleteRitual(ritualId, today.toEpochDay())) {
            return RitualResult.AlreadyDoneToday
        }
        advanceStreak(today)
        recomputeVitality()
        evaluateMilestones()
        val total = RitualCatalog.personalizedFor(repo.latestClinicalOnce()).size
        val done = repo.countRitualCompletionsForDay(today.toEpochDay())
        _events.tryEmit(GamificationEvent.RitualCompleted(ritual, done, total))
        return RitualResult.Done(done)
    }

    // ── Daily check-in ──────────────────────────────────────────────────────-

    suspend fun checkInToday(mood: CheckInMood): CheckInResult {
        val today = LocalDate.now()
        if (!repo.tryRecordCheckIn(today.toEpochDay(), mood)) {
            return CheckInResult.AlreadyCheckedInToday
        }
        repo.tryLogEvent(DedupeKeys.checkIn(today.toEpochDay()), RewardType.CHECK_IN)
        val streakDays = advanceStreak(today)
        recomputeVitality()
        evaluateMilestones()
        _events.tryEmit(GamificationEvent.CheckInRecorded(mood, streakDays))
        return CheckInResult.Done(streakDays)
    }

    /** Current counts for milestone progress hints (Journey screen). */
    suspend fun milestoneContext(): MilestoneContext = repo.buildMilestoneContext()

    // ── Internals ─────────────────────────────────────────────────────────────

    /**
     * Recomputes Spine Vitality from the latest clinical scores + recent habit
     * adherence and persists it (bumping the monotonic peak). Emits
     * [GamificationEvent.VitalityImproved] only when vitality rose.
     */
    private suspend fun recomputeVitality() {
        val today = LocalDate.now()
        val clinical = repo.latestClinicalOnce()
        val state = repo.getStateOnce()
        val streak = StreakLogic.effectiveStreak(
            state?.lastActivityDay?.let(LocalDate::ofEpochDay), today, state?.currentStreakDays ?: 0,
        )
        val ritualsPerDay = RitualCatalog.personalizedFor(clinical).size.coerceAtLeast(1)
        val done7d = repo.ritualCompletionsInRange(today.minusDays(6).toEpochDay(), today.toEpochDay())
        val rate = (done7d.toFloat() / (ritualsPerDay * 7)).coerceIn(0f, 1f)
        val checkedIn = repo.isCheckedInOn(today.toEpochDay())

        val newVitality = SpineVitality.compute(
            clinical, SpineVitality.AdherenceWindow(rate, streak, checkedIn),
        )
        val before = state?.latestVitality ?: SpineVitality.NEUTRAL_BASE_VITALITY
        repo.applyVitality(newVitality)
        if (newVitality > before) {
            _events.tryEmit(
                GamificationEvent.VitalityImproved(before, newVitality, SpineVitality.stageFor(newVitality))
            )
        }
    }

    /** Advances the streak for [today] and emits any newly crossed one-time milestones. */
    private suspend fun advanceStreak(today: LocalDate = LocalDate.now()): Int {
        val state = repo.getStateOnce()
        val advance = StreakLogic.advance(
            lastActivityDay = state?.lastActivityDay?.let(LocalDate::ofEpochDay),
            today = today,
            currentStreak = state?.currentStreakDays ?: 0,
            longestStreak = state?.longestStreakDays ?: 0,
        )
        if (!advance.changed) return advance.newStreak
        repo.applyStreakAdvance(advance, today)
        for (days in advance.crossedMilestones) {
            if (repo.tryLogEvent(DedupeKeys.streakMilestone(days), RewardType.STREAK_MILESTONE, days)) {
                _events.tryEmit(GamificationEvent.StreakMilestone(days))
            }
        }
        return advance.newStreak
    }

    /**
     * Unlocks every milestone whose predicate now passes. Safe to call after any
     * event: unlock rows + ledger keys dedupe re-entry, and predicates are
     * monotonic so stale context can only delay an unlock.
     */
    private suspend fun evaluateMilestones() {
        val ctx = repo.buildMilestoneContext()
        val unlockedIds = repo.getUnlockedIdsOnce().toSet()
        for (milestone in MilestoneCatalog.ALL) {
            if (milestone.id in unlockedIds || !milestone.isUnlocked(ctx)) continue
            if (repo.tryUnlockMilestone(milestone)) {
                _events.tryEmit(GamificationEvent.MilestoneReached(milestone))
            }
        }
    }
}
