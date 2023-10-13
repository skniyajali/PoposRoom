package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.database.model.MarketListEntity
import com.niyaj.database.model.MarketListWithItemEntity
import com.niyaj.database.model.MarketListWithItemsDto
import com.niyaj.model.MarketListWithItem
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

    @Transaction
    @Query(
        value = """
        SELECT * FROM market_list WHERE marketId = :marketId
    """
    )
    fun getMarketListById(marketId: Int): MarketListWithItemsDto?

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


    @Query("""
        SELECT * FROM market_list_with_item WHERE marketId = :marketId AND itemId = :itemId
    """)
    fun getItemByMarketIdAndItemId(marketId: Int, itemId: Int): MarketListWithItemEntity?

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