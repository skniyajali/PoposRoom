package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.database.model.MarketListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketListDao {

    @Query(
        value = """
        SELECT * FROM market_list ORDER BY createdAt DESC
    """
    )
    fun getAllMarketLists(): Flow<List<MarketListEntity>>

    @Query(
        value = """
        SELECT * FROM market_list WHERE itemId = :itemId
    """
    )
    fun getMarketListById(itemId: Int): MarketListEntity?

    @Query(
        value = """
            SELECT itemType FROM market_list ORDER BY createdAt DESC
        """
    )
    fun getAllItemTypes(): Flow<List<String>>

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
                DELETE FROM market_list WHERE itemId = :itemId
        """
    )
    suspend fun deleteMarketList(itemId: Int): Int

    /**
     * Deletes rows in the db matching the specified [itemIds]
     */
    @Query(
        value = """
                DELETE FROM market_list
                WHERE itemId in(:itemIds)
        """,
    )
    suspend fun deleteMarketLists(itemIds: List<Int>): Int

    @Query(
        value = """
                SELECT itemId FROM market_list WHERE
                CASE WHEN :itemId IS NULL OR :itemId = 0
    THEN itemName = :itemName
    ELSE itemId != :itemId AND itemName = :itemName
    END LIMIT 1
    """
    )
    fun findItemByName(itemName: String, itemId: Int?): Int?
}