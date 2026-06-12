package noshtek.back_pain_prototype.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Single-row gamification wallet/streak state for the one local user.
 * The level is never stored — it is derived from [xp] via LevelTable so the
 * progression curve can be retuned without a migration. Cascade delete from
 * the user profile wipes this with the rest of the data (Section 15.3).
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

    /** Spendable Spine Coins balance, never negative. */
    @ColumnInfo(name = "coins")
    val coins: Int = 0,

    /** Lifetime XP, monotonic — levels are derived from this. */
    @ColumnInfo(name = "xp")
    val xp: Int = 0,

    @ColumnInfo(name = "current_streak_days")
    val currentStreakDays: Int = 0,

    @ColumnInfo(name = "longest_streak_days")
    val longestStreakDays: Int = 0,

    /** Epoch day of the last streak-qualifying event (check-in or assessment completion). */
    @ColumnInfo(name = "last_activity_day")
    val lastActivityDay: Long? = null,

    /** Epoch millis. */
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
