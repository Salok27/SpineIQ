package noshtek.back_pain_prototype.core.data.gamification

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import noshtek.back_pain_prototype.core.data.db.entity.AchievementUnlockEntity
import noshtek.back_pain_prototype.core.data.db.entity.AvatarItemEntity
import noshtek.back_pain_prototype.core.data.db.entity.DailyCheckInEntity
import noshtek.back_pain_prototype.core.data.repository.GamificationRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single entry point for everything gamification in the app module: grants
 * rewards on assessment/check-in events, advances streaks, evaluates
 * achievements, and runs the shop. Durable state changes happen first inside
 * repository transactions; [events] fire only afterwards, so the UI never
 * celebrates a grant that didn't commit. Callers in the medical flow must
 * wrap calls in runCatching — gamification may never block an assessment.
 */
@Singleton
class GamificationManager @Inject constructor(
    private val repo: GamificationRepository,
) {

    private val _events = MutableSharedFlow<GamificationEvent>(extraBufferCapacity = 32)

    /** Transient celebration events. No replay: a missed event loses an animation, never currency. */
    val events: SharedFlow<GamificationEvent> = _events.asSharedFlow()

    val snapshot: Flow<GamificationSnapshot> =
        combine(repo.observeState(), repo.observeCheckInHistory()) { state, checkIns ->
            val today = LocalDate.now()
            val xp = state?.xp ?: 0
            GamificationSnapshot(
                coins = state?.coins ?: 0,
                xp = xp,
                level = LevelTable.levelFor(xp),
                nextLevel = LevelTable.nextLevel(xp),
                progressToNext = LevelTable.progressToNext(xp),
                xpIntoLevel = LevelTable.xpIntoLevel(xp),
                xpForNextLevel = LevelTable.xpForNextLevel(xp),
                effectiveStreakDays = StreakLogic.effectiveStreak(
                    state?.lastActivityDay?.let(LocalDate::ofEpochDay), today, state?.currentStreakDays ?: 0,
                ),
                longestStreakDays = state?.longestStreakDays ?: 0,
                checkedInToday = checkIns.any { it.checkInDay == today.toEpochDay() },
            )
        }

    val ownedItems: Flow<List<AvatarItemEntity>> = repo.observeOwnedItems()
    val unlocks: Flow<List<AchievementUnlockEntity>> = repo.observeUnlocks()
    val checkInHistory: Flow<List<DailyCheckInEntity>> = repo.observeCheckInHistory()

    // ── Assessment hooks ──────────────────────────────────────────────────────

    /** Idempotent per (assessment, step): re-saving a section via back-navigation grants nothing. */
    suspend fun onAssessmentStepCompleted(assessmentId: String, step: AssessmentStep) {
        if (assessmentId.isEmpty()) return
        val granted = grantAndNotify(
            DedupeKeys.step(assessmentId, step), RewardType.STEP,
            Economy.COINS_PER_STEP, Economy.XP_PER_STEP,
        )
        if (granted) evaluateAchievements()
    }

    /** Completion grant + streak advance. Idempotent per assessment. */
    suspend fun onAssessmentCompleted(assessmentId: String) {
        if (assessmentId.isEmpty()) return
        grantAndNotify(
            DedupeKeys.completion(assessmentId), RewardType.COMPLETION,
            Economy.COINS_PER_COMPLETION, Economy.XP_PER_COMPLETION,
        )
        advanceStreak()
        evaluateAchievements()
    }

    // ── Daily check-in ────────────────────────────────────────────────────────

    suspend fun checkInToday(mood: CheckInMood): CheckInResult {
        val today = LocalDate.now()
        if (!repo.tryRecordCheckIn(today.toEpochDay(), mood)) {
            return CheckInResult.AlreadyCheckedInToday
        }
        grantAndNotify(
            DedupeKeys.checkIn(today.toEpochDay()), RewardType.CHECK_IN,
            Economy.COINS_PER_CHECKIN, Economy.XP_PER_CHECKIN,
        )
        val streakDays = advanceStreak(today)
        evaluateAchievements()
        _events.tryEmit(GamificationEvent.CheckInRecorded(mood, streakDays))
        return CheckInResult.Done(streakDays)
    }

    // ── Shop ──────────────────────────────────────────────────────────────────

    suspend fun purchaseItem(itemId: String): PurchaseResult {
        val item = AvatarCatalog.byId(itemId) ?: return PurchaseResult.UnknownItem
        val result = repo.tryPurchase(item)
        if (result == PurchaseResult.Success) evaluateAchievements()
        return result
    }

    suspend fun equipItem(itemId: String) = repo.equipItem(itemId)

    suspend fun unequipItem(itemId: String) = repo.unequipItem(itemId)

    /** Current counts for achievement progress hints (Awards screen). */
    suspend fun achievementContext(): AchievementContext = repo.buildAchievementContext()

    // ── Internals ─────────────────────────────────────────────────────────────

    /** Returns true when the grant was new (not deduped). */
    private suspend fun grantAndNotify(
        dedupeKey: String,
        type: RewardType,
        coins: Int,
        xp: Int,
    ): Boolean {
        val outcome = repo.tryGrant(dedupeKey, type, coins, xp) ?: return false
        emitRewardEvents(outcome, coins, xp)
        return true
    }

    private fun emitRewardEvents(outcome: GamificationRepository.GrantOutcome, coins: Int, xp: Int) {
        if (coins > 0) _events.tryEmit(GamificationEvent.CoinsEarned(coins, outcome.after.coins))
        if (xp > 0) _events.tryEmit(GamificationEvent.XpEarned(xp, outcome.after.xp))
        val levelBefore = LevelTable.levelFor(outcome.before.xp)
        val levelAfter = LevelTable.levelFor(outcome.after.xp)
        if (levelAfter.number > levelBefore.number) {
            _events.tryEmit(GamificationEvent.LevelUp(levelAfter))
        }
    }

    /** Advances the streak for today and pays any newly crossed one-time milestones. */
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
            val bonus = Economy.STREAK_MILESTONE_COINS[days] ?: continue
            val granted = grantAndNotify(
                DedupeKeys.streakMilestone(days), RewardType.STREAK_MILESTONE, bonus, 0,
            )
            if (granted) _events.tryEmit(GamificationEvent.StreakMilestoneReached(days, bonus))
        }
        return advance.newStreak
    }

    /**
     * Unlocks every catalog achievement whose predicate now passes. Safe to
     * call after any event: unlock rows + ledger keys dedupe re-entry, and
     * predicates are monotonic so stale context can only delay an unlock.
     */
    private suspend fun evaluateAchievements() {
        val ctx = repo.buildAchievementContext()
        val unlockedIds = repo.getUnlockedIdsOnce().toSet()
        for (achievement in AchievementCatalog.ALL) {
            if (achievement.id in unlockedIds || !achievement.isUnlocked(ctx)) continue
            val outcome = repo.tryUnlockAchievement(achievement) ?: continue
            _events.tryEmit(GamificationEvent.AchievementUnlocked(achievement))
            emitRewardEvents(outcome, achievement.coinReward, achievement.xpReward)
        }
    }
}
