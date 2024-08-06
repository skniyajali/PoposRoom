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

package com.niyaj.customer

import app.cash.turbine.test
import com.niyaj.customer.settings.CustomerSettingsEvent
import com.niyaj.customer.settings.CustomerSettingsViewModel
import com.niyaj.model.searchCustomer
import com.niyaj.testing.repository.TestCustomerRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.parameterProvider.CustomerPreviewData
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

class CustomerSettingsViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = CustomerPreviewData.customerList
    private val repository = TestCustomerRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private lateinit var viewModel: CustomerSettingsViewModel

    @Before
    fun setup() {
        viewModel = CustomerSettingsViewModel(repository, analyticsHelper)
    }

    @Test
    fun `when search text changes, charges are updated`() = runTest {
        repository.updateCustomerData(itemList)

        viewModel.searchTextChanged("Jane")
        advanceUntilIdle()

        viewModel.customers.test {
            assertEquals(itemList.searchCustomer("Jane"), awaitItem())
        }
    }

    @Test
    fun `when GetExportedItems event is triggered with no selection, all items are exported`() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.customers.collect() }

            repository.updateCustomerData(itemList)

            viewModel.onEvent(CustomerSettingsEvent.GetExportedItems)
            testScheduler.advanceUntilIdle()

            viewModel.exportedItems.test {
                assertEquals(itemList, awaitItem())
            }

            job.cancel()
        }

    @Test
    fun `when GetExportedItems event is triggered with selection, only selected items are exported`() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.customers.collect() }

            repository.updateCustomerData(itemList)

            viewModel.selectItem(1)
            viewModel.selectItem(3)

            viewModel.onEvent(CustomerSettingsEvent.GetExportedItems)
            advanceUntilIdle()

            assertEquals(
                itemList.filter { it.customerId == 1 || it.customerId == 3 },
                viewModel.exportedItems.value,
            )

            job.cancel()
        }

    @Test
    fun `when OnImportChargesFromFile event is triggered, importedItems are updated`() =
        runTest {
            viewModel.onEvent(CustomerSettingsEvent.OnImportCustomerItemsFromFile(itemList))
            testScheduler.advanceUntilIdle()

            assertEquals(itemList, viewModel.importedItems.value)
        }

    @Test
    fun `when ImportChargesToDatabase event is triggered with no selection, all imported items are added`() =
        runTest {
            val job = launch { viewModel.customers.collect() }

            viewModel.onEvent(CustomerSettingsEvent.OnImportCustomerItemsFromFile(itemList))
            assertEquals(itemList, viewModel.importedItems.value)
            viewModel.onEvent(CustomerSettingsEvent.ImportCustomerItemsToDatabase)
            advanceUntilIdle()

            viewModel.eventFlow.test {
                val event = awaitItem()
                assertTrue(event is UiEvent.OnSuccess)
                assertEquals(
                    "${itemList.size} customers has been imported successfully",
                    (event as UiEvent.OnSuccess).successMessage,
                )
            }

            advanceUntilIdle()

            viewModel.customers.test {
                assertEquals(itemList, awaitItem())
            }

            job.cancel()
        }

    @Test
    fun `when ImportChargesToDatabase event is triggered with selection, only selected items are added`() =
        runTest {
            val job = launch { viewModel.customers.collect() }

            viewModel.onEvent(CustomerSettingsEvent.OnImportCustomerItemsFromFile(itemList))

            assertEquals(itemList, viewModel.importedItems.value)

            viewModel.selectItem(2)
            viewModel.selectItem(4)

            viewModel.onEvent(CustomerSettingsEvent.ImportCustomerItemsToDatabase)
            advanceUntilIdle()

            viewModel.eventFlow.test {
                val event = awaitItem()
                assertTrue(event is UiEvent.OnSuccess)
                assertEquals(
                    "2 customers has been imported successfully",
                    (event as UiEvent.OnSuccess).successMessage,
                )
            }

            advanceUntilIdle()

            viewModel.customers.test {
                val event = awaitItem()
                assertEquals(itemList.filter { it.customerId == 2 || it.customerId == 4 }, event)
            }

            job.cancel()
        }
}
