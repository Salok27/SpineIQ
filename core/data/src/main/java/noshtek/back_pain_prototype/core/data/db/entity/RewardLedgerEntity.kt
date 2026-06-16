package noshtek.back_pain_prototype.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import noshtek.back_pain_prototype.core.data.gamification.RewardType

/**
 * Append-only log of every engagement event (step, completion, check-in,
 * ritual, streak milestone, milestone unlock). The [dedupeKey] primary key
 * (formats owned by DedupeKeys) is the idempotency mechanism: events insert
 * with OnConflictStrategy.IGNORE, and a -1 row id means it already happened —
 * re-saving a wizard section, retrying a completion, or double-tapping a ritual
 * can never count twice. Doubles as the count source for milestone progress.
 */
@Entity(
    tableName = "reward_ledger",
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
data class RewardLedgerEntity(

    @PrimaryKey
    @ColumnInfo(name = "dedupe_key")
    val dedupeKey: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "reward_type")
    val rewardType: RewardType,

    /** Optional informational payload (e.g. the streak day count at the event). */
    @ColumnInfo(name = "meta")
    val meta: Int? = null,

    /** Epoch millis. */
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
