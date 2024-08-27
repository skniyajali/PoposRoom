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

package com.niyaj.order.details

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.OrderRepository
import com.niyaj.ui.event.ShareViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class OrderDetailsViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    savedStateHandle: SavedStateHandle,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
    private val analyticsHelper: AnalyticsHelper,
) : ShareViewModel(ioDispatcher) {

    private val orderId = savedStateHandle.get<Int>("orderId") ?: 0

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val orderDetails = snapshotFlow { orderId }.flatMapLatest { data ->
        orderRepository.getOrderDetails(data).map {
            if (it.cartOrder.orderId == 0) {
                UiState.Empty
            } else {
                UiState.Success(it)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    val charges = snapshotFlow { orderId }.flatMapLatest {
        orderRepository.getAllCharges()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val deliveryPartners = snapshotFlow { orderId }.flatMapLatest {
        orderRepository.getDeliveryPartners()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    fun updateDeliveryPartner(partnerId: Int) {
        viewModelScope.launch {
            when (val result = orderRepository.updateDeliveryPartner(orderId, partnerId)) {
                is Resource.Error -> {
                    _eventFlow.emit(
                        UiEvent.OnError(result.message.toString()),
                    )
                }
                is Resource.Success -> {
                    _eventFlow.emit(
                        UiEvent.OnSuccess("Partner Updated Successfully"),
                    )
                }
            }
        }
    }

    init {
        savedStateHandle.get<Int>("orderId")?.let {
            analyticsHelper.logOrderDetailsViewed(it)
        }
    }
}

internal fun AnalyticsHelper.logOrderDetailsViewed(orderId: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "order_details_viewed",
            extras = listOf(
                com.niyaj.core.analytics.AnalyticsEvent.Param("order_details_viewed", orderId.toString()),
            ),
        ),
    )
}
