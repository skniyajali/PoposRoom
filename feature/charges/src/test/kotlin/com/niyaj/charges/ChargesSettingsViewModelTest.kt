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
import com.niyaj.charges.settings.ChargesSettingsEvent
import com.niyaj.charges.settings.ChargesSettingsViewModel
import com.niyaj.model.searchCharges
import com.niyaj.testing.repository.TestChargesRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.parameterProvider.ChargesPreviewData
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

class ChargesSettingsViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = ChargesPreviewData.chargesList
    private val repository = TestChargesRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private lateinit var viewModel: ChargesSettingsViewModel

    @Before
    fun setup() {
        viewModel = ChargesSettingsViewModel(repository, analyticsHelper)
    }

    @Test
    fun `when search text changes, charges are updated`() = runTest {
        repository.updateChargesData(itemList)

        viewModel.searchTextChanged("Discount")
        advanceUntilIdle()

        viewModel.charges.test {
            assertEquals(itemList.searchCharges("Discount"), awaitItem())
        }
    }

    @Test
    fun `when GetExportedItems event is triggered with no selection, all items are exported`() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.charges.collect() }

            repository.updateChargesData(itemList)

            viewModel.onEvent(ChargesSettingsEvent.GetExportedItems)
            testScheduler.advanceUntilIdle()

            viewModel.exportedItems.test {
                assertEquals(itemList, awaitItem())
            }

            job.cancel()
        }

    @Test
    fun `when GetExportedItems event is triggered with selection, only selected items are exported`() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.charges.collect() }

            repository.updateChargesData(itemList)

            viewModel.selectItem(1)
            viewModel.selectItem(3)

            viewModel.onEvent(ChargesSettingsEvent.GetExportedItems)
            advanceUntilIdle()

            assertEquals(
                itemList.filter { it.chargesId == 1 || it.chargesId == 3 },
                viewModel.exportedItems.value,
            )

            job.cancel()
        }

    @Test
    fun `when OnImportChargesFromFile event is triggered, importedItems are updated`() =
        runTest {
            viewModel.onEvent(ChargesSettingsEvent.OnImportChargesItemsFromFile(itemList))
            testScheduler.advanceUntilIdle()

            assertEquals(itemList, viewModel.importedItems.value)
        }

    @Test
    fun `when ImportChargesToDatabase event is triggered with no selection, all imported items are added`() =
        runTest {
            val job = launch { viewModel.charges.collect() }

            viewModel.onEvent(ChargesSettingsEvent.OnImportChargesItemsFromFile(itemList))
            assertEquals(itemList, viewModel.importedItems.value)
            viewModel.onEvent(ChargesSettingsEvent.ImportChargesItemsToDatabase)
            advanceUntilIdle()

            viewModel.eventFlow.test {
                val event = awaitItem()
                assertTrue(event is UiEvent.OnSuccess)
                assertEquals(
                    "${itemList.size} items has been imported successfully",
                    (event as UiEvent.OnSuccess).successMessage,
                )
            }

            advanceUntilIdle()

            viewModel.charges.test {
                assertEquals(itemList, awaitItem())
            }

            job.cancel()
        }

    @Test
    fun `when ImportChargesToDatabase event is triggered with selection, only selected items are added`() =
        runTest {
            val job = launch { viewModel.charges.collect() }

            viewModel.onEvent(ChargesSettingsEvent.OnImportChargesItemsFromFile(itemList))

            assertEquals(itemList, viewModel.importedItems.value)

            viewModel.selectItem(2)
            viewModel.selectItem(4)

            viewModel.onEvent(ChargesSettingsEvent.ImportChargesItemsToDatabase)
            advanceUntilIdle()

            viewModel.eventFlow.test {
                val event = awaitItem()
                assertTrue(event is UiEvent.OnSuccess)
                assertEquals(
                    "2 items has been imported successfully",
                    (event as UiEvent.OnSuccess).successMessage,
                )
            }

            advanceUntilIdle()

            viewModel.charges.test {
                val event = awaitItem()
                assertEquals(itemList.filter { it.chargesId == 2 || it.chargesId == 4 }, event)
            }

            job.cancel()
        }
}
