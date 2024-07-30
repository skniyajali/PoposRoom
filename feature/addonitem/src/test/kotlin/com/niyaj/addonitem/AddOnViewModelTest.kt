/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.addonitem

import app.cash.turbine.test
import com.niyaj.model.searchAddOnItem
import com.niyaj.testing.repository.TestAddOnItemRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.AddOnPreviewData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class AddOnViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = AddOnPreviewData.addOnItemList
    private val repository = TestAddOnItemRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private lateinit var viewModel: AddOnViewModel

    @Before
    fun setup() {
        viewModel = AddOnViewModel(repository, analyticsHelper)
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(
            UiState.Loading,
            viewModel.addOnItems.value,
        )
    }

    @Test
    fun stateIsEmptyAfterLoading() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.addOnItems.collect() }

        assertEquals(
            UiState.Empty,
            viewModel.addOnItems.value,
        )

        job.cancel()
    }

    @Test
    fun stateIsSuccessAfterLoading() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.addOnItems.collect() }
        repository.updateAddOnData(itemList)

        assertEquals(
            UiState.Success(itemList),
            viewModel.addOnItems.value,
        )

        job.cancel()
    }

    @Test
    fun performSearchAndReturnEmptyResult() = runTest {
        repository.updateAddOnData(itemList)

        viewModel.searchTextChanged("Some Items")
        advanceUntilIdle()

        viewModel.addOnItems.test {
            assertEquals(UiState.Empty, awaitItem())
        }
    }

    @Test
    fun performSearchAndReturnResult() = runTest {
        repository.updateAddOnData(itemList)

        viewModel.searchTextChanged("Avocado")
        advanceUntilIdle()

        viewModel.addOnItems.test {
            assertEquals(
                UiState.Success(itemList.searchAddOnItem("Avocado")),
                awaitItem(),
            )
        }
    }

    @Test
    fun deleteItemsShouldRemovedFromSelectedItemsAndList() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.addOnItems.collect() }
        repository.updateAddOnData(itemList)

        viewModel.selectItem(2)
        viewModel.selectItem(3)

        viewModel.deleteItems()

        assertEquals(
            emptyList(),
            viewModel.mSelectedItems,
        )

        assertEquals(
            UiState.Success(itemList.filter { it.itemId != 2 && it.itemId != 3 }),
            viewModel.addOnItems.value,
        )

        job.cancel()
    }
}
