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

package com.niyaj.cartorder.createOrUpdate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.capitalizeWords
import com.niyaj.common.utils.getCapitalWord
import com.niyaj.data.repository.CartOrderRepository
import com.niyaj.data.repository.validation.CartOrderValidationRepository
import com.niyaj.model.Address
import com.niyaj.model.CartOrder
import com.niyaj.model.CartOrderWithAddOnAndCharges
import com.niyaj.model.Customer
import com.niyaj.model.OrderStatus
import com.niyaj.model.OrderType
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import com.samples.apps.core.analytics.AnalyticsEvent
import com.samples.apps.core.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AddEditCartOrderViewModel @Inject constructor(
    private val cartOrderRepository: CartOrderRepository,
    private val validationRepository: CartOrderValidationRepository,
    savedStateHandle: SavedStateHandle,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    private val cartOrderId = savedStateHandle.get<Int>("cartOrderId") ?: 0

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var state by mutableStateOf(AddEditCartOrderState())

    private val orderTypeFlow = snapshotFlow { state.orderType }
    private val addressFlow = snapshotFlow { state.address }
    private val customerFlow = snapshotFlow { state.customer }

    val customerError = orderTypeFlow.combine(customerFlow) { orderType, customer ->
        if (orderType != OrderType.DineIn) {
            validationRepository.validateCustomerPhone(customer.customerPhone).errorMessage
        } else {
            null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val addressError = orderTypeFlow.combine(addressFlow) { orderType, address ->
        if (orderType != OrderType.DineIn) {
            validationRepository.validateAddressName(address.addressName).errorMessage
        } else {
            null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val orderId = snapshotFlow { cartOrderId }.mapLatest {
        cartOrderRepository.getLastCreatedOrderId(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 1,
    )

    val addresses = addressFlow.flatMapLatest {
        cartOrderRepository.getAllAddresses(it.addressName)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val customers = customerFlow.flatMapLatest {
        cartOrderRepository.getAllCustomer(it.customerPhone)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val addOnItems = snapshotFlow { cartOrderId }.flatMapLatest {
        cartOrderRepository.getAllAddOnItem()
    }.mapLatest {
        if (it.isEmpty()) UiState.Empty else UiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    val charges = snapshotFlow { cartOrderId }.flatMapLatest {
        cartOrderRepository.getAllCharges()
    }.mapLatest {
        if (it.isEmpty()) UiState.Empty else UiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    val deliveryPartners = snapshotFlow { cartOrderId }.flatMapLatest {
        cartOrderRepository.getDeliveryPartners()
    }.mapLatest {
        if (it.isEmpty()) UiState.Empty else UiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    init {
        savedStateHandle.get<Int>("cartOrderId")?.let { cartOrderId ->
            getCartOrderById(cartOrderId)
        }
    }

    fun onEvent(event: AddEditCartOrderEvent) {
        when (event) {
            is AddEditCartOrderEvent.AddressNameChanged -> {
                viewModelScope.launch {
                    state = state.copy(
                        address = Address(
                            addressName = event.addressName.capitalizeWords,
                            shortName = event.addressName.getCapitalWord(),
                        ),
                    )
                }
            }

            is AddEditCartOrderEvent.AddressChanged -> {
                viewModelScope.launch {
                    state = state.copy(
                        address = event.address,
                    )
                }
            }

            is AddEditCartOrderEvent.CustomerPhoneChanged -> {
                viewModelScope.launch {
                    state = state.copy(
                        customer = Customer(
                            customerPhone = event.customerPhone,
                        ),
                    )
                }
            }

            is AddEditCartOrderEvent.CustomerChanged -> {
                viewModelScope.launch {
                    state = state.copy(customer = event.customer)
                }
            }

            is AddEditCartOrderEvent.DoesChargesIncluded -> {
                state = state.copy(
                    doesChargesIncluded = !state.doesChargesIncluded,
                )
            }

            is AddEditCartOrderEvent.OrderTypeChanged -> {
                state = if (event.orderType == OrderType.DineIn) {
                    state.copy(
                        orderType = event.orderType,
                        doesChargesIncluded = false,
                    )
                } else {
                    state.copy(
                        orderType = event.orderType,
                        doesChargesIncluded = true,
                    )
                }
            }

            is AddEditCartOrderEvent.SelectAddOnItem -> {
                if (state.selectedAddOnItems.contains(event.itemId)) {
                    state.selectedAddOnItems.remove(event.itemId)
                } else {
                    state.selectedAddOnItems.add(event.itemId)
                }
            }

            is AddEditCartOrderEvent.SelectCharges -> {
                if (state.selectedCharges.contains(event.chargesId)) {
                    state.selectedCharges.remove(event.chargesId)
                } else {
                    state.selectedCharges.add(event.chargesId)
                }
            }

            is AddEditCartOrderEvent.SelectDeliveryPartner -> {
                val newPartnerId = if (state.deliveryPartnerId == event.partnerId) 0
                else event.partnerId

                state = state.copy(
                    deliveryPartnerId = newPartnerId,
                )
            }


            is AddEditCartOrderEvent.CreateOrUpdateCartOrder -> {
                createOrUpdateCartOrder(cartOrderId)
            }

        }
    }

    private fun createOrUpdateCartOrder(cartOrderId: Int = 0) {
        viewModelScope.launch {
            if (addressError.value == null && customerError.value == null) {
                val newCartOrder = CartOrderWithAddOnAndCharges(
                    cartOrder = CartOrder(
                        orderId = cartOrderId,
                        orderType = state.orderType,
                        orderStatus = OrderStatus.PROCESSING,
                        doesChargesIncluded = state.doesChargesIncluded,
                        customer = state.customer,
                        address = state.address,
                        deliveryPartnerId = state.deliveryPartnerId,
                        createdAt = Date(),
                        updatedAt = if (cartOrderId == 0) null else Date(),
                    ),
                    addOnItems = state.selectedAddOnItems.toImmutableList(),
                    charges = state.selectedCharges.toImmutableList(),
                )

                when (val result = cartOrderRepository.createOrUpdateCartOrder(newCartOrder)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                    }

                    is Resource.Success -> {
                        val message = if (cartOrderId == 0) "created" else "updated"

                        _eventFlow.emit(
                            UiEvent.OnSuccess("Cart Order $message successfully"),
                        )

                        analyticsHelper.logOnCreateOrUpdateOrder(cartOrderId, message)
                    }
                }

                state = AddEditCartOrderState()
            } else {
                _eventFlow.emit(UiEvent.OnError("Unable to validate order"))
            }
        }
    }

    private fun getCartOrderById(cartOrderId: Int) {
        if (cartOrderId != 0) {
            viewModelScope.launch {
                when (val result = cartOrderRepository.getCartOrderById(cartOrderId)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError(result.message ?: "Unknown"))
                    }

                    is Resource.Success -> {
                        result.data?.let { (cartOrder, items, charges) ->
                            state.selectedAddOnItems.clear()
                            state.selectedAddOnItems.addAll(items)

                            state.selectedCharges.clear()
                            state.selectedCharges.addAll(charges)

                            state = state.copy(
                                orderType = cartOrder.orderType,
                                doesChargesIncluded = cartOrder.doesChargesIncluded,
                                address = cartOrder.address,
                                customer = cartOrder.customer,
                                deliveryPartnerId = cartOrder.deliveryPartnerId,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun AnalyticsHelper.logOnCreateOrUpdateOrder(cartOrderId: Int, message: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "cart_order_$message",
            extras = listOf(
                AnalyticsEvent.Param("cart_order_$message", cartOrderId.toString()),
            ),
        ),
    )
}
