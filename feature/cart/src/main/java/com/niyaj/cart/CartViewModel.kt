/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.cart

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.CartRepository
import com.niyaj.ui.utils.UiEvent
import com.samples.apps.core.analytics.AnalyticsEvent
import com.samples.apps.core.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {
    private val fetchData = MutableStateFlow(false)

    private val _selectedDineInOrder = mutableStateListOf<Int>()
    val selectedDineInOrder: MutableList<Int> = _selectedDineInOrder

    private val _selectedDineOutOrder = mutableStateListOf<Int>()
    val selectedDineOutOrder: MutableList<Int> = _selectedDineOutOrder

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var dineInCount = 0
    private var dineOutCount = 0

    val dineInState = fetchData.flatMapLatest {
        cartRepository.getAllDineInCart()
    }.mapLatest {
        if (it.isEmpty()) CartState.Empty else CartState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CartState.Loading,
    )

    val dineOutState = fetchData.flatMapLatest {
        cartRepository.getAllDineOutCart()
    }.mapLatest {
        if (it.isEmpty()) CartState.Empty else CartState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CartState.Loading,
    )

    val addOnItems = cartRepository.getAllAddOnItems().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val deliveryPartners = fetchData.flatMapLatest {
        cartRepository.getDeliveryPartners()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    fun onEvent(event: CartEvent) {
        when (event) {
            is CartEvent.IncreaseQuantity -> {
                viewModelScope.launch {
                    when (val result =
                        cartRepository.addProductToCart(event.orderId, event.productId)) {
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

            is CartEvent.DecreaseQuantity -> {
                viewModelScope.launch {
                    when (val result =
                        cartRepository.removeProductFromCart(event.orderId, event.productId)) {
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

            is CartEvent.PlaceCartOrder -> {
                viewModelScope.launch {
                    when (cartRepository.placeOrder(event.orderId)) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError("Unable to place orders"))
                        }

                        is Resource.Success -> {
                            _eventFlow.emit(
                                UiEvent.OnSuccess("order placed successfully"),
                            )
                            analyticsHelper.logPlacedOrder(event.orderId)
                        }
                    }
                }
            }

            is CartEvent.UpdateAddOnItemInCart -> {
                viewModelScope.launch {
                    when (val result =
                        cartRepository.updateAddOnItem(event.orderId, event.itemId)) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to update"))
                        }

                        is Resource.Success -> {}
                    }
                }
            }


            is DineOutEvent.UpdateDeliveryPartner -> {
                viewModelScope.launch {
                    val result =
                        cartRepository.updateDeliveryPartner(event.orderId, event.deliveryPartnerId)

                    when (result) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message.toString()))
                        }

                        is Resource.Success -> {}
                    }
                }
            }

            is DineOutEvent.SelectDineOutCart -> {
                viewModelScope.launch {
                    if (_selectedDineOutOrder.contains(event.orderId)) {
                        _selectedDineOutOrder.remove(event.orderId)
                    } else {
                        _selectedDineOutOrder.add(event.orderId)
                    }
                }
            }

            is DineOutEvent.SelectAllDineOutCart -> {
                viewModelScope.launch {
                    dineOutCount += 1

                    val items = if (dineOutState.value is CartState.Success)
                        (dineOutState.value as CartState.Success).items.map { it.orderId } else emptyList()

                    if (items.isNotEmpty()) {
                        if (items.size == _selectedDineOutOrder.size) {
                            _selectedDineOutOrder.clear()
                        } else {
                            items.forEach { orderId ->
                                if (dineOutCount % 2 == 0 && !_selectedDineOutOrder.contains(orderId)
                                ) {
                                    _selectedDineOutOrder.add(orderId)
                                } else {
                                    _selectedDineOutOrder.remove(orderId)
                                }
                            }
                        }
                    }

                }
            }

            is DineOutEvent.PlaceAllDineOutCart -> {
                viewModelScope.launch {
                    when (cartRepository.placeAllOrder(_selectedDineOutOrder.toList())) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError("Unable to place all orders"))
                        }

                        is Resource.Success -> {
                            _eventFlow.emit(
                                UiEvent.OnSuccess("${_selectedDineOutOrder.size} orders placed successfully"),
                            )
                            analyticsHelper.logPlacedDineOutOrder(_selectedDineOutOrder.toList())
                            _selectedDineOutOrder.clear()
                        }
                    }
                }
            }

            is DineInEvent.SelectDineInCart -> {
                viewModelScope.launch {
                    if (_selectedDineInOrder.contains(event.orderId)) {
                        _selectedDineInOrder.remove(event.orderId)
                    } else {
                        _selectedDineInOrder.add(event.orderId)
                    }
                }
            }

            is DineInEvent.SelectAllDineInCart -> {
                viewModelScope.launch {
                    dineInCount += 1

                    val items = if (dineInState.value is CartState.Success)
                        (dineInState.value as CartState.Success).items.map { it.orderId } else emptyList()

                    if (items.isNotEmpty()) {
                        if (items.size == _selectedDineInOrder.size) {
                            _selectedDineInOrder.clear()
                        } else {
                            items.forEach { orderId ->
                                if (dineOutCount % 2 == 0 && !_selectedDineInOrder.contains(orderId)) {
                                    _selectedDineInOrder.add(orderId)
                                } else {
                                    _selectedDineInOrder.remove(orderId)
                                }
                            }
                        }
                    }

                }
            }

            is DineInEvent.PlaceAllDineInCart -> {
                viewModelScope.launch {
                    when (cartRepository.placeAllOrder(_selectedDineInOrder)) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError("Unable to place all orders"))
                        }

                        is Resource.Success -> {
                            _eventFlow.emit(
                                UiEvent.OnSuccess("${_selectedDineInOrder.size} orders placed successfully"),
                            )
                            analyticsHelper.logPlacedDineInOrder(_selectedDineInOrder.toList())
                            _selectedDineInOrder.clear()
                        }
                    }
                }
            }
        }
    }
}


internal fun AnalyticsHelper.logPlacedOrder(orderId: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "order_placed",
            extras = listOf(
                AnalyticsEvent.Param("order_placed", orderId.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logPlacedDineOutOrder(orderId: List<Int>) {
    logEvent(
        event = AnalyticsEvent(
            type = "dine_out_order_placed",
            extras = listOf(
                AnalyticsEvent.Param("dine_out_order_placed", orderId.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logPlacedDineInOrder(orderId: List<Int>) {
    logEvent(
        event = AnalyticsEvent(
            type = "dineIn_order_placed",
            extras = listOf(
                AnalyticsEvent.Param("dine_in_order_placed", orderId.toString()),
            ),
        ),
    )
}