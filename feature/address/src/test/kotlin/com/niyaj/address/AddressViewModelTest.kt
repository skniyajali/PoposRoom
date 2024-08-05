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
import com.niyaj.domain.address.DeleteAddressesUseCase
import com.niyaj.model.searchAddress
import com.niyaj.testing.repository.TestAddressRepository
import com.niyaj.testing.repository.TestCartOrderRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.AddressPreviewData
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

class AddressViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = AddressPreviewData.addressList
    private val repository = TestAddressRepository()
    private val cartOrderRepository = TestCartOrderRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private lateinit var viewModel: AddressViewModel
    private val deleteAddressesUseCase = DeleteAddressesUseCase(
        repository,
        cartOrderRepository,
        UnconfinedTestDispatcher(),
    )

    @Before
    fun setup() {
        viewModel = AddressViewModel(repository, deleteAddressesUseCase, analyticsHelper)
    }

    @Test
    fun addressState_initially_Loading() = runTest {
        assertEquals(
            UiState.Loading,
            viewModel.addresses.value,
        )
    }

    @Test
    fun addressState_isEmpty_whenDataIsEmpty() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.addresses.collect() }

        assertEquals(
            UiState.Empty,
            viewModel.addresses.value,
        )

        job.cancel()
    }

    @Test
    fun addressState_isSuccess_whenDataIsAvailable() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.addresses.collect() }
        repository.updateAddressData(itemList)

        assertEquals(
            UiState.Success(itemList),
            viewModel.addresses.value,
        )

        job.cancel()
    }

    @Test
    fun search_withInvalidData_returnEmptyResult() = runTest {
        repository.updateAddressData(itemList)
        viewModel.searchTextChanged("Invalid")

        advanceUntilIdle()

        viewModel.addresses.test {
            assertEquals(UiState.Empty, awaitItem())
        }
    }

    @Test
    fun search_withValidData_returnResult() = runTest {
        repository.updateAddressData(itemList)
        viewModel.searchTextChanged("Main")

        advanceUntilIdle()

        viewModel.addresses.test {
            assertEquals(UiState.Success(itemList.searchAddress("Main")), awaitItem())
        }
    }

    @Test
    fun search_onClose_returnAllData() = runTest {
        repository.updateAddressData(itemList)
        viewModel.searchTextChanged("Address")

        advanceUntilIdle()

        viewModel.addresses.test {
            assertEquals(UiState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.closeSearchBar()
        advanceUntilIdle()

        viewModel.addresses.test {
            assertEquals(UiState.Success(itemList), awaitItem())
        }
    }

    @Test
    fun deleteAddress_shouldRemoveFromList() = runTest {
        repository.updateAddressData(itemList)
        val addressId = itemList.first().addressId

        viewModel.selectItem(addressId)
        viewModel.deleteItems()

        advanceUntilIdle()

        viewModel.addresses.test {
            assertEquals(UiState.Success(itemList.drop(1)), awaitItem())
        }

        assert(addressId !in viewModel.selectedItems.toList())
    }

    @Test
    fun selectAddress_onSelectItem_shouldUpdateSelectedAddress() = runTest {
        repository.updateAddressData(itemList)

        val addressId = itemList.first().addressId
        viewModel.selectItem(addressId)

        advanceUntilIdle()

        assertContains(viewModel.selectedItems.toList(), addressId)
    }

    @Test
    fun selectAllItems_onSelectAllItems_shouldUpdateSelectedItems() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.addresses.collect() }

        repository.updateAddressData(itemList)
        viewModel.selectAllItems()

        advanceUntilIdle()

        assertEquals(itemList.map { it.addressId }, viewModel.selectedItems.toList())

        job.cancel()
    }

    @Test
    fun deselectItems_onDeselectItems_shouldClearSelectedItems() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.addresses.collect() }
        repository.updateAddressData(itemList)

        viewModel.selectAllItems()
        assertEquals(viewModel.selectedItems.toList(), itemList.map { it.addressId })

        advanceUntilIdle()

        viewModel.deselectItems()

        advanceUntilIdle()

        assertEquals(emptyList(), viewModel.selectedItems.toList())

        job.cancel()
    }
}
