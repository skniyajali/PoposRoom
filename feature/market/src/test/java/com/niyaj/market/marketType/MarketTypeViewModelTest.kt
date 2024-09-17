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

package com.niyaj.market.marketType

import app.cash.turbine.test
import com.niyaj.model.searchMarketType
import com.niyaj.testing.repository.TestMarketTypeRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.MarketTypePreviewData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class MarketTypeViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: MarketTypeViewModel
    private val repository = TestMarketTypeRepository()

    private val items = MarketTypePreviewData.marketTypes

    @Before
    fun setup() {
        viewModel = MarketTypeViewModel(repository, TestAnalyticsHelper())
    }

    @Test
    fun marketTypes_initialStateIsLoading() = runTest {
        assertEquals(UiState.Loading, viewModel.marketTypes.value)
    }

    @Test
    fun marketTypes_isEmpty_whenDataNotAvailable() = runTest {
        val job = launch { viewModel.marketTypes.collect() }
        advanceUntilIdle()

        viewModel.marketTypes.test {
            assertEquals(UiState.Empty, awaitItem())
        }
        job.cancel()
    }

    @Test
    fun marketTypes_populated_whenDataIsAvailable() = runTest {
        val job = launch { viewModel.marketTypes.collect() }

        repository.updateMarketTypeData(items)
        advanceUntilIdle()

        viewModel.marketTypes.test {
            assertEquals(UiState.Success(items), awaitItem())
            assertEquals(items.map { it.typeId }, viewModel.totalItems)
        }
        job.cancel()
    }

    @Test
    fun marketTypes_updated_whenSearchTextChanges() = runTest {
        val job = launch { viewModel.marketTypes.collect() }

        repository.updateMarketTypeData(items)
        viewModel.searchTextChanged("Dairy")

        advanceUntilIdle()

        viewModel.marketTypes.test {
            assertEquals(UiState.Success(items.searchMarketType("Dairy")), awaitItem())
        }
        job.cancel()
    }

    @Test
    fun deleteItems_withSelection_deleteSelectedItems() = runTest {
        val job = launch { viewModel.marketTypes.collect() }

        repository.updateMarketTypeData(items)
        advanceUntilIdle()

        viewModel.selectItem(2)
        viewModel.selectItem(4)
        viewModel.deleteItems()

        advanceUntilIdle()

        viewModel.marketTypes.test {
            assertEquals(
                UiState.Success(items.filterNot { it.typeId in listOf(2, 4) }),
                awaitItem(),
            )
        }
        job.cancel()
    }
}