package com.niyaj.product.details

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.toMillis
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.data.repository.ProductRepository
import com.niyaj.model.OrderType
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
class ProductDetailsViewModel @Inject constructor(
    private val productRepository : ProductRepository,
    savedStateHandle : SavedStateHandle,
): ViewModel() {

    private val productId = savedStateHandle.get<Int>("productId") ?: 0

    private val _totalOrders = MutableStateFlow(ProductTotalOrderDetails())
    val totalOrders = _totalOrders.asStateFlow()

    val productPrice = snapshotFlow { productId }.mapLatest {
        productRepository.getProductPrice(productId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0
    )

    val product = snapshotFlow { productId }.mapLatest {
        val data = productRepository.getProductById(it).data

        if (data == null) UiState.Empty else UiState.Success(data)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
    )


    val orderDetails = snapshotFlow { productId }.flatMapLatest { productId ->
        productRepository.getProductWiseOrderDetails(productId).mapLatest { orders ->
            if (orders.isEmpty()) UiState.Empty else {

                val groupByDate = orders.groupBy { it.orderedDate.toPrettyDate() }
                val grpByOrderType = orders.groupBy { it.orderType }

                val dineInOrders = grpByOrderType.getOrElse(OrderType.DineIn, defaultValue = { emptyList() })
                val dineOutOrders = grpByOrderType.getOrElse(OrderType.DineOut, defaultValue = { emptyList() })

                val dineInAmount = dineInOrders.sumOf { it.quantity }.times(productPrice.value)
                val dineOutAmount = dineOutOrders.sumOf { it.quantity}.times(productPrice.value)

                val totalAmount = dineInAmount + dineOutAmount

                val startDate = if (orders.isNotEmpty()) orders.first().orderedDate.toMillis else ""
                val endDate = if (orders.isNotEmpty()) orders.last().orderedDate.toMillis else ""

                val mostOrderItemDate = if (groupByDate.isNotEmpty()) groupByDate.maxBy { it.value.size }.key else ""
                val mostOrderQtyDate = if (groupByDate.isNotEmpty()) groupByDate.maxBy { entry -> entry.value.sumOf { it.quantity } }.key else ""

                _totalOrders.value = _totalOrders.value.copy(
                    totalAmount = totalAmount.toString(),
                    dineInAmount = dineInAmount.toString(),
                    dineInQty = dineInOrders.size,
                    dineOutAmount = dineOutAmount.toString(),
                    dineOutQty = dineOutOrders.size,
                    mostOrderItemDate = mostOrderItemDate,
                    mostOrderQtyDate = mostOrderQtyDate,
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