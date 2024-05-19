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
import com.niyaj.model.MarketItemAndQuantity
import com.niyaj.model.MarketListAndType
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketListWIthItemsDao {

    @Transaction
    @Query(
        value = """
            SELECT mi.itemName, mi.itemPrice, mi.itemId, mu.unitName, mu.unitValue,
            mt.typeName, mlt.listWithTypeId, COALESCE(mil.itemQuantity, null) as itemQuantity, 
            COALESCE(mlt.listType, "") as listType
            
            FROM market_item mi
            JOIN measure_unit mu ON mi.unitId = mu.unitId
            JOIN market_type mt ON mi.typeId = mt.typeId
            JOIN market_list_with_type mlt ON mlt.listWithTypeId = :listTypeId
            LEFT JOIN market_list_with_items mil ON mi.itemId = mil.itemId AND mil.listWithTypeId = :listTypeId
            
            WHERE mi.typeId = mlt.typeId
            ORDER BY mi.createdAt DESC
    """,
    )
    fun getAllMarketItemsById(listTypeId: Int): Flow<List<MarketItemAndQuantity>>

    @Transaction
    @Query(
        value = """
            SELECT mi.itemName, mi.itemPrice, mi.itemId, mu.unitName, mu.unitValue,
            mt.typeName, mlt.listWithTypeId, COALESCE(mil.itemQuantity, null) as itemQuantity, 
            COALESCE(mlt.listType, "") as listType
            
            FROM market_item mi
            JOIN measure_unit mu ON mi.unitId = mu.unitId
            JOIN market_type mt ON mi.typeId = mt.typeId
            JOIN market_list_with_type mlt ON mlt.listWithTypeId IN (:listTypeIds)
            LEFT JOIN market_list_with_items mil ON mi.itemId = mil.itemId AND mil.listWithTypeId IN (:listTypeIds)
            
            WHERE mi.typeId = mlt.typeId
            ORDER BY mi.createdAt DESC

    """,
    )
    fun getAllMarketItemsByIds(listTypeIds: List<Int>): Flow<List<MarketItemAndQuantity>>

    @Transaction
    @Query(
        value = """
            SELECT mi.itemName, mi.itemPrice, mi.itemId, mu.unitName, mu.unitValue,
            mt.typeName, mlt.listWithTypeId, COALESCE(mil.itemQuantity, null) as itemQuantity, 
            COALESCE(mlt.listType, "") as listType
            
            FROM  market_list_with_items mil
            JOIN market_list_with_type mlt ON mlt.listWithTypeId = :listTypeId
            JOIN measure_unit mu ON mu.unitId = mi.unitId
            JOIN market_type mt ON mt.typeId = mi.typeId
            LEFT JOIN market_item mi ON mi.itemId = mil.itemId
            
            WHERE mil.listWithTypeId = :listTypeId AND mi.typeId = mlt.typeId
            ORDER BY mi.createdAt DESC
    """,
    )
    fun getShareableMarketItems(listTypeId: Int): Flow<List<MarketItemAndQuantity>>

    @Transaction
    @Query(
        value = """
            SELECT mi.itemName, mi.itemPrice, mi.itemId, mu.unitName, mu.unitValue,
            mt.typeName, mlt.listWithTypeId, COALESCE(mil.itemQuantity, null) as itemQuantity, COALESCE(mlt.listType, "") as listType

            FROM  market_item mi 
            JOIN measure_unit mu ON mu.unitId = mi.unitId
            JOIN market_type mt ON mt.typeId = mi.typeId
            LEFT JOIN market_list_with_type mlt ON mlt.listWithTypeId = mil.listWithTypeId
            LEFT JOIN  market_list_with_items mil ON  mil.itemId = mi.itemId AND mil.listWithTypeId IN (:listTypeIds)
            
            WHERE mil.listWithTypeId IN (:listTypeIds) AND mi.typeId = mlt.typeId
            ORDER BY mi.createdAt DESC
    """,
    )
    fun getShareableMarketItems(listTypeIds: List<Int>): Flow<List<MarketItemAndQuantity>>

    @Transaction
    @Query(
        value = """
            SELECT ml.marketId, ml.marketDate, ml.createdAt, 
            mlt.typeId, mt.typeName, mlt.listWithTypeId, mlt.listType, ml.updatedAt
            FROM market_list ml
            JOIN market_list_with_type mlt ON mlt.listWithTypeId = :listTypeId
            JOIN market_type mt ON mlt.typeId = mt.typeId
            WHERE ml.marketId = mlt.marketId
        """
    )
    fun getMarketDetailsById(listTypeId: Int): Flow<MarketListAndType>

    @Query(
        value = """
            SELECT mu.unitValue
            FROM market_item mi
            JOIN measure_unit mu ON mi.unitId = mu.unitId
            WHERE mi.itemId = :itemId
        """
    )
    fun getUnitValueByItemId(itemId: Int): Double

    @Query(
        value = """
            SELECT itemQuantity
            FROM market_list_with_items
            WHERE listWithTypeId = :listId AND itemId = :itemId
        """
    )
    fun getItemQuantityByListIdItemId(listId: Int, itemId: Int): Double?

    @Query(
        value = """
            UPDATE market_list_with_items
            SET itemQuantity = :itemQuantity
            WHERE listWithTypeId = :listId AND itemId = :itemId
        """
    )
    fun updateItemQuantityByListIdItemId(listId: Int, itemId: Int, itemQuantity: Double): Int

    @Query(
        value = """
            SELECT listWithTypeId
            FROM market_list_with_items
            WHERE listWithTypeId = :listId AND itemId = :itemId
        """
    )
    fun findMarketListItem(listId: Int, itemId: Int): Int?

    @Query(
        value = """
            INSERT INTO market_list_with_items (listWithTypeId, itemId, itemQuantity)
            VALUES (:listId, :itemId, :itemQuantity)
        """
    )
    fun addMarketListItem(listId: Int, itemId: Int, itemQuantity: Double): Long

    @Query(
        value = """
            DELETE FROM market_list_with_items
            WHERE listWithTypeId = :listId AND itemId = :itemId
        """
    )
    fun removeMarketListItem(listId: Int, itemId: Int): Int
}