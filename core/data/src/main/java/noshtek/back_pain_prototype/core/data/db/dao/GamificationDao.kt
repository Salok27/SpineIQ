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

@Dao
interface GamificationDao {

    // ── Wallet / streak state (single row) ────────────────────────────────────

    @Query("SELECT * FROM gamification_state LIMIT 1")
    fun observeState(): Flow<GamificationStateEntity?>

    @Query("SELECT * FROM gamification_state LIMIT 1")
    suspend fun getStateOnce(): GamificationStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertState(state: GamificationStateEntity)

    // ── Reward ledger (idempotency + audit) ───────────────────────────────────

    /** Returns -1 when [RewardLedgerEntity.dedupeKey] already exists — the grant already happened. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLedgerEntry(entry: RewardLedgerEntity): Long

    /** Counts grants whose dedupe key matches a SQL LIKE [pattern] (see DedupeKeys.stepPattern). */
    @Query("SELECT COUNT(*) FROM reward_ledger WHERE dedupe_key LIKE :pattern")
    suspend fun countLedgerEntriesMatching(pattern: String): Int

    // ── Achievement unlocks ───────────────────────────────────────────────────

    /** Returns -1 when the achievement is already unlocked. */
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

    @Query("SELECT * FROM daily_checkins WHERE check_in_day = :epochDay")
    fun observeCheckInForDay(epochDay: Long): Flow<DailyCheckInEntity?>

    @Query("SELECT * FROM daily_checkins ORDER BY check_in_day DESC")
    fun observeCheckInHistory(): Flow<List<DailyCheckInEntity>>

    @Query("SELECT COUNT(*) FROM daily_checkins")
    suspend fun getCheckInCount(): Int
}
