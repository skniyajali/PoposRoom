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

package com.niyaj.feature.product.details

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.utils.getCapitalWord
import com.niyaj.common.utils.isSameDay
import com.niyaj.common.utils.toBarDate
import com.niyaj.common.utils.toMillis
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.ProductRepository
import com.niyaj.feature.printer.bluetoothPrinter.BluetoothPrinter
import com.niyaj.model.OrderType
import com.niyaj.ui.event.ShareViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
    private val analyticsHelper: AnalyticsHelper,
    private val bluetoothPrinter: BluetoothPrinter,
) : ShareViewModel(ioDispatcher) {

    private val productId = savedStateHandle.get<Int>("productId") ?: 0

    private val _totalOrders = MutableStateFlow(ProductTotalOrderDetails())
    val totalOrders = _totalOrders.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val productPrice = snapshotFlow { productId }.mapLatest {
        productRepository.getProductPrice(productId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0,
    )

    val product = snapshotFlow { productId }.mapLatest {
        val data = productRepository.getProductById(it).data

        if (data == null) UiState.Empty else UiState.Success(data)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    val orderDetails = snapshotFlow { productId }.flatMapLatest { productId ->
        productRepository.getProductWiseOrderDetails(productId).mapLatest { orders ->
            if (orders.isEmpty()) {
                UiState.Empty
            } else {

                val groupByDate = orders.groupBy { it.orderedDate.toBarDate }
                val grpByOrderType = orders.groupBy { it.orderType }

                val dineInOrders =
                    grpByOrderType.getOrElse(OrderType.DineIn, defaultValue = { emptyList() })
                val dineOutOrders =
                    grpByOrderType.getOrElse(OrderType.DineOut, defaultValue = { emptyList() })

                val dineInAmount = dineInOrders.sumOf { it.quantity }.times(productPrice.value)
                val dineOutAmount = dineOutOrders.sumOf { it.quantity }.times(productPrice.value)

                val totalAmount = dineInAmount + dineOutAmount

                val startDate = if (orders.isNotEmpty()) orders.first().orderedDate.toMillis else ""
                val endDate = if (orders.isNotEmpty()) orders.last().orderedDate.toMillis else ""

                val mostOrderItemDate =
                    if (groupByDate.isNotEmpty()) groupByDate.maxBy { it.value.size }.key else ""
                val mostOrderQtyDate =
                    if (groupByDate.isNotEmpty()) {
                        groupByDate.maxBy { entry ->
                            entry.value.sumOf { it.quantity }
                        }.key
                    } else {
                        ""
                    }

                _totalOrders.value = _totalOrders.value.copy(
                    totalAmount = totalAmount.toString(),
                    dineInAmount = dineInAmount.toString(),
                    dineInQty = dineInOrders.size,
                    dineOutAmount = dineOutAmount.toString(),
                    dineOutQty = dineOutOrders.size,
                    mostOrderItemDate = mostOrderItemDate,
                    mostOrderQtyDate = mostOrderQtyDate,
                    datePeriod = Pair(startDate, endDate),
                )

                UiState.Success(orders)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    init {
        savedStateHandle.get<Int>("productId")?.let {
            analyticsHelper.logProductDetailsViewed(it)
        }
    }

    fun printProductDetails() {
        viewModelScope.launch {
            try {
                bluetoothPrinter
                    .connectAndGetBluetoothPrinterAsync()
                    .onSuccess {
                        it?.let {
                            var printItems = ""

                            printItems += bluetoothPrinter.getPrintableHeader("Product Details", "")
                            printItems += getPrintableProductDetails()
                            printItems += getTotalSales()
                            printItems += getPrintableProductOrderDetails()
                            printItems += "[L]-------------------------------\n"
                            printItems += "[C]{^..^}--END OF REPORTS--{^..^}\n"
                            printItems += "[L]-------------------------------\n"

                            it.printFormattedText(printItems, 10f)
                            analyticsHelper.logProductDetailsPrinted(productId)
                        }
                    }.onFailure {
                        _eventFlow.emit(UiEvent.OnError("Printer Not Connected"))
                    }
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.OnError("Printer Not Connected"))
            }
        }
    }

    private fun getPrintableProductDetails(): String {
        var productDetails = ""
        val productData = (product.value as UiState.Success).data

        if (productData.productId != 0) {
            val productDate =
                (productData.updatedAt ?: productData.createdAt).toBarDate

            productDetails += "[L]-------------------------------\n"
            productDetails += "[L]Name [R]${productData.productName}\n"
            productDetails += "[L]Price [R]Rs.${productPrice.value}\n"
            productDetails += "[L]Last Updated [R]${productDate}\n"
        }

        return productDetails
    }

    private fun getTotalSales(): String {
        var boxReport = ""
        val report = totalOrders.value
        val datePeriod =
            report.datePeriod.first.toBarDate +
                if (!report.datePeriod.isSameDay) " --> " + report.datePeriod.second.toBarDate else ""

        boxReport += "[L]-------------------------------\n"
        boxReport += "[L]TOTAL ORDERS & SALES [R] ${report.dineInQty.plus(report.dineOutQty)}\n"
        boxReport += "[L]-------------------------------\n"

        boxReport += "[C]${datePeriod}\n"
        boxReport += "[L]-------------------------------\n"
        boxReport += "[L]DineIn Sales(${report.dineInQty})[R]Rs.${report.dineInAmount}\n"
        boxReport += "[L]DineOut Sales(${report.dineOutQty})[R]Rs.${report.dineOutAmount}\n"
        boxReport += "[L]Most Sales(Qty)[R]${report.mostOrderQtyDate}\n"
        boxReport += "[L]Most Orders(Price)[R]${report.mostOrderItemDate}\n"
        boxReport += "[L]-------------------------------\n"
        boxReport += "[L]Total - [R]Rs.${report.totalAmount}\n"
        boxReport += "[L]-------------------------------\n"

        return boxReport
    }

    private fun getPrintableProductOrderDetails(): String {
        var details = ""

        details += "[C]PRODUCT ORDERS\n"
        details += "[L]-------------------------------\n"

        val orders = (orderDetails.value as UiState.Success).data

        if (orders.isNotEmpty()) {
            val groupByDate = orders
                .sortedBy { it.orderType }
                .groupBy { it.orderedDate.toBarDate }

            details += "[L]ID[L]Qty[L]Phone[R]Address\n"

            groupByDate.forEach { (date, orderList) ->
                val totalSales = orderList
                    .sumOf { it.quantity }
                    .times(productPrice.value).toString()

                details += "[L]-------------------------------\n"
                details += "[L]$date --> [R][${orderList.size}]|[${orderList.sumOf { it.quantity }}]|Rs.${totalSales}\n"
                details += "[L]-------------------------------\n"

                orderList.forEach { productWiseOrder ->
                    val customerAddress =
                        productWiseOrder.customerAddress?.getCapitalWord() ?: "N/A"
                    val customerPhone = productWiseOrder.customerPhone ?: "N/A"

                    details += "[L]#${productWiseOrder.orderId}[L]" +
                        "${productWiseOrder.quantity}[L]$customerPhone[R]${customerAddress}\n\n"
                }
            }
        } else {
            details += "[C]No orders found for this product\n"
        }

        return details
    }
}

internal fun AnalyticsHelper.logProductDetailsViewed(productId: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "product_details_viewed",
            extras = listOf(
                AnalyticsEvent.Param("product_details_viewed", productId.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logProductDetailsPrinted(productId: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "product_details_printed",
            extras = listOf(
                AnalyticsEvent.Param("product_details_printed", productId.toString()),
            ),
        ),
    )
}
