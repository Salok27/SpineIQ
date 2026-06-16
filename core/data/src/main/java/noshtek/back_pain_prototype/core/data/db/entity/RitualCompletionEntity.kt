package noshtek.back_pain_prototype.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * One completion of one ritual on one calendar day. The composite primary key
 * (ritual_id, completion_day) enforces the once-per-day-per-ritual rule at the
 * schema level — the same idempotency philosophy as the daily-checkins epoch-day
 * key. Ritual definitions live in the static RitualCatalog keyed by [ritualId].
 */
@Entity(
    tableName = "ritual_completions",
    primaryKeys = ["ritual_id", "completion_day"],
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("user_id"), Index("completion_day")]
)
data class RitualCompletionEntity(

    @ColumnInfo(name = "ritual_id")
    val ritualId: String,

    /** Epoch day (device-local calendar date). */
    @ColumnInfo(name = "completion_day")
    val completionDay: Long,

    @ColumnInfo(name = "user_id")
    val userId: String,

    /** Epoch millis. */
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
)
