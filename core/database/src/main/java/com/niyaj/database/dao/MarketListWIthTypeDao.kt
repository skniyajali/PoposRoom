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
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomWarnings
import androidx.room.Upsert
import com.niyaj.database.model.MarketListWithTypeEntity
import com.niyaj.model.TypeIdAndListType

@Dao
interface MarketListWIthTypeDao {

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query(
        """
        SELECT typeId, listType FROM market_list_with_type 
        WHERE marketId = :marketId
    """,
    )
    suspend fun getAllMarketListWithTypeById(marketId: Int): List<TypeIdAndListType>

    @Query(
        """
        SELECT listWithTypeId FROM market_list_with_type 
        WHERE typeId = :typeId AND marketId = :marketId AND listType = :listType
    """,
    )
    suspend fun findListTypeByTypeId(marketId: Int, typeId: Int, listType: String): Int?

    @Upsert
    suspend fun upsertMarketListWithType(marketListWithType: MarketListWithTypeEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMarketListWithType(marketListWithType: MarketListWithTypeEntity): Long

    @Query(
        """
        DELETE FROM market_list_with_type 
        WHERE marketId = :marketId AND listType = :listType AND typeId = :typeId
    """,
    )
    suspend fun deleteMarketListWithType(marketId: Int, typeId: Int, listType: String): Int
}