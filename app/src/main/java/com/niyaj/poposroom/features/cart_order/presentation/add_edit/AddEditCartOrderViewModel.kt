package com.niyaj.poposroom.features.cart_order.presentation.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.poposroom.features.address.domain.model.Address
import com.niyaj.poposroom.features.cart_order.domain.model.CartOrder
import com.niyaj.poposroom.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.poposroom.features.cart_order.domain.repository.CartOrderValidationRepository
import com.niyaj.poposroom.features.cart_order.domain.utils.OrderStatus
import com.niyaj.poposroom.features.cart_order.domain.utils.OrderType
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.common.utils.capitalizeWords
import com.niyaj.poposroom.features.common.utils.getCapitalWord
import com.niyaj.poposroom.features.customer.domain.model.Customer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddEditCartOrderViewModel @Inject constructor(
    private val cartOrderRepository: CartOrderRepository,
    private val validationRepository: CartOrderValidationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val cartOrderId = savedStateHandle.get<Int>("cartOrderId") ?: 0

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var state by mutableStateOf(AddEditCartOrderState())

    private val orderType = snapshotFlow { state.orderType }

    private val _newAddress = MutableStateFlow(Address())
    val newAddress = _newAddress.asStateFlow()

    private val _newCustomer = MutableStateFlow(Customer())
    val newCustomer = _newCustomer.asStateFlow()

    val customerError = orderType.combine(_newCustomer) { orderType, customer ->
        if (orderType != OrderType.DineIn) {
            validationRepository.validateCustomerPhone(customer.customerPhone).errorMessage
        } else null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val addressError = orderType.combine(_newAddress) { orderType, address ->
        if (orderType != OrderType.DineIn) {
            validationRepository.validateAddressName(address.addressName).errorMessage
        } else null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val orderId = snapshotFlow { cartOrderId }.mapLatest {
        cartOrderRepository.getLastCreatedOrderId(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 1
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val addresses = _newAddress.flatMapLatest {
        cartOrderRepository.getAllAddresses(it.addressName)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val customers = _newCustomer.flatMapLatest {
        cartOrderRepository.getAllCustomer(it.customerPhone)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
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
                    val newAddress = Address(
                        addressId = 0,
                        addressName = event.addressName.capitalizeWords,
                        shortName = event.addressName.getCapitalWord(),
                    )

                    _newAddress.value = newAddress
                }
            }

            is AddEditCartOrderEvent.AddressChanged -> {
                viewModelScope.launch {
                    _newAddress.value = event.address
                }
            }

            is AddEditCartOrderEvent.CustomerPhoneChanged -> {
                viewModelScope.launch {
                    val newCustomer = Customer(
                        customerId = 0,
                        customerPhone = event.customerPhone
                    )

                    _newCustomer.value = newCustomer
                }
            }

            is AddEditCartOrderEvent.CustomerChanged -> {
                viewModelScope.launch {
                    _newCustomer.value = event.customer
                }
            }

            is AddEditCartOrderEvent.DoesChargesIncluded -> {
                state = state.copy(
                    doesChargesIncluded = !state.doesChargesIncluded
                )
            }

            is AddEditCartOrderEvent.OrderTypeChanged -> {
                state = state.copy(
                    orderType = event.orderType
                )
            }

            is AddEditCartOrderEvent.CreateOrUpdateCartOrder -> {
                createOrUpdateCartOrder(event.cartOrderId)
            }
        }
    }

    private fun createOrUpdateCartOrder(cartOrderId: Int = 0) {
        viewModelScope.launch {
            if (addressError.value == null && customerError.value == null) {
                val newCartOrder = CartOrder(
                    orderId = cartOrderId,
                    orderType = state.orderType,
                    orderStatus = OrderStatus.PROCESSING,
                    doesChargesIncluded = state.doesChargesIncluded,
                    customer = _newCustomer.value,
                    address = _newAddress.value,
                    createdAt = Date(),
                    updatedAt = if (cartOrderId == 0) null else Date()

                )

                when (val result = cartOrderRepository.createOrUpdateCartOrder(newCartOrder)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                    }

                    is Resource.Success -> {
                        _eventFlow.emit(
                            UiEvent.OnSuccess(
                                "Cart Order created or updated successfully"
                            )
                        )
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
                        result.data?.let { cartOrder ->
                            _newAddress.value = cartOrder.address
                            _newCustomer.value = cartOrder.customer
                            state = state.copy(
                                orderType = cartOrder.orderType,
                                doesChargesIncluded = cartOrder.doesChargesIncluded
                            )
                        }
                    }
                }
            }
        }
    }
}