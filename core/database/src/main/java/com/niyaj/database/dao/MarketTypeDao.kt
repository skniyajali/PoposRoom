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
import androidx.room.Upsert
import com.niyaj.database.model.MarketTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketTypeDao {

    /**
     * Get all market types from the database
     */
    @Query(
        value = """
        SELECT * FROM market_type ORDER BY createdAt DESC
    """,
    )
    fun getAllMarketTypes(): Flow<List<MarketTypeEntity>>

    /**
     * Get market type by id
     */
    @Query(
        value = """
        SELECT * FROM market_type WHERE typeId = :id
    """,
    )
    suspend fun getMarketTypeById(id: Int): MarketTypeEntity?


    @Upsert(entity = MarketTypeEntity::class)
    suspend fun upsertMarketType(marketType: MarketTypeEntity): Long


    @Query(
        value = """
        DELETE FROM market_type WHERE typeId IN (:items)
    """,
    )
    suspend fun deleteMarketTypes(items: List<Int>): Int

    @Query(value = """
        SELECT typeId FROM market_type WHERE
            CASE WHEN :typeId IS NULL OR :typeId = 0
            THEN typeName = :typeName
            ELSE typeId != :typeId AND typeName = :typeName
            END LIMIT 1
    """
    )
    fun findMarketTypeByName(typeName: String, typeId: Int?): Int?
}