package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.database.model.MarketItemEntity
import com.niyaj.database.model.MarketItemWithQuantityDto
import com.niyaj.database.model.MarketListEntity
import com.niyaj.database.model.MarketListWithItemEntity
import com.niyaj.database.model.MarketListWithItemsDto
import com.niyaj.model.ItemQuantityAndType
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketListDao {

    @Transaction
    @Query(
        value = """
        SELECT * FROM market_list ORDER BY createdAt DESC
    """
    )
    fun getAllMarketLists(): Flow<List<MarketListWithItemsDto>>

    @Query(
        value = """
        SELECT * FROM market_list WHERE marketId = :marketId
    """
    )
    fun getMarketListById(marketId: Int): Flow<MarketListEntity?>

    @Query(
        value = """
        SELECT * FROM market_item ORDER BY createdAt DESC
    """
    )
    fun getMarketItems(): Flow<List<MarketItemEntity>>


    @Query(
        value = """
        SELECT itemQuantity, marketListType FROM market_list_with_item WHERE marketId = :marketId AND itemId = :itemId
    """
    )
    fun getItemQuantityAndType(marketId: Int, itemId: Int): Flow<ItemQuantityAndType?>


    /**
     * Inserts [MarketListEntity] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreMarketList(marketList: MarketListEntity): Long

    /**
     * Updates [MarketListEntity] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateMarketList(marketList: MarketListEntity): Int

    /**
     * Inserts or updates [MarketListEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertMarketList(marketList: MarketListEntity): Long

    @Query(
        value = """
                DELETE FROM market_list WHERE marketId = :marketId
        """
    )
    suspend fun deleteMarketList(marketId: Int): Int

    /**
     * Deletes rows in the db matching the specified [marketIds]
     */
    @Query(
        value = """
                DELETE FROM market_list
                WHERE marketId in(:marketIds)
        """,
    )
    suspend fun deleteMarketLists(marketIds: List<Int>): Int

    @Query("""
        SELECT * FROM market_list_with_item WHERE marketId = :marketId
    """)
    fun getItemsByMarketId(marketId: Int): Flow<List<MarketListWithItemEntity>>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("""
        SELECT * FROM market_item JOIN market_list_with_item ON market_item.itemId is market_list_with_item.itemId WHERE marketId = :marketId
    """)
    fun getItemsWithQuantityByMarketId(marketId: Int): Flow<List<MarketItemWithQuantityDto>>


    @Query("""
        SELECT itemQuantity FROM market_list_with_item WHERE marketId = :marketId AND itemId = :itemId
    """)
    suspend fun getItemQuantityByMarketIdAndItemId(marketId: Int, itemId: Int): Double?

    @Query("""
        SELECT unitValue FROM market_item WHERE itemId = :itemId
    """)
    suspend fun getItemMeasureUnitValueItemId(itemId: Int): Double?


    @Query("""
        SELECT itemQuantity, marketListType FROM market_list_with_item WHERE marketId = :marketId AND itemId = :itemId
    """)
    suspend fun findItemByMarketIdAndItemId(marketId: Int, itemId: Int): ItemQuantityAndType?

    @Query("""
        SELECT listId FROM market_list_with_item WHERE marketId = :marketId AND itemId = :itemId
    """)
    fun findItemIdByMarketIdAndItemId(marketId: Int, itemId: Int): Flow<Int?>

    /**
     * Inserts [MarketListWithItemEntity] into the db if they don't exist, and ignores those that do
     */
    @Insert(entity = MarketListWithItemEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreMarketListWithItem(item: MarketListWithItemEntity): Long

    /**
     * Inserts or updates [MarketListWithItemEntity] in the db under the specified primary keys
     */
    @Upsert(entity = MarketListWithItemEntity::class)
    suspend fun upsertMarketListWithItem(item: MarketListWithItemEntity): Long

    /**
     * Inserts or updates [MarketListWithItemEntity] in the db under the specified primary keys
     */
    @Upsert(entity = MarketListWithItemEntity::class)
    suspend fun upsertMarketListsWithItem(items: List<MarketListWithItemEntity>): List<Long>

    @Query(
        """
            UPDATE market_list_with_item SET itemQuantity = :quantity WHERE marketId = :marketId AND itemId = :itemId
        """
    )
    suspend fun updateMarketListWithItemQuantity(marketId: Int, itemId: Int, quantity: Double): Int

    @Query(
        """
            DELETE FROM market_list_with_item WHERE marketId = :marketId AND itemId = :itemId
        """
    )
    suspend fun deleteMarketListWithItem(marketId: Int, itemId: Int): Int
}