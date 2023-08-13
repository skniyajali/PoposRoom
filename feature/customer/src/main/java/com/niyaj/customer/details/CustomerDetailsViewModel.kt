package com.niyaj.customer.details

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.data.repository.CustomerRepository
import com.niyaj.model.TotalOrderDetails
import com.niyaj.ui.event.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CustomerDetailsViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val customerId = savedStateHandle.get<Int>("customerId") ?: 0

    private val _totalOrders = MutableStateFlow(TotalOrderDetails())
    val totalOrders = _totalOrders.asStateFlow()

    val customerDetails = snapshotFlow { customerId }.mapLatest {
        val data = customerRepository.getCustomerById(it).data

        if (data == null) UiState.Empty else UiState.Success(data)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
    )

    val orderDetails = snapshotFlow { customerId }.flatMapLatest { customerId ->
        customerRepository.getCustomerWiseOrders(customerId).mapLatest { orders ->
            if (orders.isEmpty()) UiState.Empty else {
                val startDate = orders.first().updatedAt
                val endDate = orders.last().updatedAt
                val repeatedOrder = orders.groupingBy { it.customerAddress }
                    .eachCount()
                    .filter { it.value > 1 }.size

                _totalOrders.value = _totalOrders.value.copy(
                    totalAmount = orders.sumOf { it.totalPrice },
                    totalOrder = orders.size,
                    repeatedOrder = repeatedOrder,
                    datePeriod = Pair(startDate, endDate)
                )

                UiState.Success(orders)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
    )
}