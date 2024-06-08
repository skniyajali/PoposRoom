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

package com.niyaj.cartorder

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.CartOrderRepository
import com.niyaj.model.Selected
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartOrderViewModel @Inject constructor(
    private val cartOrderRepository: CartOrderRepository,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel() {

    override var totalItems: List<Int> = emptyList()

    private val showAllOrders = MutableStateFlow(false)

    val selectedId = cartOrderRepository.getSelectedCartOrder()
        .mapLatest {
            it?.orderId ?: 0
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0,
        )

    val cartOrders = snapshotFlow { mSearchText.value }.combine(showAllOrders) { text, viewAll ->
        cartOrderRepository.getAllCartOrders(text, viewAll)
    }
        .flatMapLatest { listFlow ->
            listFlow.mapLatest { list ->
                val data = list.sortedByDescending { it.orderId == selectedId.value }
                totalItems = data.map { it.orderId }
                data.groupBy { (it.updatedAt ?: it.createdAt).toPrettyDate() }
            }
        }
        .mapLatest { data ->
            if (data.isEmpty()) UiState.Empty else UiState.Success(data)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading,
        )

    fun selectCartOrder() {
        viewModelScope.launch {
            val result = cartOrderRepository.insertOrUpdateSelectedOrder(
                Selected(orderId = selectedItems.first()),
            )

            when (result) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                }

                is Resource.Success -> {
                    analyticsHelper.logSelectedCartOrder(selectedItems.first())

                    deselectItems()
                }
            }
        }
    }

    fun onClickViewAllOrder() {
        viewModelScope.launch {
            showAllOrders.value = !showAllOrders.value
        }
    }

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch {
            when (val result = cartOrderRepository.deleteCartOrders(selectedItems.toList())) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                }

                is Resource.Success -> {
                    mEventFlow.emit(
                        UiEvent.OnSuccess(
                            "${selectedItems.size} cart orders has been deleted",
                        ),
                    )
                    analyticsHelper.logDeletedCartOrder(selectedItems.toList())
                }
            }

            mSelectedItems.clear()
        }
    }
}

internal fun AnalyticsHelper.logSelectedCartOrder(cartOrderId: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "cart_order_selected",
            extras = listOf(
                com.niyaj.core.analytics.AnalyticsEvent.Param("cart_order_selected", cartOrderId.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logDeletedCartOrder(cartOrderId: List<Int>) {
    logEvent(
        event = AnalyticsEvent(
            type = "cart_order_deleted",
            extras = listOf(
                com.niyaj.core.analytics.AnalyticsEvent.Param("cart_order_deleted", cartOrderId.toString()),
            ),
        ),
    )
}
