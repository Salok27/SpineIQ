package noshtek.back_pain_prototype.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Unlock state for one achievement. Definitions (titles, rewards, predicates)
 * live in the static AchievementCatalog keyed by [achievementId]; only the
 * unlock fact is persisted, so the catalog can grow without schema changes.
 */
@Entity(
    tableName = "achievement_unlocks",
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
data class AchievementUnlockEntity(

    @PrimaryKey
    @ColumnInfo(name = "achievement_id")
    val achievementId: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    /** Epoch millis. */
    @ColumnInfo(name = "unlocked_at")
    val unlockedAt: Long
)
