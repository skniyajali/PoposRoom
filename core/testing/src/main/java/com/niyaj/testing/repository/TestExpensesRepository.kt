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

package com.niyaj.testing.repository

import com.niyaj.common.result.Resource
import com.niyaj.common.utils.getStartTime
import com.niyaj.data.repository.ExpenseRepository
import com.niyaj.model.Expense
import com.niyaj.model.searchExpense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import org.jetbrains.annotations.TestOnly

class TestExpensesRepository : ExpenseRepository {

    /**
     * The backing expenses list for testing
     */
    private val items = MutableStateFlow(mutableListOf<Expense>())
    private val expensesName = MutableStateFlow(mutableListOf<String>())

    override suspend fun getAllExpensesOnSpecificDate(
        searchText: String,
        givenDate: String,
    ): Flow<List<Expense>> {
        return items.mapLatest { list ->
            list.filter { it.expenseDate == givenDate }.searchExpense(searchText)
        }
    }

    override suspend fun getAllExpenses(searchText: String): Flow<List<Expense>> {
        return items.mapLatest { it.searchExpense(searchText) }
    }

    override suspend fun getAllExpenseName(searchText: String): Flow<List<String>> {
        return expensesName.mapLatest { list ->
            list.filter { it.contains(searchText, true) }
        }
    }

    override suspend fun findExpenseByNameAndDate(
        expenseName: String,
        expenseDate: String,
    ): Boolean {
        return items.value.any { it.expenseName == expenseName && it.expenseDate == expenseDate }
    }

    override suspend fun getExpenseById(expenseId: Int): Resource<Expense?> {
        return Resource.Success(items.value.find { it.expenseId == expenseId })
    }

    override suspend fun upsertExpense(newExpense: Expense): Resource<Boolean> {
        val result = items.value.find { it.expenseId == newExpense.expenseId }

        return Resource.Success(
            if (result == null) {
                items.value.add(newExpense)
                expensesName.value.add(newExpense.expenseName)
            } else {
                items.value.remove(result)
                items.value.add(newExpense)
                expensesName.value.remove(result.expenseName)
                expensesName.value.add(newExpense.expenseName)
            },
        )
    }

    override suspend fun deleteExpenses(expenseIds: List<Int>): Resource<Boolean> {
        return Resource.Success(items.value.removeAll { it.expenseId in expenseIds })
    }

    override suspend fun importExpensesDataToDatabase(expenses: List<Expense>): Resource<Boolean> {
        items.value.addAll(expenses)
        expensesName.value.addAll(expenses.map { it.expenseName })
        return Resource.Success(true)
    }

    @TestOnly
    fun createTestExpenses(expenses: List<Expense>) {
        items.value = expenses.toMutableList()
    }

    @TestOnly
    fun createTestExpense(): Expense {
        val expense = Expense(
            expenseId = 1,
            expenseName = "Test Expense",
            expenseAmount = "1000",
            expenseDate = getStartTime,
            expenseNote = "Test Note",
        )

        items.value.add(expense)
        expensesName.value.add(expense.expenseName)
        return expense
    }

    @TestOnly
    fun updateExpensesName(names: List<String>) {
        expensesName.value = names.toMutableList()
    }
}
