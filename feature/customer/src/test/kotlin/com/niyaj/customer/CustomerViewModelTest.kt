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
import com.niyaj.domain.customer.DeleteCustomersUseCase
import com.niyaj.model.searchCustomer
import com.niyaj.testing.repository.TestCartOrderRepository
import com.niyaj.testing.repository.TestCustomerRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.CustomerPreviewData
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

class CustomerViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = CustomerPreviewData.customerList
    private val repository = TestCustomerRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private val cartOrderRepository = TestCartOrderRepository()
    private val deleteCustomersUseCase = DeleteCustomersUseCase(
        repository,
        cartOrderRepository,
        UnconfinedTestDispatcher(),
    )
    private lateinit var viewModel: CustomerViewModel

    @Before
    fun setup() {
        viewModel = CustomerViewModel(repository, deleteCustomersUseCase, analyticsHelper)
    }

    @Test
    fun customersState_initially_Loading() = runTest {
        assertEquals(UiState.Loading, viewModel.customers.value)
    }

    @Test
    fun customersState_isEmpty_whenDataIsEmpty() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.customers.collect() }

        assertEquals(UiState.Empty, viewModel.customers.value)

        job.cancel()
    }

    @Test
    fun customersState_isSuccess_whenDataIsAvailable() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.customers.collect() }
        repository.updateCustomerData(itemList)

        assertEquals(UiState.Success(itemList), viewModel.customers.value)

        job.cancel()
    }

    @Test
    fun search_withInvalidData_returnEmptyResult() = runTest {
        repository.updateCustomerData(itemList)
        viewModel.searchTextChanged("Invalid")

        advanceUntilIdle()

        viewModel.customers.test {
            assertEquals(UiState.Empty, awaitItem())
        }
    }

    @Test
    fun search_withValidData_returnResult() = runTest {
        repository.updateCustomerData(itemList)
        viewModel.searchTextChanged("Jane")

        advanceUntilIdle()

        viewModel.customers.test {
            assertEquals(UiState.Success(itemList.searchCustomer("Jane")), awaitItem())
        }
    }

    @Test
    fun search_onClose_returnAllData() = runTest {
        repository.updateCustomerData(itemList)
        viewModel.searchTextChanged("Extra")

        advanceUntilIdle()

        viewModel.customers.test {
            assertEquals(UiState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.closeSearchBar()
        advanceUntilIdle()

        viewModel.customers.test {
            assertEquals(UiState.Success(itemList), awaitItem())
        }
    }

    @Test
    fun deleteCharges_shouldRemoveFromList() = runTest {
        repository.updateCustomerData(itemList)
        val customersId = itemList.first().customerId

        viewModel.selectItem(customersId)
        viewModel.deleteItems()

        advanceUntilIdle()

        viewModel.customers.test {
            assertEquals(UiState.Success(itemList.drop(1)), awaitItem())
        }

        assert(customersId !in viewModel.selectedItems.toList())
    }

    @Test
    fun selectCharges_onSelectItem_shouldUpdateSelectedCharges() = runTest {
        repository.updateCustomerData(itemList)

        val customerId = itemList.first().customerId
        viewModel.selectItem(customerId)

        advanceUntilIdle()

        assertContains(viewModel.selectedItems.toList(), customerId)
    }

    @Test
    fun selectAllItems_onSelectAllItems_shouldUpdateSelectedItems() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.customers.collect() }

        repository.updateCustomerData(itemList)
        viewModel.selectAllItems()

        advanceUntilIdle()

        assertEquals(itemList.map { it.customerId }, viewModel.selectedItems.toList())

        job.cancel()
    }

    @Test
    fun deselectItems_onDeselectItems_shouldClearSelectedItems() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.customers.collect() }
        repository.updateCustomerData(itemList)

        viewModel.selectAllItems()
        assertEquals(viewModel.selectedItems.toList(), itemList.map { it.customerId })

        advanceUntilIdle()

        viewModel.deselectItems()

        advanceUntilIdle()

        assertEquals(emptyList(), viewModel.selectedItems.toList())

        job.cancel()
    }
}
