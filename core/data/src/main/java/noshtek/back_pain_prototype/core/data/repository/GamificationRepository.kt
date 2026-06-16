package noshtek.back_pain_prototype.core.data.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import noshtek.back_pain_prototype.core.data.db.SpineIQDatabase
import noshtek.back_pain_prototype.core.data.db.dao.GamificationDao
import noshtek.back_pain_prototype.core.data.db.dao.UserProfileDao
import noshtek.back_pain_prototype.core.data.db.entity.AchievementUnlockEntity
import noshtek.back_pain_prototype.core.data.db.entity.DailyCheckInEntity
import noshtek.back_pain_prototype.core.data.db.entity.GamificationStateEntity
import noshtek.back_pain_prototype.core.data.db.entity.RewardLedgerEntity
import noshtek.back_pain_prototype.core.data.db.entity.RitualCompletionEntity
import noshtek.back_pain_prototype.core.data.db.entity.ScoresRecordEntity
import noshtek.back_pain_prototype.core.data.gamification.CheckInMood
import noshtek.back_pain_prototype.core.data.gamification.ClinicalInputs
import noshtek.back_pain_prototype.core.data.gamification.DedupeKeys
import noshtek.back_pain_prototype.core.data.gamification.Milestone
import noshtek.back_pain_prototype.core.data.gamification.MilestoneContext
import noshtek.back_pain_prototype.core.data.gamification.RewardType
import noshtek.back_pain_prototype.core.data.gamification.StreakAdvance
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Transactional persistence for the Aura engagement layer. Every event is
 * logged to the reward ledger (idempotent via its dedupe-key PK), and durable
 * state (vitality, streak, unlocks, ritual completions) changes inside Room
 * transactions. All engagement tables cascade-delete from user_profiles, so the
 * existing "delete all data" flow wipes them with no extra code. This layer only
 * READS clinical scores — it never writes them, never gates them.
 */
