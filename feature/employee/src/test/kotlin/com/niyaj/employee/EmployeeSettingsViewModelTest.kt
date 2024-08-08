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

package com.niyaj.employee

import app.cash.turbine.test
import com.niyaj.employee.settings.EmployeeSettingsEvent
import com.niyaj.employee.settings.EmployeeSettingsViewModel
import com.niyaj.model.searchEmployee
import com.niyaj.testing.repository.TestEmployeeRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.parameterProvider.EmployeePreviewData
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

class EmployeeSettingsViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = EmployeePreviewData.employeeList
    private val repository = TestEmployeeRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private lateinit var viewModel: EmployeeSettingsViewModel

    @Before
    fun setup() {
        viewModel = EmployeeSettingsViewModel(repository, analyticsHelper)
    }

    @Test
    fun `when search text changes, charges are updated`() = runTest {
        repository.updateEmployeeData(itemList)

        viewModel.searchTextChanged("Jane")
        advanceUntilIdle()

        viewModel.employees.test {
            assertEquals(itemList.searchEmployee("Jane"), awaitItem())
        }
    }

    @Test
    fun `when GetExportedItems event is triggered with no selection, all items are exported`() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.employees.collect() }

            repository.updateEmployeeData(itemList)

            viewModel.onEvent(EmployeeSettingsEvent.GetExportedItems)
            testScheduler.advanceUntilIdle()

            viewModel.exportedItems.test {
                assertEquals(itemList, awaitItem())
            }

            job.cancel()
        }

    @Test
    fun `when GetExportedItems event is triggered with selection, only selected items are exported`() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.employees.collect() }

            repository.updateEmployeeData(itemList)

            viewModel.selectItem(1)
            viewModel.selectItem(3)

            viewModel.onEvent(EmployeeSettingsEvent.GetExportedItems)
            advanceUntilIdle()

            assertEquals(
                itemList.filter { it.employeeId == 1 || it.employeeId == 3 },
                viewModel.exportedItems.value,
            )

            job.cancel()
        }

    @Test
    fun `when OnImportChargesFromFile event is triggered, importedItems are updated`() =
        runTest {
            viewModel.onEvent(EmployeeSettingsEvent.OnImportEmployeeItemsFromFile(itemList))
            testScheduler.advanceUntilIdle()

            assertEquals(itemList, viewModel.importedItems.value)
        }

    @Test
    fun `when ImportChargesToDatabase event is triggered with no selection, all imported items are added`() =
        runTest {
            val job = launch { viewModel.employees.collect() }

            viewModel.onEvent(EmployeeSettingsEvent.OnImportEmployeeItemsFromFile(itemList))
            assertEquals(itemList, viewModel.importedItems.value)
            viewModel.onEvent(EmployeeSettingsEvent.ImportEmployeeItemsToDatabase)
            advanceUntilIdle()

            viewModel.eventFlow.test {
                val event = awaitItem()
                assertTrue(event is UiEvent.OnSuccess)
                assertEquals(
                    "${itemList.size} employees has been imported successfully",
                    (event as UiEvent.OnSuccess).successMessage,
                )
            }

            advanceUntilIdle()

            viewModel.employees.test {
                assertEquals(itemList, awaitItem())
            }

            job.cancel()
        }

    @Test
    fun `when ImportChargesToDatabase event is triggered with selection, only selected items are added`() =
        runTest {
            val job = launch { viewModel.employees.collect() }

            viewModel.onEvent(EmployeeSettingsEvent.OnImportEmployeeItemsFromFile(itemList))

            assertEquals(itemList, viewModel.importedItems.value)

            viewModel.selectItem(2)
            viewModel.selectItem(4)

            viewModel.onEvent(EmployeeSettingsEvent.ImportEmployeeItemsToDatabase)
            advanceUntilIdle()

            viewModel.eventFlow.test {
                val event = awaitItem()
                assertTrue(event is UiEvent.OnSuccess)
                assertEquals(
                    "2 employees has been imported successfully",
                    (event as UiEvent.OnSuccess).successMessage,
                )
            }

            advanceUntilIdle()

            viewModel.employees.test {
                val event = awaitItem()
                assertEquals(itemList.filter { it.employeeId == 2 || it.employeeId == 4 }, event)
            }

            job.cancel()
        }
}
