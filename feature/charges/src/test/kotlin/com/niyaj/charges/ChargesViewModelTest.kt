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

package com.niyaj.charges

import app.cash.turbine.test
import com.niyaj.model.searchCharges
import com.niyaj.testing.repository.TestChargesRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.ChargesPreviewData
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

class ChargesViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = ChargesPreviewData.chargesList
    private val repository = TestChargesRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private lateinit var viewModel: ChargesViewModel

    @Before
    fun setup() {
        viewModel = ChargesViewModel(repository, analyticsHelper)
    }

    @Test
    fun chargesState_initially_Loading() = runTest {
        assertEquals(
            UiState.Loading,
            viewModel.charges.value,
        )
    }

    @Test
    fun chargesState_isEmpty_whenDataIsEmpty() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.charges.collect() }

        assertEquals(
            UiState.Empty,
            viewModel.charges.value,
        )

        job.cancel()
    }

    @Test
    fun chargesState_isSuccess_whenDataIsAvailable() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.charges.collect() }
        repository.updateChargesData(itemList)

        assertEquals(
            UiState.Success(itemList),
            viewModel.charges.value,
        )

        job.cancel()
    }

    @Test
    fun search_withInvalidData_returnEmptyResult() = runTest {
        repository.updateChargesData(itemList)
        viewModel.searchTextChanged("Invalid")

        advanceUntilIdle()

        viewModel.charges.test {
            assertEquals(UiState.Empty, awaitItem())
        }
    }

    @Test
    fun search_withValidData_returnResult() = runTest {
        repository.updateChargesData(itemList)
        viewModel.searchTextChanged("Discount")

        advanceUntilIdle()

        viewModel.charges.test {
            assertEquals(UiState.Success(itemList.searchCharges("Discount")), awaitItem())
        }
    }

    @Test
    fun search_onClose_returnAllData() = runTest {
        repository.updateChargesData(itemList)
        viewModel.searchTextChanged("Extra")

        advanceUntilIdle()

        viewModel.charges.test {
            assertEquals(UiState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.closeSearchBar()
        advanceUntilIdle()

        viewModel.charges.test {
            assertEquals(UiState.Success(itemList), awaitItem())
        }
    }

    @Test
    fun deleteCharges_shouldRemoveFromList() = runTest {
        repository.updateChargesData(itemList)
        val chargesId = itemList.first().chargesId

        viewModel.selectItem(chargesId)
        viewModel.deleteItems()

        advanceUntilIdle()

        viewModel.charges.test {
            assertEquals(UiState.Success(itemList.drop(1)), awaitItem())
        }

        assert(chargesId !in viewModel.selectedItems.toList())
    }

    @Test
    fun selectCharges_onSelectItem_shouldUpdateSelectedCharges() = runTest {
        repository.updateChargesData(itemList)

        val chargesId = itemList.first().chargesId
        viewModel.selectItem(chargesId)

        advanceUntilIdle()

        assertContains(viewModel.selectedItems.toList(), chargesId)
    }

    @Test
    fun selectAllItems_onSelectAllItems_shouldUpdateSelectedItems() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.charges.collect() }

        repository.updateChargesData(itemList)
        viewModel.selectAllItems()

        advanceUntilIdle()

        assertEquals(itemList.map { it.chargesId }, viewModel.selectedItems.toList())

        job.cancel()
    }

    @Test
    fun deselectItems_onDeselectItems_shouldClearSelectedItems() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.charges.collect() }
        repository.updateChargesData(itemList)

        viewModel.selectAllItems()
        assertEquals(viewModel.selectedItems.toList(), itemList.map { it.chargesId })

        advanceUntilIdle()

        viewModel.deselectItems()

        advanceUntilIdle()

        assertEquals(emptyList(), viewModel.selectedItems.toList())

        job.cancel()
    }
}
