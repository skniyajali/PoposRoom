/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.niyaj.database.model.MarketItemDto
import com.niyaj.database.model.MarketItemEntity
import com.niyaj.database.model.MeasureUnitEntity
import com.niyaj.model.MarketTypeIdAndName
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketItemDao {

    @Transaction
    @Query(
        value = """
        SELECT * FROM market_item ORDER BY createdAt DESC
    """
    )
    fun getAllMarketItems(): Flow<List<MarketItemDto>>

    @Query(
        value = """
        SELECT * FROM measure_unit ORDER BY unitId DESC
    """
    )
    fun getAllMeasureUnits(): Flow<List<MeasureUnitEntity>>

    @Transaction
    @Query(
        value = """
        SELECT * FROM market_item WHERE itemId = :itemId
    """
    )
    fun getMarketItemById(itemId: Int): MarketItemDto?

    @Query(
        value = """
            SELECT typeId, typeName FROM market_type ORDER BY createdAt DESC
        """
    )
    fun getAllItemTypes(): Flow<List<MarketTypeIdAndName>>

    /**
     * Inserts or updates [MarketItemEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertMarketItem(marketItem: MarketItemEntity): Long


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
}