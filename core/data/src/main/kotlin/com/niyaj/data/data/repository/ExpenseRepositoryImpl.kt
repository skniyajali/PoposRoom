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

package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.ExpenseRepository
import com.niyaj.database.dao.ExpenseDao
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Expense
import com.niyaj.model.searchExpense
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : ExpenseRepository {

    override suspend fun getAllExpensesOnSpecificDate(
        searchText: String,
        givenDate: String,
    ): Flow<List<Expense>> {
        return withContext(ioDispatcher) {
            expenseDao.getAllExpenseOnGivenDate(givenDate).mapLatest { it ->
                it.map {
                    it.asExternalModel()
                }.searchExpense(searchText)
            }
        }
    }

    override suspend fun getAllExpenses(searchText: String): Flow<List<Expense>> {
        return withContext(ioDispatcher) {
            expenseDao.getAllExpense().mapLatest { it ->
                it.map {
                    it.asExternalModel()
                }.searchExpense(searchText)
            }
        }
    }

    override suspend fun getAllExpenseName(searchText: String): Flow<List<String>> {
        return withContext(ioDispatcher) {
            expenseDao.getAllExpenseName().mapLatest { list ->
                list.filter { it.contains(searchText, ignoreCase = true) }
            }
        }
    }

    override suspend fun findExpenseByNameAndDate(
        expenseName: String,
        expenseDate: String,
    ): Boolean {
        return withContext(ioDispatcher) {
            expenseDao.findExpenseByNameAndDate(expenseName, expenseDate) != null
        }
    }

    override suspend fun getExpenseById(expenseId: Int): Resource<Expense?> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(expenseDao.getExpenseById(expenseId)?.asExternalModel())
            }
        } catch (e: Exception) {
            Resource.Error("Unable to get expense details")
        }
    }

    override suspend fun upsertExpense(newExpense: Expense): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = withContext(ioDispatcher) {
                    expenseDao.upsertExpense(newExpense.toEntity())
                }

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error("Unable to add or update expense")
        }
    }

    override suspend fun deleteExpenses(expenseIds: List<Int>): Resource<Boolean> {
        return try {
            val result = withContext(ioDispatcher) {
                expenseDao.deleteExpense(expenseIds)
            }

            Resource.Success(result > 0)
        } catch (e: Exception) {
            Resource.Error("Unable to delete expenses")
        }
    }

    override suspend fun importExpensesDataToDatabase(expenses: List<Expense>): Resource<Boolean> {
        try {
            expenses.forEach { newExpense ->
                withContext(ioDispatcher) {
                    expenseDao.upsertExpense(newExpense.toEntity())
                }
            }

            return Resource.Success(true)
        } catch (e: Exception) {
            return Resource.Error("Unable to add or update expense")
        }
    }
}
