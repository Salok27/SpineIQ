package noshtek.back_pain_prototype.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import noshtek.back_pain_prototype.core.data.gamification.AvatarCategory

/**
 * Owned/equipped state for one purchased cosmetic. Item definitions live in
 * the static AvatarCatalog keyed by [itemId]; free default items have no row
 * and are treated as implicitly owned. At most one item per [category] is
 * equipped (the category is denormalized from the catalog at purchase time so
 * unequip-category is a single UPDATE).
 */
@Entity(
    tableName = "avatar_items",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("user_id"), Index("category")]
)
data class AvatarItemEntity(

    @PrimaryKey
    @ColumnInfo(name = "item_id")
    val itemId: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "category")
    val category: AvatarCategory,

    @ColumnInfo(name = "equipped")
    val equipped: Boolean = false,

    /** Epoch millis. */
    @ColumnInfo(name = "purchased_at")
    val purchasedAt: Long
)
