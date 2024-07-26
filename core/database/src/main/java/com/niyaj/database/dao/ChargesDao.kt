/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.database.model.ChargesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChargesDao {

    @Query(
        value = """
        SELECT * FROM charges ORDER BY createdAt DESC
    """,
    )
    fun getAllCharges(): Flow<List<ChargesEntity>>

    @Query(
        value = """
        SELECT * FROM charges WHERE chargesId = :chargesId
    """,
    )
    fun getChargesById(chargesId: Int): ChargesEntity?

    /**
     * Inserts [ChargesEntity] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreCharges(charges: ChargesEntity): Long

    /**
     * Updates [ChargesEntity] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateCharges(charges: ChargesEntity): Int

    /**
     * Inserts or updates [ChargesEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertCharges(charges: ChargesEntity): Long

    @Query(
        value = """
        DELETE FROM charges WHERE chargesId = :chargesId
    """,
    )
    suspend fun deleteCharges(chargesId: Int): Int

    /**
     * Deletes rows in the db matching the specified [chargesIds]
     */
    @Query(
        value = """
            DELETE FROM charges
            WHERE chargesId in (:chargesIds)
        """,
    )
    suspend fun deleteCharges(chargesIds: List<Int>): Int

    @Query(
        value = """
        SELECT chargesId FROM charges WHERE
            CASE WHEN :chargesId IS NULL OR :chargesId = 0
            THEN chargesName = :chargesName
            ELSE chargesId != :chargesId AND chargesName = :chargesName
            END LIMIT 1
    """,
    )
    fun findChargesByName(chargesName: String, chargesId: Int?): Int?
}
