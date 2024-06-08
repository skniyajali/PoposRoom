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

package com.niyaj.order

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.OrderRepository
import com.niyaj.model.Charges
import com.niyaj.model.OrderDetails
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel() {

    private val _selectedDate = MutableStateFlow("")
    val selectedDate = _selectedDate.asStateFlow()

    val text = snapshotFlow { mSearchText.value }

    val dineInOrders = _selectedDate.combine(text) { date, text ->
        orderRepository.getDineInOrders(date, text)
    }.flatMapLatest {
        it.map { items ->
            if (items.isEmpty()) OrderState.Empty else OrderState.Success(items)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = OrderState.Loading,
    )

    val dineOutOrders = _selectedDate.combine(text) { date, text ->
        orderRepository.getDineOutOrders(date, text)
    }.flatMapLatest {
        it.map { items ->
            if (items.isEmpty()) OrderState.Empty else OrderState.Success(items)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = OrderState.Loading,
    )

    fun onOrderEvent(event: OrderEvent) {
        when (event) {
            is OrderEvent.DeleteOrder -> {
                viewModelScope.launch {
                    when (orderRepository.deleteOrder(event.orderId)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError("Unable to delete order"))
                        }

                        is Resource.Success -> {
                            analyticsHelper.logDeletedOrder(event.orderId)
                            mEventFlow.emit(UiEvent.OnSuccess("Order deleted successfully"))
                        }
                    }
                }
            }

            is OrderEvent.MarkedAsProcessing -> {
                viewModelScope.launch {
                    when (orderRepository.markOrderAsProcessing(event.orderId)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError("Unable to mark order as processing"))
                        }

                        is Resource.Success -> {
                            analyticsHelper.logOrderMarkedAsProcessing(event.orderId)
                            mEventFlow.emit(UiEvent.OnSuccess("Order marked as processing"))
                        }
                    }
                }
            }

            is OrderEvent.SelectDate -> {
                viewModelScope.launch {
                    _selectedDate.value = event.date
                }
            }

            is OrderEvent.GetShareableDetails -> {
                getOrderDetails(event.orderId)
                getCharges()
            }
        }
    }

    private val _orderDetails = MutableStateFlow<UiState<OrderDetails>>(UiState.Loading)
    val orderDetails = _orderDetails.asStateFlow()

    private val _charges = MutableStateFlow<List<Charges>>(emptyList())
    val charges = _charges.asStateFlow()

    private fun getOrderDetails(orderId: Int) {
        viewModelScope.launch {
            orderRepository.getOrderDetails(orderId).collectLatest { data ->
                if (data.cartOrder.orderId == 0) {
                    _orderDetails.update { UiState.Empty }
                } else {
                    _orderDetails.update { UiState.Success(data) }
                }
            }
        }
    }

    private fun getCharges() {
        viewModelScope.launch {
            orderRepository.getAllCharges().collectLatest { data ->
                if (data.isNotEmpty()) {
                    _charges.update { data }
                }
            }
        }
    }
}

internal fun AnalyticsHelper.logDeletedOrder(orderId: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "order_has_been_deleted",
            extras = listOf(
                com.niyaj.core.analytics.AnalyticsEvent.Param("order_has_been_deleted", orderId.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logOrderMarkedAsProcessing(orderId: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "order_marked_as_processing",
            extras = listOf(
                com.niyaj.core.analytics.AnalyticsEvent.Param("order_marked_as_processing", orderId.toString()),
            ),
        ),
    )
}
