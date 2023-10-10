package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.database.model.MarketItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketItemDao {

    @Query(
        value = """
        SELECT * FROM market_item ORDER BY createdAt DESC
    """
    )
    fun getAllMarketLists(): Flow<List<MarketItemEntity>>

    @Query(
        value = """
        SELECT * FROM market_item WHERE itemId = :itemId
    """
    )
    fun getMarketListById(itemId: Int): MarketItemEntity?

    @Query(
        value = """
            SELECT DISTINCT itemType FROM market_item ORDER BY createdAt DESC
        """
    )
    fun getAllItemTypes(): Flow<List<String>>

    /**
     * Inserts [MarketItemEntity] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreMarketList(marketList: MarketItemEntity): Long

    /**
     * Updates [MarketItemEntity] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateMarketList(marketList: MarketItemEntity): Int

    /**
     * Inserts or updates [MarketItemEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertMarketList(marketList: MarketItemEntity): Long

    @Query(
        value = """
                DELETE FROM market_item WHERE itemId = :itemId
        """
    )
    suspend fun deleteMarketList(itemId: Int): Int

    /**
     * Deletes rows in the db matching the specified [itemIds]
     */
    @Query(
        value = """
                DELETE FROM market_item
                WHERE itemId in(:itemIds)
        """,
    )
    suspend fun deleteMarketLists(itemIds: List<Int>): Int

    @Query(
        value = """
                SELECT itemId FROM market_item WHERE
                CASE WHEN :itemId IS NULL OR :itemId = 0
    THEN itemName = :itemName
    ELSE itemId != :itemId AND itemName = :itemName
    END LIMIT 1
    """
    )
    fun findItemByName(itemName: String, itemId: Int?): Int?
}