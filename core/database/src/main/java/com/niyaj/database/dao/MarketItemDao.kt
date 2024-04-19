package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.database.model.MarketItemEntity
import com.niyaj.database.model.MeasureUnitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketItemDao {

    @Query(
        value = """
        SELECT * FROM market_item ORDER BY createdAt DESC
    """
    )
    fun getAllMarketItems(): Flow<List<MarketItemEntity>>

    @Query(
        value = """
        SELECT * FROM measure_unit
    """
    )
    fun getAllMeasureUnits(): Flow<List<MeasureUnitEntity>>

    @Query(
        value = """
        SELECT * FROM market_item WHERE itemId = :itemId
    """
    )
    fun getMarketItemById(itemId: Int): MarketItemEntity?

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
    suspend fun insertOrIgnoreMarketItem(marketItem: MarketItemEntity): Long


    /**
     * Updates [MarketItemEntity] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateMarketItem(marketItem: MarketItemEntity): Int

    /**
     * Inserts or updates [MarketItemEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertMarketItem(marketItem: MarketItemEntity): Long

    @Query(
        value = """
                DELETE FROM market_item WHERE itemId = :itemId
        """
    )
    suspend fun deleteMarketItem(itemId: Int): Int

    /**
     * Deletes rows in the db matching the specified [itemIds]
     */
    @Query(
        value = """
                DELETE FROM market_item
                WHERE itemId in(:itemIds)
        """,
    )
    suspend fun deleteMarketItems(itemIds: List<Int>): Int

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


    /**
     * Inserts or updates [MeasureUnitEntity] in the db under the specified primary keys
     */
    @Upsert(entity = MeasureUnitEntity::class)
    suspend fun upsertMeasureUnit(measureUnit: MeasureUnitEntity): Long

    @Query(
        value = """
        SELECT * FROM measure_unit WHERE unitId = :unitId
    """
    )
    fun getMeasureUnitById(unitId: Int): MeasureUnitEntity?

    @Query(
        value = """
            SELECT * FROM measure_unit WHERE unitId = :unitId OR unitName = :unitName
        """
    )
    fun findMeasureUnitByIdOrName(unitId: Int, unitName: String): MeasureUnitEntity?


    @Query(
        """
        SELECT whitelistItems FROM market_list WHERE marketId = :marketId
    """
    )
    suspend fun getWhitelistItems(marketId: Int): String

    @Query(
        """
        UPDATE market_list SET whitelistItems = :whitelistItems  WHERE marketId = :marketId
    """
    )
    suspend fun updateWhiteListItems(marketId: Int, whitelistItems: String): Int
}