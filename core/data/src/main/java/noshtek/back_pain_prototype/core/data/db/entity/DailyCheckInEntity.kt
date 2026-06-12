package noshtek.back_pain_prototype.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import noshtek.back_pain_prototype.core.data.gamification.CheckInMood

/**
 * One mood check-in per calendar day — the epoch-day primary key enforces
 * the once-per-day rule at the schema level. Rows are kept (not just a
 * latest-day field) to power mood-trend history and week-dot displays.
 */
@Entity(
    tableName = "daily_checkins",
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
data class DailyCheckInEntity(

    /** Epoch day (device-local calendar date). */
    @PrimaryKey
    @ColumnInfo(name = "check_in_day")
    val checkInDay: Long,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "mood")
    val mood: CheckInMood,

    /** Epoch millis. */
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
