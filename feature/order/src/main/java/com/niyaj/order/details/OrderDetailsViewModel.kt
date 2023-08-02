package com.niyaj.order.details

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.data.repository.OrderRepository
import com.niyaj.ui.event.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val orderId = savedStateHandle.get<Int>("orderId") ?: 0

    @OptIn(ExperimentalCoroutinesApi::class)
    val orderDetails = snapshotFlow { orderId }
        .flatMapLatest { it ->
            orderRepository.getOrderDetails(it).map {
                if (it.cartOrder.orderId == 0) {
                    UiState.Empty
                }else {
                    UiState.Success(it)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )
    @OptIn(ExperimentalCoroutinesApi::class)
    val charges = snapshotFlow { orderId }.flatMapLatest {
        orderRepository.getAllCharges()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
}