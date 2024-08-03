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
import androidx.room.RawQuery
import androidx.room.Update
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import com.niyaj.database.model.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query(
        value = """
        SELECT * FROM expense WHERE expenseDate = :givenDate ORDER BY expenseDate DESC
    """,
    )
    fun getAllExpenseOnGivenDate(givenDate: String): Flow<List<ExpenseEntity>>

    @Query(
        value = """
        SELECT * FROM expense ORDER BY expenseDate DESC
    """,
    )
    fun getAllExpense(): Flow<List<ExpenseEntity>>

    @RawQuery(observedEntities = [ExpenseEntity::class])
    fun getAllPagingExpenses(query: SupportSQLiteQuery): List<ExpenseEntity>

    @Query(
        value = """
        SELECT DISTINCT expenseName FROM expense ORDER BY expenseDate DESC
    """,
    )
    fun getAllExpenseName(): Flow<List<String>>

    @Query(
        value = """
        SELECT * FROM expense WHERE expenseId = :expenseId
    """,
    )
    fun getExpenseById(expenseId: Int): ExpenseEntity?

    /**
     * Inserts [ExpenseEntity] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreExpense(expense: ExpenseEntity): Long

    /**
     * Updates [ExpenseEntity] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateExpense(expense: ExpenseEntity): Int

    /**
     * Inserts or updates [ExpenseEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertExpense(expense: ExpenseEntity): Long

    @Query(
        value = """
        DELETE FROM expense WHERE expenseId = :expenseId
    """,
    )
    suspend fun deleteExpense(expenseId: Int): Int

    /**
     * Deletes rows in the db matching the specified [expenseIds]
     */
    @Query(
        value = """
            DELETE FROM expense
            WHERE expenseId in (:expenseIds)
        """,
    )
    suspend fun deleteExpense(expenseIds: List<Int>): Int

    @Query(
        value = """
        SELECT * FROM expense WHERE 
        expenseDate = :expenseDate AND expenseName = :expenseName LIMIT 1
    """,
    )
    fun findExpenseByNameAndDate(expenseName: String, expenseDate: String): ExpenseEntity?
}
