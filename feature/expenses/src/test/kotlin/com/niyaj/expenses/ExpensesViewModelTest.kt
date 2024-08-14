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

import app.cash.turbine.test
import com.niyaj.common.utils.getStartTime
import com.niyaj.model.searchExpense
import com.niyaj.testing.repository.TestExpensesRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.ExpensePreviewData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ExpensesViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = ExpensePreviewData.expenses.filter { it.expenseDate == getStartTime }
    private val repository = TestExpensesRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private lateinit var viewModel: ExpensesViewModel

    @Before
    fun setup() {
        viewModel = ExpensesViewModel(repository, analyticsHelper)
        viewModel.selectDate(getStartTime)
    }

    @Test
    fun expensesState_initially_Loading() = runTest {
        assertEquals(
            UiState.Loading,
            viewModel.expenses.value,
        )
    }

    @Test
    fun expensesState_isEmpty_whenDataIsEmpty() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.expenses.collect() }

        assertEquals(UiState.Empty, viewModel.expenses.value)

        job.cancel()
    }

    @Test
    fun expensesState_isSuccess_whenDataIsAvailable() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.expenses.collect() }
        repository.createTestExpenses(itemList)

        assertEquals(UiState.Success(itemList), viewModel.expenses.value)

        job.cancel()
    }

    @Test
    fun search_withInvalidData_returnEmptyResult() = runTest {
        repository.createTestExpenses(itemList)
        viewModel.searchTextChanged("Invalid")

        advanceUntilIdle()

        viewModel.expenses.test {
            assertEquals(UiState.Empty, awaitItem())
        }
    }

    @Test
    fun search_withValidData_returnResult() = runTest {
        repository.createTestExpenses(itemList)
        viewModel.searchTextChanged("Groceries")

        advanceUntilIdle()
        val items = itemList.searchExpense("Groceries")
        viewModel.expenses.test {
            assertEquals(UiState.Success(items), awaitItem())
        }
    }

    @Test
    fun search_onClose_returnAllData() = runTest {
        repository.createTestExpenses(itemList)
        viewModel.searchTextChanged("Extra")

        advanceUntilIdle()

        viewModel.expenses.test {
            assertEquals(UiState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.closeSearchBar()
        advanceUntilIdle()

        viewModel.expenses.test {
            assertEquals(UiState.Success(itemList), awaitItem())
        }
    }

    @Test
    fun deleteCharges_shouldRemoveFromList() = runTest {
        repository.createTestExpenses(itemList)
        val expenseId = itemList.first().expenseId

        viewModel.selectItem(expenseId)
        viewModel.deleteItems()

        advanceUntilIdle()

        viewModel.expenses.test {
            assertEquals(UiState.Success(itemList.drop(1)), awaitItem())
        }

        assert(expenseId !in viewModel.selectedItems.toList())
    }

    @Test
    fun selectExpenses_onSelectItem_shouldUpdateSelectedCharges() = runTest {
        repository.createTestExpenses(itemList)

        val expenseId = itemList.first().expenseId
        viewModel.selectItem(expenseId)

        advanceUntilIdle()

        assertContains(viewModel.selectedItems.toList(), expenseId)
    }

    @Test
    fun selectAllItems_onSelectAllItems_shouldUpdateSelectedItems() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.expenses.collect() }

        repository.createTestExpenses(itemList)
        viewModel.selectAllItems()

        advanceUntilIdle()

        assertEquals(itemList.map { it.expenseId }, viewModel.selectedItems.toList())

        job.cancel()
    }

    @Test
    fun deselectItems_onDeselectItems_shouldClearSelectedItems() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.expenses.collect() }
        repository.createTestExpenses(itemList)

        viewModel.selectAllItems()
        assertEquals(viewModel.selectedItems.toList(), itemList.map { it.expenseId })

        advanceUntilIdle()

        viewModel.deselectItems()

        advanceUntilIdle()

        assertEquals(emptyList(), viewModel.selectedItems.toList())

        job.cancel()
    }
}
