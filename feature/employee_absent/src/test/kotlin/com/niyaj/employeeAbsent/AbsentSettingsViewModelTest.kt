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

package com.niyaj.employeeAbsent

import app.cash.turbine.test
import com.niyaj.employeeAbsent.settings.AbsentSettingsEvent
import com.niyaj.employeeAbsent.settings.AbsentSettingsViewModel
import com.niyaj.model.searchAbsentees
import com.niyaj.model.utils.toDate
import com.niyaj.testing.repository.TestAbsentRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.parameterProvider.AbsentPreviewData
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

class AbsentSettingsViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = AbsentPreviewData.employeesWithAbsents
    private val repository = TestAbsentRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private lateinit var viewModel: AbsentSettingsViewModel

    @Before
    fun setup() {
        viewModel = AbsentSettingsViewModel(repository, analyticsHelper)
    }

    @Test
    fun `when search text changes, charges are updated`() = runTest {
        repository.updateEmployeeAbsents(itemList)
        val searchDate = ("1675323600000").toDate

        viewModel.searchTextChanged(searchDate)
        advanceUntilIdle()

        viewModel.items.test {
            assertEquals(itemList.searchAbsentees(searchDate), awaitItem())
        }
    }

    @Test
    fun `when GetExportedItems event is triggered with no selection, all items are exported`() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.items.collect() }

            repository.updateEmployeeAbsents(itemList)

            viewModel.onEvent(AbsentSettingsEvent.GetExportedItems)
            testScheduler.advanceUntilIdle()

            viewModel.exportedItems.test {
                assertEquals(itemList, awaitItem())
            }

            job.cancel()
        }

    @Test
    fun `when GetExportedItems event is triggered with selection, only selected items are exported`() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.items.collect() }

            repository.updateEmployeeAbsents(itemList)
            val selectedItems = itemList.filter { absents ->
                absents.absents.any {
                    it.absentId == 1 || it.absentId == 3
                }
            }

            viewModel.selectItem(1)
            viewModel.selectItem(3)

            viewModel.onEvent(AbsentSettingsEvent.GetExportedItems)
            advanceUntilIdle()

            assertEquals(selectedItems, viewModel.exportedItems.value)

            job.cancel()
        }

    @Test
    fun `when OnImportChargesFromFile event is triggered, importedItems are updated`() =
        runTest {
            viewModel.onEvent(AbsentSettingsEvent.OnImportAbsentItemsFromFile(itemList))
            testScheduler.advanceUntilIdle()

            assertEquals(itemList, viewModel.importedItems.value)
        }

    @Test
    fun `when ImportChargesToDatabase event is triggered with no selection, all imported items are added`() =
        runTest {
            val job = launch { viewModel.items.collect() }

            viewModel.onEvent(AbsentSettingsEvent.OnImportAbsentItemsFromFile(itemList))
            assertEquals(itemList, viewModel.importedItems.value)
            viewModel.onEvent(AbsentSettingsEvent.ImportAbsentItemsToDatabase)
            advanceUntilIdle()

            viewModel.eventFlow.test {
                val event = awaitItem()
                assertTrue(event is UiEvent.OnSuccess)
                assertEquals(
                    "${itemList.sumOf { it.absents.size }} items has been imported successfully",
                    (event as UiEvent.OnSuccess).successMessage,
                )
            }

            advanceUntilIdle()

            viewModel.items.test {
                assertEquals(itemList, awaitItem())
            }

            job.cancel()
        }

    @Test
    fun `when ImportChargesToDatabase event is triggered with selection, only selected items are added`() =
        runTest {
            val job = launch { viewModel.items.collect() }

            viewModel.onEvent(AbsentSettingsEvent.OnImportAbsentItemsFromFile(itemList))

            assertEquals(itemList, viewModel.importedItems.value)

            viewModel.selectItem(2)
            viewModel.selectItem(4)

            assertEquals(listOf(2, 4), viewModel.selectedItems.toList())

            viewModel.onEvent(AbsentSettingsEvent.ImportAbsentItemsToDatabase)
            advanceUntilIdle()

//            viewModel.eventFlow.test {
//                val event = awaitItem()
//                assertTrue(event is UiEvent.OnSuccess)
//                assertEquals(
//                    "2 items has been imported successfully",
//                    (event as UiEvent.OnSuccess).successMessage,
//                )
//            }

            advanceUntilIdle()

            val selectedItems = itemList.filter { absents ->
                absents.absents.any {
                    it.absentId == 2 || it.absentId == 4
                }
            }

            viewModel.items.test {
                assertEquals(selectedItems, awaitItem())
            }

            job.cancel()
        }
}
