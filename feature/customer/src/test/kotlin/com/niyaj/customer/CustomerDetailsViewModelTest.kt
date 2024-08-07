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

import androidx.lifecycle.SavedStateHandle
import com.niyaj.customer.details.CustomerDetailsViewModel
import com.niyaj.model.TotalOrderDetails
import com.niyaj.testing.repository.TestCustomerRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.CustomerPreviewData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class CustomerDetailsViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestCustomerRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private val savedStateHandle = SavedStateHandle()

    private lateinit var viewModel: CustomerDetailsViewModel

    @Before
    fun setup() {
        viewModel = CustomerDetailsViewModel(
            customerRepository = repository,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun `customerDetails is loading, initially`() = runTest {
        assertEquals(UiState.Loading, viewModel.customerDetails.value)
    }

    @Test
    fun `customerDetails is empty, when no customer found`() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.customerDetails.collect() }

        assertEquals(UiState.Empty, viewModel.customerDetails.value)

        job.cancel()
    }

    @Test
    fun `init with customerId loads customer details`() = runTest {
        val data = repository.createTestItem()
        viewModel.setCustomerId(data.customerId)

        val job = launch(UnconfinedTestDispatcher()) { viewModel.customerDetails.collect() }

        assertEquals(UiState.Success(data), viewModel.customerDetails.value)

        job.cancel()
    }

    @Test
    fun `orderDetails is loading, initially`() = runTest {
        assertEquals(UiState.Loading, viewModel.orderDetails.value)
    }

    @Test
    fun `orderDetails is empty, when no orders found`() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.orderDetails.collect() }

        assertEquals(UiState.Empty, viewModel.orderDetails.value)
        assertEquals(TotalOrderDetails(), viewModel.totalOrders.value)

        job.cancel()
    }

    @Test
    fun `orderDetails populated, when orders found`() = runTest {
        val data = repository.createTestItem()
        val orders = CustomerPreviewData.customerWiseOrders.take(5)
        repository.updateCustomerWiseOrderData(orders)
        viewModel.setCustomerId(data.customerId)

        val job = launch(UnconfinedTestDispatcher()) { viewModel.orderDetails.collect() }

        assertEquals(UiState.Success(orders), viewModel.orderDetails.value)

        val totalAmount = orders.sumOf { it.totalPrice }
        assertEquals(totalAmount, viewModel.totalOrders.value.totalAmount)
        assertEquals(orders.size, viewModel.totalOrders.value.totalOrder)

        job.cancel()
    }
}
