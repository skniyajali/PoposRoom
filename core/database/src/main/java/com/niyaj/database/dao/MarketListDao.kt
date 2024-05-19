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
import com.niyaj.database.model.MarketListEntity
import com.niyaj.model.MarketItemAndQuantity
import com.niyaj.model.MarketListAndType
import com.niyaj.model.MarketTypeIdAndListTypes
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketListDao {

    @Query(
        value = """
        SELECT
            ml.marketId, ml.createdAt, ml.updatedAt, ml.marketDate,
            mt.typeId, mt.typeName, mlwt.listType, mlwt.listWithTypeId
        FROM market_list ml
        JOIN market_list_with_type mlwt ON ml.marketId = mlwt.marketId
        JOIN market_type mt ON mlwt.typeId = mt.typeId
        WHERE ml.marketId = :marketId
        """
    )
    fun getMarketListByMarketId(marketId: Int): Flow<List<MarketListAndType>?>


    @Transaction
    @Query(
        value = """
            SELECT ml.marketId, ml.marketDate, ml.createdAt, 
            mlt.typeId, mt.typeName, mlt.listWithTypeId, mlt.listType, ml.updatedAt
            FROM market_list ml
            JOIN market_list_with_type mlt ON mlt.marketId = ml.marketId
            JOIN market_type mt ON mlt.typeId = mt.typeId
            WHERE ml.marketId = :marketId
        """
    )
    fun getMarketDetailsById(marketId: Int): Flow<MarketListAndType>

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
        SELECT
            ml.marketId, ml.createdAt, ml.updatedAt, ml.marketDate,
            mt.typeId, mt.typeName, mlwt.listType, mlwt.listWithTypeId
        FROM market_list ml
        JOIN market_list_with_type mlwt ON ml.marketId = mlwt.marketId
        JOIN market_type mt ON mlwt.typeId = mt.typeId
        ORDER BY ml.marketId DESC
        """
    )
    fun getMarketItems(): Flow<List<MarketListAndType>>


    @Query(
        value = """
        SELECT typeId, typeName, listTypes FROM market_type ORDER BY typeId DESC
    """
    )
    fun getAllMarketTypes(): Flow<List<MarketTypeIdAndListTypes>>


    /**
     * Inserts or updates [MarketListEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertMarketList(marketList: MarketListEntity): Long

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

}