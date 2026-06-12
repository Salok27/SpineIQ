package noshtek.back_pain_prototype.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import noshtek.back_pain_prototype.core.data.gamification.RewardType

/**
 * Append-only record of every coin/XP grant and spend. The [dedupeKey]
 * primary key (formats owned by DedupeKeys) is the idempotency mechanism:
 * grants insert with OnConflictStrategy.IGNORE, and a -1 row id means the
 * reward was already given — re-saving a wizard section, retrying a
 * completion, or double-tapping a purchase can never pay out twice.
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

    /** Negative for purchases. */
    @ColumnInfo(name = "coins_delta")
    val coinsDelta: Int,

    @ColumnInfo(name = "xp_delta")
    val xpDelta: Int,

    /** Epoch millis. */
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
