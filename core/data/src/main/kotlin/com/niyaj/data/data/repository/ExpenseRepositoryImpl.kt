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
import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_DATE_EMPTY_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NAME_EMPTY_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NAME_LENGTH_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_PRICE_EMPTY_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_PRICE_LESS_THAN_TEN_ERROR
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.ExpenseRepository
import com.niyaj.data.repository.ExpenseValidationRepository
import com.niyaj.database.dao.ExpenseDao
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Expense
import com.niyaj.model.searchExpense
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : ExpenseRepository, ExpenseValidationRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
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

    @OptIn(ExperimentalCoroutinesApi::class)
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
                val validateName = validateExpenseName(newExpense.expenseName)
                val validateAmount = validateExpenseName(newExpense.expenseAmount)
                val validateDate = validateExpenseName(newExpense.expenseDate)

                val hasError = listOf(validateName, validateAmount, validateDate).any {
                    !it.successful
                }

                if (!hasError) {
                    val result = withContext(ioDispatcher) {
                        expenseDao.upsertExpense(newExpense.toEntity())
                    }

                    Resource.Success(result > 0)
                } else {
                    Resource.Error("Unable to validate expenses")
                }
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

    override fun validateExpenseName(expenseName: String): ValidationResult {
        if (expenseName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = EXPENSE_NAME_EMPTY_ERROR,
            )
        }

        if (expenseName.length < 3) {
            return ValidationResult(
                successful = false,
                errorMessage = EXPENSE_NAME_LENGTH_ERROR,
            )
        }

        return ValidationResult(true)
    }

    override fun validateExpenseDate(expenseDate: String): ValidationResult {
        if (expenseDate.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = EXPENSE_DATE_EMPTY_ERROR,
            )
        }

        return ValidationResult(true)
    }

    override fun validateExpenseAmount(expenseAmount: String): ValidationResult {
        if (expenseAmount.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = EXPENSE_PRICE_EMPTY_ERROR,
            )
        }

        try {
            if (expenseAmount.toInt() < 10) {
                return ValidationResult(
                    successful = false,
                    errorMessage = EXPENSE_PRICE_LESS_THAN_TEN_ERROR,
                )
            }
        } catch (e: NumberFormatException) {
            return ValidationResult(
                successful = false,
                errorMessage = "Expenses amount is not valid.",
            )
        }

        return ValidationResult(true)
    }

    override suspend fun importExpensesDataToDatabase(expenses: List<Expense>): Resource<Boolean> {
        try {
            expenses.forEach { newExpense ->
                val validateName = validateExpenseName(newExpense.expenseName)
                val validateAmount = validateExpenseName(newExpense.expenseAmount)
                val validateDate = validateExpenseName(newExpense.expenseDate)

                val hasError = listOf(validateName, validateAmount, validateDate).any {
                    !it.successful
                }

                if (!hasError) {
                    withContext(ioDispatcher) {
                        expenseDao.upsertExpense(newExpense.toEntity())
                    }
                } else {
                    return Resource.Error("Unable to validate expenses")
                }
            }

            return Resource.Success(true)
        } catch (e: Exception) {
            return Resource.Error("Unable to add or update expense")
        }
    }
}
