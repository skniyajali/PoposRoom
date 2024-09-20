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

package com.niyaj.market.marketItem

import app.cash.turbine.test
import com.niyaj.model.searchMarketItems
import com.niyaj.testing.repository.TestMarketItemRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.MarketItemPreviewData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class MarketItemViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: MarketItemViewModel
    private val repository = TestMarketItemRepository()

    private val items = MarketItemPreviewData.marketItems

    @Before
    fun setup() {
        viewModel = MarketItemViewModel(repository, TestAnalyticsHelper())
    }

    @Test
    fun marketItems_initialStateIsLoading() = runTest {
        assertEquals(UiState.Loading, viewModel.marketItems.value)
    }

    @Test
    fun marketItems_isEmpty_whenDataNotAvailable() = runTest {
        val job = launch { viewModel.marketItems.collect() }
        advanceUntilIdle()

        viewModel.marketItems.test {
            assertEquals(UiState.Empty, awaitItem())
        }
        job.cancel()
    }

    @Test
    fun marketItems_populated_whenDataIsAvailable() = runTest {
        val job = launch { viewModel.marketItems.collect() }

        repository.updateMarketItemData(items)
        advanceUntilIdle()

        viewModel.marketItems.test {
            assertEquals(UiState.Success(items), awaitItem())
            assertEquals(items.map { it.itemId }, viewModel.totalItems)
        }
        job.cancel()
    }

    @Test
    fun marketItems_updated_whenSearchTextChanges() = runTest {
        val job = launch { viewModel.marketItems.collect() }

        repository.updateMarketItemData(items)
        viewModel.searchTextChanged("Carrots")

        advanceUntilIdle()

        viewModel.marketItems.test {
            assertEquals(UiState.Success(items.searchMarketItems("Carrots")), awaitItem())
        }
        job.cancel()
    }

    @Test
    fun deleteItems_withSelection_deleteSelectedItems() = runTest {
        val job = launch { viewModel.marketItems.collect() }

        repository.updateMarketItemData(items)
        advanceUntilIdle()

        viewModel.selectItem(2)
        viewModel.selectItem(4)
        viewModel.deleteItems()

        advanceUntilIdle()

        viewModel.marketItems.test {
            assertEquals(
                UiState.Success(items.filterNot { it.itemId in listOf(2, 4) }),
                awaitItem(),
            )
        }
        job.cancel()
    }
}