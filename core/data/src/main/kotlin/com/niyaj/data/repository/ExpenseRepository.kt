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

package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {

    suspend fun getAllExpensesOnSpecificDate(
        searchText: String,
        givenDate: String,
    ): Flow<List<Expense>>

    suspend fun getAllExpenses(searchText: String): Flow<List<Expense>>

    suspend fun getAllExpenseName(searchText: String): Flow<List<String>>

    suspend fun findExpenseByNameAndDate(expenseName: String, expenseDate: String): Boolean

    suspend fun getExpenseById(expenseId: Int): Resource<Expense?>

    suspend fun upsertExpense(newExpense: Expense): Resource<Boolean>

    suspend fun deleteExpenses(expenseIds: List<Int>): Resource<Boolean>

    suspend fun importExpensesDataToDatabase(expenses: List<Expense>): Resource<Boolean>
}
