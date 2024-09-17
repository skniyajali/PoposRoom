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

package com.niyaj.market.measureUnit

import com.niyaj.model.searchMeasureUnit
import com.niyaj.testing.repository.TestMeasureUnitRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.MeasureUnitPreviewData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class MeasureUnitViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: MeasureUnitViewModel
    private val repository = TestMeasureUnitRepository()

    @Before
    fun setup() {
        viewModel = MeasureUnitViewModel(repository, TestAnalyticsHelper())
    }

    // Add test cases here
    @Test
    fun measureUnits_initialIsLoading() = runTest {
        assertEquals(UiState.Loading, viewModel.measureUnits.value)
    }

    @Test
    fun measureUnits_isEmpty_whenNotAvailable() = runTest {
        val job = launch { viewModel.measureUnits.collect() }
        advanceUntilIdle()

        assertEquals(UiState.Empty, viewModel.measureUnits.value)

        job.cancel()
    }

    @Test
    fun measureUnits_isSuccess_whenAvailable() = runTest {
        val job = launch { viewModel.measureUnits.collect() }
        val data = MeasureUnitPreviewData.measureUnits
        repository.updateMeasureUnitData(data)

        advanceUntilIdle()

        assertEquals(UiState.Success(data), viewModel.measureUnits.value)

        job.cancel()
    }

    @Test
    fun measureUnits_updated_whenSearchTextChanged() = runTest {
        val job = launch { viewModel.measureUnits.collect() }

        val data = MeasureUnitPreviewData.measureUnits
        repository.updateMeasureUnitData(data)

        viewModel.searchTextChanged("Meter")

        advanceUntilIdle()

        assertEquals(
            UiState.Success(data.searchMeasureUnit("Meter")),
            viewModel.measureUnits.value,
        )

        job.cancel()
    }

    @Test
    fun measureUnits_deleted_whenDeleteItems() = runTest {
        val job = launch { viewModel.measureUnits.collect() }

        val data = MeasureUnitPreviewData.measureUnits
        repository.updateMeasureUnitData(data)

        viewModel.selectItem(3)
        viewModel.selectItem(4)
        viewModel.deleteItems()

        advanceUntilIdle()

        val items = data.filterNot { it.unitId in listOf(3, 4) }

        assertEquals(UiState.Success(items), viewModel.measureUnits.value)

        job.cancel()
    }
}