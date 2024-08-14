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

package com.niyaj.expenses

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.niyaj.common.tags.ExpenseTestTags.EXPENSES_AMOUNT_ALREADY_EXISTS
import com.niyaj.common.tags.ExpenseTestTags.EXPENSES_PRICE_IS_NOT_VALID
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_DATE_EMPTY_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NAME_EMPTY_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NAME_LENGTH_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_PRICE_EMPTY_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_PRICE_LESS_THAN_TEN_ERROR
import com.niyaj.common.utils.getStartTime
import com.niyaj.domain.expense.ValidateExpenseAmountUseCase
import com.niyaj.domain.expense.ValidateExpenseDateUseCase
import com.niyaj.domain.expense.ValidateExpenseNameUseCase
import com.niyaj.expenses.createOrUpdate.AddEditExpenseEvent
import com.niyaj.expenses.createOrUpdate.AddEditExpenseViewModel
import com.niyaj.testing.repository.TestExpensesRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.parameterProvider.ExpensePreviewData
import com.niyaj.ui.utils.UiEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test

class AddEditExpensesViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestExpensesRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private val savedStateHandle = SavedStateHandle()

    private val validateExpenseAmount = ValidateExpenseAmountUseCase()
    private val validateExpenseDate = ValidateExpenseDateUseCase()
    private val validateExpenseName = ValidateExpenseNameUseCase()

    private lateinit var viewModel: AddEditExpenseViewModel

    @Before
    fun setup() {
        viewModel = AddEditExpenseViewModel(
            expenseRepository = repository,
            validateExpenseAmount = validateExpenseAmount,
            validateExpenseDate = validateExpenseDate,
            validateExpenseName = validateExpenseName,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun `init with expenseId loads expense`() = runTest {
        val expense = repository.createTestExpense()

        val savedStateHandle = SavedStateHandle()
        savedStateHandle["expenseId"] = expense.expenseId

        viewModel = AddEditExpenseViewModel(
            expenseRepository = repository,
            validateExpenseAmount = validateExpenseAmount,
            validateExpenseDate = validateExpenseDate,
            validateExpenseName = validateExpenseName,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )

        assertEquals(expense.expenseName, viewModel.state.expenseName)
        assertEquals(expense.expenseAmount, viewModel.state.expenseAmount)
        assertEquals(expense.expenseNote, viewModel.state.expenseNote)
    }

    @Test
    fun `when ExpensesNameChanged event is received, state is updated`() {
        viewModel.onEvent(AddEditExpenseEvent.ExpensesNameChanged("New Expense"))
        assertEquals("New Expense", viewModel.state.expenseName)
    }

    @Test
    fun `when ExpensesAmountChanged event is received, state is updated`() {
        viewModel.onEvent(AddEditExpenseEvent.ExpensesAmountChanged("150"))
        assertEquals("150", viewModel.state.expenseAmount)
    }

    @Test
    fun `when ExpensesDateChanged event is received, state is updated`() {
        viewModel.onEvent(AddEditExpenseEvent.ExpensesDateChanged("2024-01-01"))
        assertEquals("2024-01-01", viewModel.state.expenseDate)
    }

    @Test
    fun `when ExpensesNoteChanged event is received, state is updated`() {
        viewModel.onEvent(AddEditExpenseEvent.ExpensesNoteChanged("Test Note"))
        assertEquals("Test Note", viewModel.state.expenseNote)
    }

    @Test
    fun `nameError updates when charges name is empty`() = runTest {
        viewModel.onEvent(AddEditExpenseEvent.ExpensesNameChanged(""))

        viewModel.nameError.test {
            assertEquals(EXPENSE_NAME_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun `nameError updates when charges name is shorter`() = runTest {
        viewModel.onEvent(AddEditExpenseEvent.ExpensesNameChanged("TC"))

        viewModel.nameError.test {
            assertEquals(EXPENSE_NAME_LENGTH_ERROR, awaitItem())
        }
    }

    @Test
    fun `priceError updates when charges price is empty`() = runTest {
        viewModel.onEvent(AddEditExpenseEvent.ExpensesAmountChanged(""))

        viewModel.priceError.test {
            assertEquals(EXPENSE_PRICE_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun `priceError updates when charges price is less than ten`() = runTest {
        viewModel.onEvent(AddEditExpenseEvent.ExpensesAmountChanged("8"))

        viewModel.priceError.test {
            assertEquals(EXPENSE_PRICE_LESS_THAN_TEN_ERROR, awaitItem())
        }
    }

    @Test
    fun `priceError updates when charges price is invalid`() = runTest {
        viewModel.onEvent(AddEditExpenseEvent.ExpensesAmountChanged("8C"))

        viewModel.priceError.test {
            assertEquals(EXPENSES_PRICE_IS_NOT_VALID, awaitItem())
        }
    }

    @Test
    fun `priceError updates when charges price is valid`() = runTest {
        viewModel.onEvent(AddEditExpenseEvent.ExpensesAmountChanged("10"))

        viewModel.priceError.test {
            assertEquals(null, awaitItem())
        }
    }

    @Test
    fun `dateError updates when expense date is empty`() = runTest {
        viewModel.onEvent(AddEditExpenseEvent.ExpensesDateChanged(""))

        viewModel.dateError.test {
            assertEquals(EXPENSE_DATE_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun `dateError updates when expense date is not empty`() = runTest {
        viewModel.onEvent(AddEditExpenseEvent.ExpensesDateChanged(getStartTime))

        viewModel.dateError.test {
            assertEquals(null, awaitItem())
        }
    }

    @Test
    fun `existingData updates when expense already exists`() = runTest {
        val expense = repository.createTestExpense()

        viewModel.onEvent(AddEditExpenseEvent.ExpensesNameChanged(expense.expenseName))
        viewModel.onEvent(AddEditExpenseEvent.ExpensesDateChanged(expense.expenseDate))

        viewModel.existingData.test {
            assertEquals(EXPENSES_AMOUNT_ALREADY_EXISTS, awaitItem())
        }
    }

    @Test
    fun `expensesName is empty when data not present`() = runTest {
        viewModel.expensesName.test {
            assertEquals(emptyList<String>(), awaitItem())
        }
    }

    @Test
    fun `expensesName is populated when data is present`() = runTest {
        val job = launch { viewModel.expensesName.collect() }

        val items = ExpensePreviewData.expenseNames
        repository.updateExpensesName(items)

        viewModel.expensesName.test {
            assertEquals(items, awaitItem())
        }

        job.cancel()
    }

    @Test
    fun `create a new charges with valid input emits success event`() = runTest {
        viewModel.onEvent(AddEditExpenseEvent.ExpensesNameChanged("New Expense"))
        viewModel.onEvent(AddEditExpenseEvent.ExpensesAmountChanged("10"))
        viewModel.onEvent(AddEditExpenseEvent.ExpensesDateChanged("2024-01-01"))
        viewModel.onEvent(AddEditExpenseEvent.ExpensesNoteChanged("Test Note"))
        viewModel.onEvent(AddEditExpenseEvent.AddOrUpdateExpense)

        advanceUntilIdle()

        viewModel.eventFlow.test {
            val event = awaitItem()
            assertTrue(event is UiEvent.OnSuccess)
            assertEquals(
                "Expense added successfully.",
                (event as UiEvent.OnSuccess).successMessage,
            )
        }

        val data = repository.getExpenseById(0).data
        assertNotNull(data)
        assertEquals("New Expense", data?.expenseName)
        assertEquals("10", data?.expenseAmount)
        assertEquals("2024-01-01", data?.expenseDate)
        assertEquals("Test Note", data?.expenseNote)
    }

    @Test
    fun `update a charges with valid input emit success event`() = runTest {
        viewModel.setExpenseId(1)
        viewModel.onEvent(AddEditExpenseEvent.ExpensesNameChanged("New Expense"))
        viewModel.onEvent(AddEditExpenseEvent.ExpensesAmountChanged("10"))
        viewModel.onEvent(AddEditExpenseEvent.ExpensesDateChanged("2024-01-01"))
        viewModel.onEvent(AddEditExpenseEvent.ExpensesNoteChanged("Test Note"))
        viewModel.onEvent(AddEditExpenseEvent.AddOrUpdateExpense)

        advanceUntilIdle()

        viewModel.onEvent(AddEditExpenseEvent.ExpensesNameChanged("Updated Expense"))
        viewModel.onEvent(AddEditExpenseEvent.ExpensesAmountChanged("100"))
        viewModel.onEvent(AddEditExpenseEvent.ExpensesDateChanged("2025-01-01"))
        viewModel.onEvent(AddEditExpenseEvent.ExpensesNoteChanged("Updated Note"))
        viewModel.onEvent(AddEditExpenseEvent.AddOrUpdateExpense)

        advanceUntilIdle()

        viewModel.eventFlow.test {
            val event = awaitItem()
            assertTrue(event is UiEvent.OnSuccess)
            assertEquals(
                "Expense updated successfully.",
                (event as UiEvent.OnSuccess).successMessage,
            )
        }

        val data = repository.getExpenseById(1).data
        assertNotNull(data)
        assertEquals("Updated Expense", data?.expenseName)
        assertEquals("100", data?.expenseAmount)
        assertEquals("2025-01-01", data?.expenseDate)
        assertEquals("Updated Note", data?.expenseNote)
    }
}
