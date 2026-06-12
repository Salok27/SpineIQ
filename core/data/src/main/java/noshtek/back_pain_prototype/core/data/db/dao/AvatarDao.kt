package noshtek.back_pain_prototype.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import noshtek.back_pain_prototype.core.data.db.entity.AvatarItemEntity
import noshtek.back_pain_prototype.core.data.gamification.AvatarCategory

@Dao
interface AvatarDao {

    /** Returns -1 when the item is already owned. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOwned(item: AvatarItemEntity): Long

    @Query("SELECT * FROM avatar_items")
    fun observeOwned(): Flow<List<AvatarItemEntity>>

    @Query("SELECT COUNT(*) FROM avatar_items")
    suspend fun getOwnedCount(): Int

    @Query("UPDATE avatar_items SET equipped = 0 WHERE category = :category")
    suspend fun unequipCategory(category: AvatarCategory)

    @Query("UPDATE avatar_items SET equipped = 1 WHERE item_id = :itemId")
    suspend fun equip(itemId: String)

    @Query("UPDATE avatar_items SET equipped = 0 WHERE item_id = :itemId")
    suspend fun unequip(itemId: String)
}
