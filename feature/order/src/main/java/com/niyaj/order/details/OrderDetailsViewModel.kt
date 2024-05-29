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
import com.niyaj.data.repository.OrderRepository
import com.niyaj.ui.event.ShareViewModel
import com.niyaj.ui.event.UiState
import com.samples.apps.core.analytics.AnalyticsEvent
import com.samples.apps.core.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class OrderDetailsViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    savedStateHandle: SavedStateHandle,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
    private val analyticsHelper: AnalyticsHelper,
) : ShareViewModel(ioDispatcher, analyticsHelper) {

    private val orderId = savedStateHandle.get<Int>("orderId") ?: 0

    val orderDetails = snapshotFlow { orderId }.flatMapLatest { it ->
        orderRepository.getOrderDetails(it).map {
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
                AnalyticsEvent.Param("order_details_viewed", orderId.toString()),
            ),
        ),
    )
}
