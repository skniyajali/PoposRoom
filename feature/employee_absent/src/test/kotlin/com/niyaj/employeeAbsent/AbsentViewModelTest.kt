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
import com.niyaj.model.searchAbsentees
import com.niyaj.model.utils.toDate
import com.niyaj.testing.repository.TestAbsentRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.AbsentPreviewData
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

class AbsentViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = AbsentPreviewData.employeesWithAbsents
    private val repository = TestAbsentRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private val searchDate = ("1675323600000").toDate

    private lateinit var viewModel: AbsentViewModel

    @Before
    fun setup() {
        viewModel = AbsentViewModel(repository, analyticsHelper)
    }

    @Test
    fun absentState_initially_Loading() = runTest {
        assertEquals(UiState.Loading, viewModel.absents.value)
    }

    @Test
    fun absentsState_isEmpty_whenDataIsEmpty() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.absents.collect() }

        assertEquals(UiState.Empty, viewModel.absents.value)

        job.cancel()
    }

    @Test
    fun absentsState_isSuccess_whenDataIsAvailable() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.absents.collect() }
        repository.updateEmployeeAbsents(itemList)

        assertEquals(UiState.Success(itemList), viewModel.absents.value)

        job.cancel()
    }

    @Test
    fun search_withInvalidData_returnEmptyResult() = runTest {
        repository.updateEmployeeAbsents(itemList)
        viewModel.searchTextChanged("Invalid")

        advanceUntilIdle()

        viewModel.absents.test {
            assertEquals(UiState.Empty, awaitItem())
        }
    }

    @Test
    fun search_withValidData_returnResult() = runTest {
        repository.updateEmployeeAbsents(itemList)
        viewModel.searchTextChanged(searchDate)

        advanceUntilIdle()

        viewModel.absents.test {
            assertEquals(
                UiState.Success(itemList.searchAbsentees(searchDate)),
                awaitItem(),
            )
        }
    }

    @Test
    fun search_onClose_returnAllData() = runTest {
        repository.updateEmployeeAbsents(itemList)
        viewModel.searchTextChanged("Extra")

        advanceUntilIdle()

        viewModel.absents.test {
            assertEquals(UiState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.closeSearchBar()
        advanceUntilIdle()

        viewModel.absents.test {
            assertEquals(UiState.Success(itemList), awaitItem())
        }
    }

    @Test
    fun deleteCharges_shouldRemoveFromList() = runTest {
        repository.updateEmployeeAbsents(itemList)
        val absentId = itemList.first().absents.first().absentId
        val deletedItemList = itemList.filter { it.absents.any { it.absentId != absentId } }

        viewModel.selectItem(absentId)
        viewModel.deleteItems()

        advanceUntilIdle()

        viewModel.absents.test {
            assertEquals(UiState.Success(deletedItemList), awaitItem())
        }

        assert(absentId !in viewModel.selectedItems.toList())
    }

    @Test
    fun selectAbsentDate_onSelectItem_shouldUpdateSelectedAbsentDate() = runTest {
        repository.updateEmployeeAbsents(itemList)

        val absentId = itemList.first().absents.first().absentId
        viewModel.selectItem(absentId)

        advanceUntilIdle()

        assertContains(viewModel.selectedItems.toList(), absentId)
    }

    @Test
    fun selectAllItems_onSelectAllItems_shouldUpdateSelectedItems() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.absents.collect() }
        val allAbsentId = itemList.flatMap { item -> item.absents.map { it.absentId } }

        repository.updateEmployeeAbsents(itemList)
        viewModel.selectAllItems()

        advanceUntilIdle()

        assertEquals(allAbsentId, viewModel.selectedItems.toList())

        job.cancel()
    }

    @Test
    fun deselectItems_onDeselectItems_shouldClearSelectedItems() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.absents.collect() }
        repository.updateEmployeeAbsents(itemList)
        val allAbsentId = itemList.flatMap { item -> item.absents.map { it.absentId } }

        viewModel.selectAllItems()
        assertEquals(allAbsentId, viewModel.selectedItems.toList())

        advanceUntilIdle()

        viewModel.deselectItems()

        advanceUntilIdle()

        assertEquals(emptyList(), viewModel.selectedItems.toList())

        job.cancel()
    }

    @Test
    fun selectEmployee_onSelectNewEmployee_shouldAddedIntoSelectedEmployee() = runTest {
        val employeeId = 1
        viewModel.selectEmployee(employeeId)

        advanceUntilIdle()

        assert(employeeId in viewModel.selectedEmployee)
    }

    @Test
    fun selectEmployee_onSelectExistingEmployee_shouldRemoveFromSelectedEmployee() = runTest {
        val employeeId = 1
        viewModel.selectEmployee(employeeId)

        advanceUntilIdle()
        assert(employeeId in viewModel.selectedEmployee)

        viewModel.selectEmployee(employeeId)

        advanceUntilIdle()
        assert(employeeId !in viewModel.selectedEmployee)
    }
}
