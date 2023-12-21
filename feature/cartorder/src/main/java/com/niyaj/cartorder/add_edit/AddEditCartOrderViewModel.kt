package com.niyaj.cartorder.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
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

@OptIn(ExperimentalCoroutinesApi::class)
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

    val orderId = snapshotFlow { cartOrderId }.mapLatest {
        cartOrderRepository.getLastCreatedOrderId(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 1
    )

    val addresses = _newAddress.flatMapLatest {
        cartOrderRepository.getAllAddresses(it.addressName)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val customers = _newCustomer.flatMapLatest {
        cartOrderRepository.getAllCustomer(it.customerPhone)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val addOnItems = snapshotFlow { cartOrderId }.flatMapLatest {
        cartOrderRepository.getAllAddOnItem()
    }.mapLatest {
        if (it.isEmpty()) UiState.Empty else UiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
    )

    val charges = snapshotFlow { cartOrderId }.flatMapLatest {
        cartOrderRepository.getAllCharges()
    }.mapLatest {
        if (it.isEmpty()) UiState.Empty else UiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
    )

    private val _selectedAddOnItems = mutableStateListOf<Int>()
    val selectedAddOnItems = _selectedAddOnItems

    private val _selectedCharges = mutableStateListOf<Int>()
    val selectedCharges = _selectedCharges

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
                state = if (event.orderType == OrderType.DineIn) {
                    state.copy(
                        orderType = event.orderType,
                        doesChargesIncluded = false
                    )
                }else {
                    state.copy(
                        orderType = event.orderType,
                        doesChargesIncluded = true
                    )
                }
            }

            is AddEditCartOrderEvent.SelectAddOnItem -> {
                if (_selectedAddOnItems.contains(event.itemId)){
                    _selectedAddOnItems.remove(event.itemId)
                }else {
                    _selectedAddOnItems.add(event.itemId)
                }
            }

            is AddEditCartOrderEvent.SelectCharges -> {
                if (_selectedCharges.contains(event.chargesId)){
                    _selectedCharges.remove(event.chargesId)
                }else {
                    _selectedCharges.add(event.chargesId)
                }
            }

            is AddEditCartOrderEvent.CreateOrUpdateCartOrder -> {
                createOrUpdateCartOrder(event.cartOrderId)
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
                        customer = _newCustomer.value,
                        address = _newAddress.value,
                        createdAt = Date(),
                        updatedAt = if (cartOrderId == 0) null else Date()
                    ),
                    addOnItems = _selectedAddOnItems.toImmutableList(),
                    charges = _selectedCharges.toImmutableList()
                )

                when (val result = cartOrderRepository.createOrUpdateCartOrder(newCartOrder)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                    }

                    is Resource.Success -> {
                        val message = if(cartOrderId == 0) "created" else "updated"

                        _eventFlow.emit(
                            UiEvent.OnSuccess("Cart Order $message successfully")
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
                        result.data?.let { (cartOrder, items, charges) ->
                            _newAddress.value = cartOrder.address
                            _newCustomer.value = cartOrder.customer

                            _selectedAddOnItems.clear()
                            _selectedAddOnItems.addAll(items)

                            _selectedCharges.clear()
                            _selectedCharges.addAll(charges)

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