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

package com.niyaj.customer.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.CustomerRepository
import com.niyaj.model.TotalOrderDetails
import com.niyaj.ui.event.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.annotations.TestOnly
import javax.inject.Inject

@HiltViewModel
class CustomerDetailsViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val customerId = savedStateHandle.getStateFlow("customerId", 0)

    init {
        savedStateHandle.get<Int>("customerId")?.let {
            analyticsHelper.logViewCustomerDetails(it)
        }
    }

    private val _totalOrders = MutableStateFlow(TotalOrderDetails())
    val totalOrders = _totalOrders.asStateFlow()

    val customerDetails = customerId.mapLatest {
        val data = customerRepository.getCustomerById(it).data

        if (data == null) UiState.Empty else UiState.Success(data)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    val orderDetails = customerId.flatMapLatest { customerId ->
        customerRepository.getCustomerWiseOrders(customerId).mapLatest { orders ->
            if (orders.isEmpty()) {
                UiState.Empty
            } else {
                val startDate = orders.first().updatedAt
                val endDate = orders.last().updatedAt
                val repeatedOrder = orders.groupingBy { it.customerAddress }
                    .eachCount()
                    .filter { it.value > 1 }.size

                _totalOrders.value = _totalOrders.value.copy(
                    totalAmount = orders.sumOf { it.totalPrice },
                    totalOrder = orders.size,
                    repeatedOrder = repeatedOrder,
                    datePeriod = Pair(startDate, endDate),
                )

                UiState.Success(orders)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    @TestOnly
    internal fun setCustomerId(customerId: Int) {
        savedStateHandle["customerId"] = customerId
    }
}

internal fun AnalyticsHelper.logViewCustomerDetails(customerId: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "customer_details_viewed",
            extras = listOf(
                com.niyaj.core.analytics.AnalyticsEvent.Param("customer_details_viewed", customerId.toString()),
            ),
        ),
    )
}
