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

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.niyaj.address.details.AddressDetailsViewModel
import com.niyaj.testing.repository.TestAddressRepository
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
import org.junit.Test
import kotlin.test.assertEquals

class AddressDetailsViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val orderDetails = AddressPreviewData.sampleAddressWiseOrders.take(4)
    private val repository = TestAddressRepository()

    private lateinit var viewModel: AddressDetailsViewModel

    @Before
    fun setup() {
        viewModel = AddressDetailsViewModel(
            repository,
            TestAnalyticsHelper(),
            SavedStateHandle(mapOf("addressId" to 1)),
        )
    }

    @Test
    fun `address details should be loading`() = runTest {
        assert(viewModel.addressDetails.value is UiState.Loading)
    }

    @Test
    fun `address details should be empty, when address id is invalid`() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.addressDetails.collect() }
        advanceUntilIdle()

        viewModel.addressDetails.test {
            assert(awaitItem() is UiState.Empty)
        }
        job.cancel()
    }

    @Test
    fun `address details should be success, when address id is valid`() = runTest {
        val data = repository.createTestAddress()
        val job = launch(UnconfinedTestDispatcher()) { viewModel.addressDetails.collect() }

        advanceUntilIdle()

        val item = viewModel.addressDetails.value
        assert(item is UiState.Success)
        assertEquals(data.addressId, (item as UiState.Success).data.addressId)
        assertEquals(data.addressName, item.data.addressName)
        assertEquals(data.shortName, item.data.shortName)

        job.cancel()
    }

    @Test
    fun `order details should be loading`() = runTest {
        assert(viewModel.orderDetails.value is UiState.Loading)
    }

    @Test
    fun `order details should be empty, when order details are empty`() = runTest {
        val job = launch { viewModel.orderDetails.collect() }

        advanceUntilIdle()

        viewModel.orderDetails.test {
            assert(awaitItem() is UiState.Empty)
        }

        job.cancel()
    }

    @Test
    fun `order details should be success, when order details are available`() = runTest {
        repository.updateAddressWiseOrders(orderDetails)
        val job = launch(UnconfinedTestDispatcher()) { viewModel.orderDetails.collect() }

        advanceUntilIdle()

        assert(viewModel.orderDetails.value is UiState.Success)
        assert(viewModel.totalOrders.value.totalAmount == orderDetails.sumOf { it.totalPrice })
        assert(viewModel.totalOrders.value.totalOrder == orderDetails.size)
        assertEquals(orderDetails, (viewModel.orderDetails.value as UiState.Success).data)

        job.cancel()
    }
}
