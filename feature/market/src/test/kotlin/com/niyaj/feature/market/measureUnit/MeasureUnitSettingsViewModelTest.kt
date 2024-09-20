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

package com.niyaj.feature.market.measureUnit

import app.cash.turbine.test
import com.niyaj.feature.market.measureUnit.settings.MeasureUnitSettingsEvent
import com.niyaj.feature.market.measureUnit.settings.MeasureUnitSettingsViewModel
import com.niyaj.model.searchMeasureUnit
import com.niyaj.testing.repository.TestMeasureUnitRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.parameterProvider.MeasureUnitPreviewData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class MeasureUnitSettingsViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = MeasureUnitPreviewData.measureUnits
    private val repository = TestMeasureUnitRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private lateinit var viewModel: MeasureUnitSettingsViewModel

    @Before
    fun setup() {
        viewModel = MeasureUnitSettingsViewModel(repository, analyticsHelper)
    }

    @Test
    fun items_initialIsEmpty() = runTest {
        assert(viewModel.items.value.isEmpty())
    }

    @Test
    fun items_populated_whenAvailable() = runTest {
        val job = launch { viewModel.items.collect() }

        repository.updateMeasureUnitData(itemList)
        advanceUntilIdle()

        assert(viewModel.items.value.isNotEmpty())
        assertEquals(itemList, viewModel.items.value)

        job.cancel()
    }

    @Test
    fun items_updated_whenSearchTextChanged() = runTest {
        val job = launch { viewModel.items.collect() }

        repository.updateMeasureUnitData(itemList)
        viewModel.searchTextChanged("Inch")

        advanceUntilIdle()

        assert(viewModel.items.value.isNotEmpty())
        assertEquals(itemList.searchMeasureUnit("Inch"), viewModel.items.value)

        job.cancel()
    }

    @Test
    fun exportedItems_initialIsEmpty() = runTest {
        assert(viewModel.exportedItems.value.isEmpty())
    }

    @Test
    fun exportedItems_allItemsExported_whenNoSelection() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.items.collect() }

        repository.updateMeasureUnitData(itemList)

        viewModel.onEvent(MeasureUnitSettingsEvent.GetExportedItems)
        advanceUntilIdle()

        viewModel.exportedItems.test {
            assertEquals(itemList, awaitItem())
        }

        job.cancel()
    }

    @Test
    fun exportedItems_selectedItemsExported_whenSelection() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.items.collect() }
        repository.updateMeasureUnitData(itemList)

        viewModel.selectItem(1)
        viewModel.selectItem(3)

        viewModel.onEvent(MeasureUnitSettingsEvent.GetExportedItems)
        advanceUntilIdle()

        viewModel.exportedItems.test {
            assertEquals(
                itemList.filter { it.unitId == 1 || it.unitId == 3 },
                awaitItem(),
            )
        }

        job.cancel()
    }

    @Test
    fun importedItems_initialIsEmpty() = runTest {
        assert(viewModel.importedItems.value.isEmpty())
    }

    @Test
    fun importedItems_updated_whenOnImportItemsFromFile() = runTest {
        viewModel.onEvent(MeasureUnitSettingsEvent.OnImportItemsFromFile(itemList))
        advanceUntilIdle()

        assertEquals(itemList, viewModel.importedItems.value)
        assertEquals(itemList.size, viewModel.totalItems.size)
        assertEquals(itemList.map { it.unitId }, viewModel.totalItems)
    }

    @Test
    fun importedItems_allItemsAdded_whenNoSelection() = runTest {
        val job = launch { viewModel.items.collect() }

        viewModel.onEvent(MeasureUnitSettingsEvent.OnImportItemsFromFile(itemList))
        assertEquals(itemList, viewModel.importedItems.value)

        viewModel.onEvent(MeasureUnitSettingsEvent.ImportItemsToDatabase)
        advanceUntilIdle()

        viewModel.items.test {
            assertEquals(itemList, awaitItem())
        }

        job.cancel()
    }

    @Test
    fun importedItems_selectedItemsAdded_whenSelection() = runTest {
        val job = launch { viewModel.items.collect() }

        viewModel.onEvent(MeasureUnitSettingsEvent.OnImportItemsFromFile(itemList))
        assertEquals(itemList, viewModel.importedItems.value)

        viewModel.selectItem(1)
        viewModel.selectItem(3)

        viewModel.onEvent(MeasureUnitSettingsEvent.ImportItemsToDatabase)
        advanceUntilIdle()

        viewModel.items.test {
            assertEquals(itemList.filter { it.unitId == 1 || it.unitId == 3 }, awaitItem())
        }

        job.cancel()
    }
}
