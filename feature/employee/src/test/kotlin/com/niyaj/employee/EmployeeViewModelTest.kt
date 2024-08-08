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
import com.niyaj.model.searchEmployee
import com.niyaj.testing.repository.TestEmployeeRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.EmployeePreviewData
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

class EmployeeViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = EmployeePreviewData.employeeList
    private val repository = TestEmployeeRepository()
    private val analyticsHelper = TestAnalyticsHelper()

    private lateinit var viewModel: EmployeeViewModel

    @Before
    fun setup() {
        viewModel = EmployeeViewModel(repository, analyticsHelper)
    }

    @Test
    fun customersState_initially_Loading() = runTest {
        assertEquals(UiState.Loading, viewModel.employees.value)
    }

    @Test
    fun employeesState_isEmpty_whenDataIsEmpty() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.employees.collect() }

        assertEquals(UiState.Empty, viewModel.employees.value)

        job.cancel()
    }

    @Test
    fun employeesState_isSuccess_whenDataIsAvailable() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.employees.collect() }
        repository.updateEmployeeData(itemList)

        assertEquals(UiState.Success(itemList), viewModel.employees.value)

        job.cancel()
    }

    @Test
    fun search_withInvalidData_returnEmptyResult() = runTest {
        repository.updateEmployeeData(itemList)
        viewModel.searchTextChanged("Invalid")

        advanceUntilIdle()

        viewModel.employees.test {
            assertEquals(UiState.Empty, awaitItem())
        }
    }

    @Test
    fun search_withValidData_returnResult() = runTest {
        repository.updateEmployeeData(itemList)
        viewModel.searchTextChanged("Jane")

        advanceUntilIdle()

        viewModel.employees.test {
            assertEquals(UiState.Success(itemList.searchEmployee("Jane")), awaitItem())
        }
    }

    @Test
    fun search_onClose_returnAllData() = runTest {
        repository.updateEmployeeData(itemList)
        viewModel.searchTextChanged("Extra")

        advanceUntilIdle()

        viewModel.employees.test {
            assertEquals(UiState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.closeSearchBar()
        advanceUntilIdle()

        viewModel.employees.test {
            assertEquals(UiState.Success(itemList), awaitItem())
        }
    }

    @Test
    fun deleteCharges_shouldRemoveFromList() = runTest {
        repository.updateEmployeeData(itemList)
        val employeeId = itemList.first().employeeId

        viewModel.selectItem(employeeId)
        viewModel.deleteItems()

        advanceUntilIdle()

        viewModel.employees.test {
            assertEquals(UiState.Success(itemList.drop(1)), awaitItem())
        }

        assert(employeeId !in viewModel.selectedItems.toList())
    }

    @Test
    fun selectEmployee_onSelectItem_shouldUpdateSelectedEmployee() = runTest {
        repository.updateEmployeeData(itemList)

        val employeeId = itemList.first().employeeId
        viewModel.selectItem(employeeId)

        advanceUntilIdle()

        assertContains(viewModel.selectedItems.toList(), employeeId)
    }

    @Test
    fun selectAllItems_onSelectAllItems_shouldUpdateSelectedItems() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.employees.collect() }

        repository.updateEmployeeData(itemList)
        viewModel.selectAllItems()

        advanceUntilIdle()

        assertEquals(itemList.map { it.employeeId }, viewModel.selectedItems.toList())

        job.cancel()
    }

    @Test
    fun deselectItems_onDeselectItems_shouldClearSelectedItems() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.employees.collect() }
        repository.updateEmployeeData(itemList)

        viewModel.selectAllItems()
        assertEquals(viewModel.selectedItems.toList(), itemList.map { it.employeeId })

        advanceUntilIdle()

        viewModel.deselectItems()

        advanceUntilIdle()

        assertEquals(emptyList(), viewModel.selectedItems.toList())

        job.cancel()
    }
}