@Singleton
class GamificationRepository @Inject constructor(
    private val db: SpineIQDatabase,
    private val gamificationDao: GamificationDao,
    private val userProfileDao: UserProfileDao,
) {

    // ── Reads ─────────────────────────────────────────────────────────────────

    fun observeState(): Flow<GamificationStateEntity?> = gamificationDao.observeState()

    suspend fun getStateOnce(): GamificationStateEntity? = gamificationDao.getStateOnce()

    fun observeUnlocks(): Flow<List<AchievementUnlockEntity>> = gamificationDao.observeUnlocks()

    fun observeCheckInHistory(): Flow<List<DailyCheckInEntity>> = gamificationDao.observeCheckInHistory()

    fun observeRitualCompletions(): Flow<List<RitualCompletionEntity>> =
        gamificationDao.observeRitualCompletions()

    /** Latest clinical picture (mapped from the most recent stored scores), reactive. */
    fun observeLatestClinical(): Flow<ClinicalInputs?> =
        gamificationDao.observeMostRecentScores().map { it?.toClinicalInputs() }

    suspend fun latestClinicalOnce(): ClinicalInputs? =
        gamificationDao.getMostRecentScores()?.toClinicalInputs()

    suspend fun getUnlockedIdsOnce(): List<String> = gamificationDao.getUnlockedIdsOnce()

    suspend fun isCheckedInOn(epochDay: Long): Boolean =
        gamificationDao.countCheckInForDay(epochDay) > 0

    suspend fun countRitualCompletionsForDay(epochDay: Long): Int =
        gamificationDao.countRitualCompletionsForDay(epochDay)

    suspend fun ritualCompletionsInRange(fromDay: Long, toDay: Long): Int =
        gamificationDao.countRitualCompletionsInRange(fromDay, toDay)

    // ── Idempotent event log ──────────────────────────────────────────────────

    /** Append one ledger event. Returns true only when newly inserted (not deduped). */
    suspend fun tryLogEvent(dedupeKey: String, type: RewardType, meta: Int? = null): Boolean {
        val userId = currentUserId() ?: return false
        return gamificationDao.insertLedgerEntry(
            RewardLedgerEntity(dedupeKey, userId, type, meta, Instant.now().toEpochMilli())
        ) != -1L
    }

    /** Returns false when a check-in for that day already exists or no profile exists. */
    suspend fun tryRecordCheckIn(epochDay: Long, mood: CheckInMood): Boolean {
        val userId = currentUserId() ?: return false
        return gamificationDao.insertCheckIn(
            DailyCheckInEntity(epochDay, userId, mood, Instant.now().toEpochMilli())
        ) != -1L
    }

    /** Records a ritual completion + its ledger event in one transaction. True only if newly inserted. */
    suspend fun tryCompleteRitual(ritualId: String, epochDay: Long): Boolean {
        val userId = currentUserId() ?: return false
        return db.withTransaction {
            val now = Instant.now().toEpochMilli()
            val inserted = gamificationDao.insertRitualCompletion(
                RitualCompletionEntity(ritualId, epochDay, userId, now)
            )
            if (inserted == -1L) return@withTransaction false
            gamificationDao.insertLedgerEntry(
                RewardLedgerEntity(DedupeKeys.ritual(ritualId, epochDay), userId, RewardType.RITUAL, null, now)
            )
            true
        }
    }

    suspend fun applyStreakAdvance(advance: StreakAdvance, today: LocalDate) {
        val userId = currentUserId() ?: return
        db.withTransaction {
            val state = getOrCreateState(userId)
            gamificationDao.upsertState(
                state.copy(
                    currentStreakDays = advance.newStreak,
                    longestStreakDays = advance.newLongest,
                    lastActivityDay = today.toEpochDay(),
                    updatedAt = Instant.now().toEpochMilli(),
                )
            )
        }
    }

    /** Persists the recomputed vitality and bumps the monotonic peak. */
    suspend fun applyVitality(vitality: Int) {
        val userId = currentUserId() ?: return
        db.withTransaction {
            val state = getOrCreateState(userId)
            gamificationDao.upsertState(
                state.copy(
                    latestVitality = vitality,
                    peakVitality = maxOf(state.peakVitality, vitality),
                    updatedAt = Instant.now().toEpochMilli(),
                )
            )
        }
    }

    /** Unlock + ledger event in one transaction. Returns true only when newly unlocked. */
    suspend fun tryUnlockMilestone(milestone: Milestone): Boolean {
        val userId = currentUserId() ?: return false
        return db.withTransaction {
            val now = Instant.now().toEpochMilli()
            val inserted = gamificationDao.insertUnlock(
                AchievementUnlockEntity(milestone.id, userId, now)
            )
            if (inserted == -1L) return@withTransaction false
            gamificationDao.insertLedgerEntry(
                RewardLedgerEntity(DedupeKeys.milestone(milestone.id), userId, RewardType.MILESTONE, null, now)
            )
            true
        }
    }

    suspend fun buildMilestoneContext(): MilestoneContext {
        val state = gamificationDao.getStateOnce()
        return MilestoneContext(
            completedAssessmentCount = gamificationDao.countLedgerEntriesMatching(DedupeKeys.COMPLETION_PATTERN),
            currentStreak = state?.currentStreakDays ?: 0,
            longestStreak = state?.longestStreakDays ?: 0,
            checkInCount = gamificationDao.getCheckInCount(),
            totalRitualCompletions = gamificationDao.getRitualCompletionCount(),
            peakVitality = state?.peakVitality ?: 0,
        )
    }

    // ── Internals ─────────────────────────────────────────────────────────────

    private suspend fun currentUserId(): String? =
        userProfileDao.getUserProfile().firstOrNull()?.id

    private suspend fun getOrCreateState(userId: String): GamificationStateEntity =
        gamificationDao.getStateOnce()
            ?: GamificationStateEntity(userId = userId, updatedAt = Instant.now().toEpochMilli())

    private fun ScoresRecordEntity.toClinicalInputs() = ClinicalInputs(
        severityTier = sssSeverityTier,
        lifestyleRisk = lifestyleRiskTier,
        classification = backPainRiskClassification,
        sittingRisk = sittingRisk,
        walkingRisk = walkingRisk,
        exerciseRisk = exerciseRisk,
        sleepRisk = sleepRisk,
        sleepQuality = sleepQualityModifier,
        bmiCategory = bmiCategory,
    )
}
