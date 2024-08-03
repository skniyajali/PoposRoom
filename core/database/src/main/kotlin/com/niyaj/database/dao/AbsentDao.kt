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
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.database.model.AbsentEntity
import com.niyaj.database.model.EmployeeEntity
import com.niyaj.database.model.EmployeeWithAbsentCrossRef
import com.niyaj.database.model.EmployeeWithAbsentsDto
import kotlinx.coroutines.flow.Flow

@Dao
interface AbsentDao {

    @Transaction
    @Query(
        value = """
        SELECT * FROM employee
    """,
    )
    fun getAllAbsentEmployee(): Flow<List<EmployeeWithAbsentsDto>>

    @Query(
        value = """
        SELECT * FROM employee
    """,
    )
    fun getAllEmployee(): Flow<List<EmployeeEntity>>

    @Query(
        value = """
        SELECT * FROM employee WHERE employeeId = :employeeId
    """,
    )
    suspend fun getEmployeeById(employeeId: Int): EmployeeEntity?

    @Query(
        value = """
        SELECT * FROM absent ORDER BY createdAt DESC
    """,
    )
    fun getAllAbsent(): Flow<List<AbsentEntity>>

    @Query(
        value = """
        SELECT * FROM absent WHERE absentId = :absentId
    """,
    )
    suspend fun getAbsentById(absentId: Int): AbsentEntity?

    /**
     * Inserts [AbsentEntity] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreAbsent(absent: AbsentEntity): Long

    /**
     * Updates [AbsentEntity] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateAbsent(absent: AbsentEntity): Int

    /**
     * Inserts or updates [AbsentEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertAbsent(absent: AbsentEntity): Long

    /**
     * Inserts or updates [EmployeeEntity] in the db under the specified primary keys
     */
    @Upsert(entity = EmployeeEntity::class)
    suspend fun upsertEmployee(employeeEntity: EmployeeEntity): Long

    @Insert(entity = EmployeeWithAbsentCrossRef::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEmployeeWithAbsentCrossReference(employeeWithAbsent: EmployeeWithAbsentCrossRef)

    @Query(
        value = """
        DELETE FROM absent WHERE absentId = :absentId
    """,
    )
    suspend fun deleteAbsent(absentId: Int): Int

    /**
     * Deletes rows in the db matching the specified [absentIds]
     */
    @Query(
        value = """
            DELETE FROM absent
            WHERE absentId in (:absentIds)
        """,
    )
    suspend fun deleteAbsents(absentIds: List<Int>): Int

    @Query(
        value = """
        SELECT absentId FROM absent WHERE
            CASE WHEN :absentId IS NULL OR :absentId = 0
            THEN absentDate = :absentDate AND employeeId = :employeeId
            ELSE absentId != :absentId AND absentDate = :absentDate AND employeeId = :employeeId
            END LIMIT 1
    """,
    )
    fun findEmployeeByDate(absentDate: String, employeeId: Int, absentId: Int?): Int?

    @Query(
        value = """
        SELECT employeeId FROM employee WHERE employeeId == :employeeId OR employeeName = :employeeName
    """,
    )
    fun findEmployeeByName(employeeName: String, employeeId: Int?): Int?
}
