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
import com.niyaj.addonitem.settings.AddOnSettingsEvent
import com.niyaj.addonitem.settings.AddOnSettingsViewModel
import com.niyaj.model.searchAddOnItem
import com.niyaj.testing.repository.TestAddOnItemRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.parameterProvider.AddOnPreviewData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class AddOnSettingsViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = AddOnPreviewData.addOnItemList
    private val repository = TestAddOnItemRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private lateinit var viewModel: AddOnSettingsViewModel

    @Before
    fun setup() {
        viewModel = AddOnSettingsViewModel(repository, analyticsHelper)
    }

    @Test
    fun `when search text changes, addonItems are updated`() = runTest {
        repository.updateAddOnData(itemList)

        viewModel.searchTextChanged("Extra")
        advanceUntilIdle()

        viewModel.addonItems.test {
            assertEquals(itemList.searchAddOnItem("Extra"), awaitItem())
        }
    }

    @Test
    fun `when GetExportedItems event is triggered with no selection, all items are exported`() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.addonItems.collect() }

            repository.updateAddOnData(itemList)

            viewModel.onEvent(AddOnSettingsEvent.GetExportedItems)
            testScheduler.advanceUntilIdle()

            viewModel.exportedItems.test {
                assertEquals(itemList, awaitItem())
            }

            job.cancel()
        }

    @Test
    fun `when GetExportedItems event is triggered with selection, only selected items are exported`() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.addonItems.collect() }

            repository.updateAddOnData(itemList)

            viewModel.selectItem(1)
            viewModel.selectItem(3)

            viewModel.onEvent(AddOnSettingsEvent.GetExportedItems)
            testScheduler.advanceUntilIdle()

            assertEquals(
                itemList.filter { it.itemId == 1 || it.itemId == 3 },
                viewModel.exportedItems.value,
            )

            job.cancel()
        }

    @Test
    fun `when OnImportAddOnItemsFromFile event is triggered, importedItems are updated`() =
        runTest {
            viewModel.onEvent(AddOnSettingsEvent.OnImportAddOnItemsFromFile(itemList))
            testScheduler.advanceUntilIdle()

            assertEquals(itemList, viewModel.importedItems.value)
        }

    @Test
    fun `when ImportAddOnItemsToDatabase event is triggered with no selection, all imported items are added`() =
        runTest {
            viewModel.onEvent(AddOnSettingsEvent.OnImportAddOnItemsFromFile(itemList))

            assertEquals(itemList, viewModel.importedItems.value)

            viewModel.onEvent(AddOnSettingsEvent.ImportAddOnItemsToDatabase)

            // TODO:: Fix shared flow test
            /*
            viewModel.mEventFlow.test {
                val event = awaitItem()
                assertTrue(event is UiEvent.OnSuccess)
                assertEquals(
                    "2 items has been imported successfully",
                    event.successMessage,
                )
            }
             */

            viewModel.addonItems.test {
                assertEquals(
                    itemList,
                    awaitItem(),
                )
            }
        }

    @Test
    fun `when ImportAddOnItemsToDatabase event is triggered with selection, only selected items are added`() =
        runTest {
            viewModel.onEvent(AddOnSettingsEvent.OnImportAddOnItemsFromFile(itemList))

            assertEquals(itemList, viewModel.importedItems.value)

            viewModel.selectItem(2)
            viewModel.selectItem(4)

            viewModel.onEvent(AddOnSettingsEvent.ImportAddOnItemsToDatabase)
            advanceUntilIdle()

            viewModel.addonItems.test {
                val event = awaitItem()
                assertEquals(itemList.filter { it.itemId == 2 || it.itemId == 4 }, event)
            }
        }
}
