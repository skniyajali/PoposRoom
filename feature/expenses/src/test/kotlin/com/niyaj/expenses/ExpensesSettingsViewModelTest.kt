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
import com.niyaj.expenses.settings.ExpensesSettingsEvent
import com.niyaj.expenses.settings.ExpensesSettingsViewModel
import com.niyaj.model.searchExpense
import com.niyaj.testing.repository.TestExpensesRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.parameterProvider.ExpensePreviewData
import com.niyaj.ui.utils.UiEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpensesSettingsViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = ExpensePreviewData.expenses
    private val repository = TestExpensesRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private lateinit var viewModel: ExpensesSettingsViewModel

    @Before
    fun setup() {
        viewModel = ExpensesSettingsViewModel(repository, analyticsHelper)
    }

    @Test
    fun `when search text changes, expenses are updated`() = runTest {
        repository.createTestExpenses(itemList)

        viewModel.searchTextChanged("Rent")
        advanceUntilIdle()

        viewModel.expenses.test {
            assertEquals(itemList.searchExpense("Rent"), awaitItem())
        }
    }

    @Test
    fun `when GetExportedItems event is triggered with no selection, all items are exported`() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.expenses.collect() }

            repository.createTestExpenses(itemList)

            viewModel.onEvent(ExpensesSettingsEvent.GetExportedItems)
            testScheduler.advanceUntilIdle()

            viewModel.exportedItems.test {
                assertEquals(itemList, awaitItem())
            }

            job.cancel()
        }

    @Test
    fun `when GetExportedItems event is triggered with selection, only selected items are exported`() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.expenses.collect() }

            repository.createTestExpenses(itemList)

            viewModel.selectItem(1)
            viewModel.selectItem(3)

            viewModel.onEvent(ExpensesSettingsEvent.GetExportedItems)
            advanceUntilIdle()

            assertEquals(
                itemList.filter { it.expenseId == 1 || it.expenseId == 3 },
                viewModel.exportedItems.value,
            )

            job.cancel()
        }

    @Test
    fun `when OnImportExpensesItemsFromFile event is triggered, importedItems are updated`() =
        runTest {
            viewModel.onEvent(ExpensesSettingsEvent.OnImportExpensesItemsFromFile(itemList))
            testScheduler.advanceUntilIdle()

            assertEquals(itemList, viewModel.importedItems.value)
        }

    @Test
    fun `when ImportExpensesItemsToDatabase event is triggered with no selection, all imported items are added`() =
        runTest {
            val job = launch { viewModel.expenses.collect() }

            viewModel.onEvent(ExpensesSettingsEvent.OnImportExpensesItemsFromFile(itemList))
            assertEquals(itemList, viewModel.importedItems.value)
            viewModel.onEvent(ExpensesSettingsEvent.ImportExpensesItemsToDatabase)
            advanceUntilIdle()

            viewModel.eventFlow.test {
                val event = awaitItem()
                assertTrue(event is UiEvent.OnSuccess)
                assertEquals(
                    "${itemList.size} expenses has been imported successfully",
                    (event as UiEvent.OnSuccess).successMessage,
                )
            }

            advanceUntilIdle()

            viewModel.expenses.test {
                assertEquals(itemList, awaitItem())
            }

            job.cancel()
        }

    @Test
    fun `when ImportExpensesItemsToDatabase event is triggered with selection, only selected items are added`() =
        runTest {
            val job = launch { viewModel.expenses.collect() }

            viewModel.onEvent(ExpensesSettingsEvent.OnImportExpensesItemsFromFile(itemList))

            assertEquals(itemList, viewModel.importedItems.value)

            viewModel.selectItem(2)
            viewModel.selectItem(4)

            viewModel.onEvent(ExpensesSettingsEvent.ImportExpensesItemsToDatabase)
            advanceUntilIdle()

            viewModel.eventFlow.test {
                val event = awaitItem()
                assertTrue(event is UiEvent.OnSuccess)
                assertEquals(
                    "2 expenses has been imported successfully",
                    (event as UiEvent.OnSuccess).successMessage,
                )
            }

            advanceUntilIdle()

            viewModel.expenses.test {
                val event = awaitItem()
                assertEquals(itemList.filter { it.expenseId == 2 || it.expenseId == 4 }, event)
            }

            job.cancel()
        }
}
