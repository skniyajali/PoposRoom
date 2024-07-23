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

package com.niyaj.cartSelected

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.CartOrderRepository
import com.niyaj.data.repository.CartRepository
import com.niyaj.data.repository.OrderRepository
import com.niyaj.model.CartItem
import com.niyaj.model.CartOrder
import com.niyaj.model.OrderDetails
import com.niyaj.model.SELECTED_ID
import com.niyaj.model.Selected
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectedViewModel @Inject constructor(
    private val cartOrderRepository: CartOrderRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val selectedId = cartOrderRepository.getSelectedCartOrder()
        .mapLatest {
            it?.orderId ?: 0
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0,
        )

    val orderDetails = selectedId
        .flatMapLatest { orderId ->
            cartRepository.getCartItemByOrderId(orderId)
        }.mapLatest {
            if (it.cartProducts.isEmpty()) {
                SelectedOrderDetails.Empty
            } else {
                SelectedOrderDetails.Success(it)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SelectedOrderDetails.Loading,
        )

    val addOnItems = cartRepository.getAllAddOnItems().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val deliveryPartners = selectedId.flatMapLatest {
        cartRepository.getDeliveryPartners()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val charges = selectedId.flatMapLatest {
        orderRepository.getAllCharges()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    private val _cartOrders = MutableStateFlow<UiState<List<CartOrder>>>(UiState.Loading)
    val cartOrders = _cartOrders.asStateFlow()

    private val _shareableOrderDetails = MutableStateFlow<UiState<OrderDetails>>(UiState.Empty)
    val shareableOrderDetails = _shareableOrderDetails.asStateFlow()

    init {
        getAllCartOrders()
    }

    internal fun deleteCartOrder(cartOrderId: Int) {
        viewModelScope.launch {
            when (val result = cartOrderRepository.deleteCartOrder(cartOrderId)) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to delete"))
                }

                is Resource.Success -> {
                    _eventFlow.emit(UiEvent.OnSuccess("Cart Order deleted successfully"))
                }
            }
        }
    }

    internal fun selectCartOrder(orderId: Int) {
        viewModelScope.launch {
            val result = cartOrderRepository.insertOrUpdateSelectedOrder(
                Selected(
                    selectedId = SELECTED_ID,
                    orderId = orderId,
                ),
            )

            when (result) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                }

                is Resource.Success -> {
                    analyticsHelper.logSelectedCartOrder(orderId)
                    _eventFlow.emit(UiEvent.OnSuccess("Cart Order Selected Successfully"))
                }
            }
        }
    }

    private fun getAllCartOrders() {
        viewModelScope.launch {
            cartOrderRepository.getAllProcessingCartOrders().collectLatest { list ->
                if (list.isEmpty()) {
                    _cartOrders.value = UiState.Empty
                } else {
                    _cartOrders.value = UiState.Success(list)
                }
            }
        }
    }

    internal fun decreaseProductQuantity(orderId: Int, productId: Int) {
        viewModelScope.launch {
            when (
                val result =
                    cartRepository.removeProductFromCart(orderId, productId)
            ) {
                is Resource.Error -> {
                    _eventFlow.emit(
                        UiEvent.OnError(
                            result.message ?: "Unable to add product",
                        ),
                    )
                }

                is Resource.Success -> {}
            }
        }
    }

    internal fun increaseProductQuantity(orderId: Int, productId: Int) {
        viewModelScope.launch {
            when (
                val result =
                    cartRepository.addProductToCart(orderId, productId)
            ) {
                is Resource.Error -> {
                    _eventFlow.emit(
                        UiEvent.OnError(
                            result.message ?: "Unable to add product",
                        ),
                    )
                }

                is Resource.Success -> {}
            }
        }
    }

    internal fun placeOrder(orderId: Int) {
        viewModelScope.launch {
            when (cartRepository.placeOrder(orderId)) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError("Unable to place orders"))
                }

                is Resource.Success -> {
                    _eventFlow.emit(
                        UiEvent.OnSuccess("order placed successfully"),
                    )
                    analyticsHelper.logPlacedCartOrder(orderId)
                }
            }
        }
    }

    internal fun updateCartAddOnItem(orderId: Int, itemId: Int) {
        viewModelScope.launch {
            when (
                val result =
                    cartRepository.updateAddOnItem(orderId, itemId)
            ) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to update"))
                }

                is Resource.Success -> {}
            }
        }
    }

    internal fun updateDeliveryPartner(orderId: Int, partnerId: Int) {
        viewModelScope.launch {
            when (
                val result =
                    cartRepository.updateDeliveryPartner(orderId, partnerId)
            ) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to update"))
                }

                is Resource.Success -> {}
            }
        }
    }

    internal fun getShareableOrderDetails(orderId: Int) {
        viewModelScope.launch {
            orderRepository.getOrderDetails(orderId)
                .onStart { _shareableOrderDetails.update { UiState.Loading } }
                .collectLatest { orderDetails ->
                    if (orderDetails.cartProducts.isEmpty()) {
                        _shareableOrderDetails.update { UiState.Empty }
                        return@collectLatest
                    }

                    _shareableOrderDetails.update { UiState.Success(orderDetails) }
                }
        }
    }
}

sealed interface SelectedOrderDetails {
    data object Loading : SelectedOrderDetails
    data object Empty : SelectedOrderDetails
    data class Success(val cartItem: CartItem) : SelectedOrderDetails
}

internal fun AnalyticsHelper.logSelectedCartOrder(cartOrderId: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "cart_order_selected",
            extras = listOf(
                AnalyticsEvent.Param("cart_order_selected", cartOrderId.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logPlacedCartOrder(cartOrderId: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "order_placed",
            extras = listOf(
                AnalyticsEvent.Param("order_placed", cartOrderId.toString()),
            ),
        ),
    )
}
