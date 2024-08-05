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

package com.niyaj.address

import app.cash.turbine.test
import com.niyaj.address.settings.AddressSettingsEvent
import com.niyaj.address.settings.AddressSettingsViewModel
import com.niyaj.model.searchAddress
import com.niyaj.testing.repository.TestAddressRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.ui.parameterProvider.AddressPreviewData
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

class AddressSettingsViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = AddressPreviewData.addressList
    private val repository = TestAddressRepository()
    private lateinit var viewModel: AddressSettingsViewModel

    @Before
    fun setup() {
        viewModel = AddressSettingsViewModel(repository)
    }

    @Test
    fun `when search text changes, addonItems are updated`() = runTest {
        repository.updateAddressData(itemList)

        viewModel.searchTextChanged("Extra")
        advanceUntilIdle()

        viewModel.addresses.test {
            assertEquals(itemList.searchAddress("Extra"), awaitItem())
        }
    }

    @Test
    fun `when GetExportedItems event is triggered with no selection, all items are exported`() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.addresses.collect() }

            repository.updateAddressData(itemList)

            viewModel.onEvent(AddressSettingsEvent.GetExportedItems)
            testScheduler.advanceUntilIdle()

            viewModel.exportedItems.test {
                assertEquals(itemList, awaitItem())
            }

            job.cancel()
        }

    @Test
    fun `when GetExportedItems event is triggered with selection, only selected items are exported`() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.addresses.collect() }

            repository.updateAddressData(itemList)

            viewModel.selectItem(1)
            viewModel.selectItem(3)

            viewModel.onEvent(AddressSettingsEvent.GetExportedItems)
            advanceUntilIdle()

            assertEquals(
                itemList.filter { it.addressId == 1 || it.addressId == 3 },
                viewModel.exportedItems.value,
            )

            job.cancel()
        }

    @Test
    fun `when OnImportAddOnItemsFromFile event is triggered, importedItems are updated`() =
        runTest {
            viewModel.onEvent(AddressSettingsEvent.OnImportAddressItemsFromFile(itemList))
            testScheduler.advanceUntilIdle()

            assertEquals(itemList, viewModel.importedItems.value)
        }

    @Test
    fun `when ImportAddOnItemsToDatabase event is triggered with no selection, all imported items are added`() =
        runTest {
            val job = launch { viewModel.addresses.collect() }

            viewModel.onEvent(AddressSettingsEvent.OnImportAddressItemsFromFile(itemList))
            assertEquals(itemList, viewModel.importedItems.value)
            viewModel.onEvent(AddressSettingsEvent.ImportAddressItemsToDatabase)
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

            viewModel.addresses.test {
                assertEquals(
                    itemList,
                    awaitItem(),
                )
            }

            job.cancel()
        }

    @Test
    fun `when ImportAddOnItemsToDatabase event is triggered with selection, only selected items are added`() =
        runTest {
            val job = launch { viewModel.addresses.collect() }

            viewModel.onEvent(AddressSettingsEvent.OnImportAddressItemsFromFile(itemList))

            assertEquals(itemList, viewModel.importedItems.value)

            viewModel.selectItem(2)
            viewModel.selectItem(4)

            viewModel.onEvent(AddressSettingsEvent.ImportAddressItemsToDatabase)
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

            viewModel.addresses.test {
                val event = awaitItem()
                assertEquals(itemList.filter { it.addressId == 2 || it.addressId == 4 }, event)
            }

            job.cancel()
        }
}
