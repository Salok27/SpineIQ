package noshtek.back_pain_prototype.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Single-row engagement state for the one local user: the cached Living Spine
 * vitality and its monotonic peak, plus the streak counters. Vitality is
 * recomputed (pure) from the latest clinical scores + recent habit adherence on
 * every qualifying event and cached here so the UI reads it directly. Cascade
 * delete from the user profile wipes this with the rest of the data.
 */
@Entity(
    tableName = "gamification_state",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("user_id")]
)
data class GamificationStateEntity(

    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,

    /** Latest computed Spine Vitality (0..100). */
    @ColumnInfo(name = "latest_vitality")
    val latestVitality: Int = 50,

    /** Monotonic high-water mark of vitality — drives VITALITY milestones. */
    @ColumnInfo(name = "peak_vitality")
    val peakVitality: Int = 0,

    @ColumnInfo(name = "current_streak_days")
    val currentStreakDays: Int = 0,

    @ColumnInfo(name = "longest_streak_days")
    val longestStreakDays: Int = 0,

    /** Epoch day of the last streak-qualifying event (check-in, ritual, or completion). */
    @ColumnInfo(name = "last_activity_day")
    val lastActivityDay: Long? = null,

    /** Epoch millis. */
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
