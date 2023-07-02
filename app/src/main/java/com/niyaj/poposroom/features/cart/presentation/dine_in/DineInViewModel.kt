package com.niyaj.poposroom.features.cart.presentation.dine_in

import androidx.lifecycle.viewModelScope
import com.niyaj.poposroom.features.cart.domain.repository.CartRepository
import com.niyaj.poposroom.features.common.event.BaseViewModel
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.common.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DineInViewModel @Inject constructor(
    private val cartRepository: CartRepository
): BaseViewModel() {

    override var totalItems: List<Int> = emptyList()

    private val _state = MutableStateFlow(DineInState())
    val state = _state.asStateFlow()


    init {
        getAllDineInOrders()
    }

    val addOnItems = cartRepository.getAllAddOnItems().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )


    fun onEvent(event: DineInEvent) {
        when(event) {

            is DineInEvent.DecreaseQuantity -> {
                viewModelScope.launch {
                    when (val result = cartRepository.removeProductFromCart(event.orderId, event.productId)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable to add product"))
                        }
                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.OnSuccess("Product removed from cart"))
                        }
                    }
                }
            }

            is DineInEvent.IncreaseQuantity -> {
                viewModelScope.launch {
                    when (val result = cartRepository.addProductToCart(event.orderId, event.productId)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable to add product"))
                        }
                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.OnSuccess("Product added to cart"))
                        }
                    }
                }
            }

            is DineInEvent.PlaceAllDineInOrder -> {
                viewModelScope.launch {
                    when(cartRepository.placeAllOrder(selectedItems.toList())) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError("Unable to place all orders"))
                        }
                        is Resource.Success -> {
                            mEventFlow.emit(
                                UiEvent.OnSuccess("${selectedItems.size} orders placed successfully")
                            )
                            mSelectedItems.clear()
                        }
                    }
                }
            }

            is DineInEvent.PlaceDineInOrder -> {
                viewModelScope.launch {
                    when(cartRepository.placeOrder(event.orderId)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError("Unable to place orders"))
                        }
                        is Resource.Success -> {
                            mEventFlow.emit(
                                UiEvent.OnSuccess("order placed successfully")
                            )
                        }
                    }
                }
            }

            is DineInEvent.UpdateAddOnItemInCart -> {
                viewModelScope.launch {
                    when(val result = cartRepository.updateAddOnItem(event.orderId, event.itemId)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable to update"))
                        }
                        is Resource.Success -> {}
                    }
                }
            }

            is DineInEvent.RefreshDineInOrder -> {}

            is DineInEvent.SelectAllDineInOrder -> {
                selectAllItems()
            }

            is DineInEvent.SelectDineInOrder -> {
                selectItem(event.orderId)
            }
        }
    }

    private fun getAllDineInOrders() {
        viewModelScope.launch {
            cartRepository.getAllDineInCart().collectLatest { data ->
                totalItems = data.map { it.orderId }

                _state.value = _state.value.copy(
                    isLoading = false,
                    items = data,
                )
            }
        }
    }
}