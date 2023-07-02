package com.niyaj.poposroom.features.order.presentation

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.dantsu.escposprinter.EscPosPrinter
import com.niyaj.poposroom.features.common.event.BaseViewModel
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.order.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
) : BaseViewModel() {

    private lateinit var escposPrinter: EscPosPrinter

    private val _selectedDate = MutableStateFlow("")
    val selectedDate = _selectedDate.asStateFlow()

    val text = snapshotFlow { _searchText.value }

    @OptIn(ExperimentalCoroutinesApi::class)
    val cartOrders = _selectedDate.combine(text) { date, text ->
        orderRepository.getAllOrders(date, text)
    }.flatMapLatest {
        it.map { items ->
            OrderState(items, false)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = OrderState()
    )

    fun onOrderEvent(event: OrderEvent) {
        when (event) {

            is OrderEvent.DeleteOrder -> {
                viewModelScope.launch {
                    when (orderRepository.deleteOrder(event.orderId)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError("Unable to delete order"))
                        }

                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.OnSuccess("Order deleted successfully"))
                        }
                    }
                }
            }

            is OrderEvent.MarkedAsProcessing -> {
                viewModelScope.launch {
                    when (orderRepository.markOrderAsProcessing(event.orderId)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError("Unable to mark order as processing"))
                        }

                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.OnSuccess("Order marked as processing"))
                        }
                    }
                }
            }

            is OrderEvent.PrintDeliveryReport -> {

            }

            is OrderEvent.SelectDate -> {
                viewModelScope.launch {
                    _selectedDate.value = event.date
                }
            }

        }
    }

}