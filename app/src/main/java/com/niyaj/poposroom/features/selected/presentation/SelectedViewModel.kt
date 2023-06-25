package com.niyaj.poposroom.features.selected.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.poposroom.features.cart_order.domain.model.CartOrder
import com.niyaj.poposroom.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.poposroom.features.common.event.UiState
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.selected.domain.model.Selected
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectedViewModel @Inject constructor(
    private val cartOrderRepository: CartOrderRepository
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedId = cartOrderRepository.getSelectedCartOrder()
        .mapLatest {
            it?.orderId ?: 0
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )


    private val _cartOrders = MutableStateFlow<UiState<List<CartOrder>>>(UiState.Loading)
    val cartOrders = _cartOrders.asStateFlow()

    init {
        getAllCartOrders()
    }

    internal fun deleteCartOrder(cartOrderId: Int){
        viewModelScope.launch {
            when(val result = cartOrderRepository.deleteCartOrder(cartOrderId)) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to delete"))
                }
                is Resource.Success -> {
                    _eventFlow.emit(UiEvent.OnSuccess("Cart Order deleted successfully"))
                }
            }
        }
    }

    fun selectCartOrder(orderId: Int) {
        viewModelScope.launch {
            val result = cartOrderRepository.insertOrUpdateSelectedOrder(
                Selected(orderId = orderId)
            )

            when (result) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                }

                is Resource.Success -> {
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
                }else {
                    _cartOrders.value = UiState.Success(list)
                }
            }
        }
    }

}