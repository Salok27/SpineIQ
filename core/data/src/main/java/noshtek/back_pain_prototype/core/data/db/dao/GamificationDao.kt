package noshtek.back_pain_prototype.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import noshtek.back_pain_prototype.core.data.db.entity.AchievementUnlockEntity
import noshtek.back_pain_prototype.core.data.db.entity.DailyCheckInEntity
import noshtek.back_pain_prototype.core.data.db.entity.GamificationStateEntity
import noshtek.back_pain_prototype.core.data.db.entity.RewardLedgerEntity
import noshtek.back_pain_prototype.core.data.db.entity.RitualCompletionEntity
import noshtek.back_pain_prototype.core.data.db.entity.ScoresRecordEntity

@Dao
interface GamificationDao {

    // ── Vitality / streak state (single row) ──────────────────────────────────

    @Query("SELECT * FROM gamification_state LIMIT 1")
    fun observeState(): Flow<GamificationStateEntity?>

    @Query("SELECT * FROM gamification_state LIMIT 1")
    suspend fun getStateOnce(): GamificationStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertState(state: GamificationStateEntity)

    // ── Reward ledger (idempotency + audit + milestone counts) ────────────────

    /** Returns -1 when [RewardLedgerEntity.dedupeKey] already exists — the event already happened. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLedgerEntry(entry: RewardLedgerEntity): Long

    /** Counts events whose dedupe key matches a SQL LIKE [pattern] (see DedupeKeys). */
    @Query("SELECT COUNT(*) FROM reward_ledger WHERE dedupe_key LIKE :pattern")
    suspend fun countLedgerEntriesMatching(pattern: String): Int

    // ── Milestone unlocks (stored in achievement_unlocks, keyed by milestone id) ─

    /** Returns -1 when the milestone is already unlocked. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUnlock(unlock: AchievementUnlockEntity): Long

    @Query("SELECT * FROM achievement_unlocks ORDER BY unlocked_at DESC")
    fun observeUnlocks(): Flow<List<AchievementUnlockEntity>>

    @Query("SELECT achievement_id FROM achievement_unlocks")
    suspend fun getUnlockedIdsOnce(): List<String>

    // ── Daily check-ins ───────────────────────────────────────────────────────

    /** Returns -1 when a check-in for that day already exists. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCheckIn(checkIn: DailyCheckInEntity): Long

    @Query("SELECT * FROM daily_checkins ORDER BY check_in_day DESC")
    fun observeCheckInHistory(): Flow<List<DailyCheckInEntity>>

    @Query("SELECT COUNT(*) FROM daily_checkins")
    suspend fun getCheckInCount(): Int

    @Query("SELECT COUNT(*) FROM daily_checkins WHERE check_in_day = :day")
    suspend fun countCheckInForDay(day: Long): Int

    // ── Ritual completions ────────────────────────────────────────────────────

    /** Returns -1 when this ritual was already completed on this day. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRitualCompletion(completion: RitualCompletionEntity): Long

    @Query("SELECT * FROM ritual_completions ORDER BY completion_day DESC")
    fun observeRitualCompletions(): Flow<List<RitualCompletionEntity>>

    @Query("SELECT COUNT(*) FROM ritual_completions")
    suspend fun getRitualCompletionCount(): Int

    @Query("SELECT COUNT(*) FROM ritual_completions WHERE completion_day = :day")
    suspend fun countRitualCompletionsForDay(day: Long): Int

    @Query("SELECT COUNT(*) FROM ritual_completions WHERE completion_day BETWEEN :fromDay AND :toDay")
    suspend fun countRitualCompletionsInRange(fromDay: Long, toDay: Long): Int

    // ── Latest clinical scores (read-only; powers vitality + ritual selection) ──

    @Query("SELECT * FROM scores_records ORDER BY computed_at DESC LIMIT 1")
    fun observeMostRecentScores(): Flow<ScoresRecordEntity?>

    @Query("SELECT * FROM scores_records ORDER BY computed_at DESC LIMIT 1")
    suspend fun getMostRecentScores(): ScoresRecordEntity?
}
