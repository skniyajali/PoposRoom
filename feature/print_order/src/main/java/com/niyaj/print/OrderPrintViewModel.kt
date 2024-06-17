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

package com.niyaj.print

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.utils.createDottedString
import com.niyaj.common.utils.toBarDate
import com.niyaj.common.utils.toFormattedTime
import com.niyaj.common.utils.toTime
import com.niyaj.data.repository.PrintRepository
import com.niyaj.data.repository.UserDataRepository
import com.niyaj.feature.printer.bluetoothPrinter.BluetoothPrinter
import com.niyaj.model.AddOnItem
import com.niyaj.model.CartOrder
import com.niyaj.model.CartProductItem
import com.niyaj.model.Charges
import com.niyaj.model.ChargesNameAndPrice
import com.niyaj.model.DeliveryReport
import com.niyaj.model.EmployeeNameAndId
import com.niyaj.model.OrderPrice
import com.niyaj.model.OrderType
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

/**
 * A ViewModel that handles printing orders and delivery reports.
 *
 * @param printRepository The repository for accessing data.
 * @param ioDispatcher The coroutine dispatcher for IO operations.
 * @param bluetoothPrinter The Bluetooth printer for printing orders.
 */
@HiltViewModel
class OrderPrintViewModel @Inject constructor(
    private val printRepository: PrintRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
    private val bluetoothPrinter: BluetoothPrinter,
    private val userDataRepository: UserDataRepository,
) : ViewModel() {

    private val printerInfo = bluetoothPrinter.printerInfo.value

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onPrintEvent(event: PrintEvent) {
        when (event) {
            is PrintEvent.PrintOrder -> {
                printOrder(event.orderId)
            }

            is PrintEvent.PrintOrders -> {
                printOrders(event.orderIds)
            }

            is PrintEvent.PrintAllExpenses -> {
            }

            is PrintEvent.PrintDeliveryReport -> {
                printDeliveryReport(event.date, event.partnerId)
            }
        }
    }

    private fun printOrders(cartOrders: List<Int>) {
        try {
            if (cartOrders.isNotEmpty()) {
                for (cartOrder in cartOrders) {
                    printOrder(cartOrder)
                }
            }
        } catch (e: Exception) {
            Log.d("Printer", e.message ?: "Error connecting printer")
        }
    }

    private fun printOrder(orderId: Int) {
        viewModelScope.launch {
            try {
                bluetoothPrinter.connectBluetoothPrinterAsync()
                val printer = bluetoothPrinter.printer

                printer?.let {
                    var printItems = ""

                    withContext(ioDispatcher) {
                        val orderDetails = printRepository.getOrderDetails(orderId)
                        val usePartnerQr = userDataRepository.usePartnerQRCode()
                        val partner = orderDetails.deliveryPartner

                        printItems += bluetoothPrinter.getPrintableRestaurantDetails(it)
                        printItems += printOrderDetails(orderDetails.cartOrder, partner)
                        printItems += printProductDetails(orderDetails.cartProducts.toList())

                        if (orderDetails.addOnItems.isNotEmpty()) {
                            printItems += printAddOnItems(orderDetails.addOnItems.toList())
                        }

                        if (orderDetails.cartOrder.doesChargesIncluded && orderDetails.cartOrder.orderType != OrderType.DineIn) {
                            val chargesList = printRepository.getCharges()

                            printItems += printCharges(chargesList)
                        }

                        if (orderDetails.charges.isNotEmpty()) {
                            printItems += printAdditionalCharges(orderDetails.charges.toList())
                        }

                        printItems += printSubTotalAndDiscount(orderDetails.orderPrice)
                        printItems += printTotalPrice(orderDetails.orderPrice.totalPrice)
                        printItems += bluetoothPrinter.getPrintableFooterInfo()
                        printItems += bluetoothPrinter.getPrintableQrCode(usePartnerQr, partner)
                    }

                    printer.printFormattedText(printItems, 10f)
                } ?: run {
                    _eventFlow.emit(UiEvent.OnError("Printer not connected"))
                }
            } catch (e: EscPosConnectionException) {
                _eventFlow.emit(UiEvent.OnError("Unable to print order details"))
                return@launch
            } catch (e: IOException) {
                _eventFlow.emit(UiEvent.OnError("Unable to print order details"))
                return@launch
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.OnError("Unable to print order details"))
                return@launch
            }
        }
    }

    private fun printOrderDetails(cartOrder: CartOrder, partner: EmployeeNameAndId?): String {
        var order = ""

        order += "[L]ID - [R]${cartOrder.orderId}\n"

        order += "[L]Type - [R]${cartOrder.orderType}\n"

        order += "[L]Time - [R]${System.currentTimeMillis().toString().toFormattedTime}\n"

        if (cartOrder.customer.customerPhone.isNotEmpty()) {
            order += "[L]Phone - [R]${cartOrder.customer.customerPhone}\n"
        }

        if (cartOrder.address.addressName.isNotEmpty()) {
            order += "[L]Address - [R]${cartOrder.address.addressName}\n"
        }

        if (partner != null) {
            order += "[L]Partner - [R]${partner.employeeName}\n"
        }

        return order
    }

    private fun printProductDetails(orderedProduct: List<CartProductItem>): String {
        var products = ""

        products += "[L]-------------------------------\n"

        products += "[L]Name[R]Qty[R]Price\n"

        products += "[L]-------------------------------\n"

        orderedProduct.forEach {
            val productName =
                createDottedString(it.productName, printerInfo.productNameLength)
            val productPrice = it.productPrice * it.productQuantity

            products += "[L]$productName[R]${it.productQuantity}[R]${productPrice}\n"
        }

        return products
    }

    private fun printTotalPrice(orderPrice: Long): String {
        return "[L]-------------------------------\n" +
            "[L]Total[R] Rs. ${orderPrice}\n" +
            "[L]-------------------------------\n\n"
    }

    private fun printAddOnItems(addOnItemList: List<AddOnItem>): String {
        var addOnItems = ""

        if (addOnItemList.isNotEmpty()) {
            addOnItems += "[L]-------------------------------\n"
            addOnItems += "[C]AddOn Items\n"
            addOnItems += "[L]-------------------------------\n"

            for (addOnItem in addOnItemList) {
                addOnItems += "[L]${addOnItem.itemName}[R]${addOnItem.itemPrice}\n"
            }
        }

        return addOnItems
    }

    private fun printCharges(chargesLists: List<ChargesNameAndPrice>): String {
        var charges = ""

        if (chargesLists.isNotEmpty()) {
            charges += "[L]-------------------------------\n"
            charges += "[C]Charges \n"
            charges += "[L]-------------------------------\n"

            for (charge in chargesLists) {
                charges += "[L]${charge.chargesName}[R]${charge.chargesPrice}\n"
            }
        }

        return charges
    }

    private fun printAdditionalCharges(additionalCharges: List<Charges>): String {
        var charges = ""

        if (additionalCharges.isNotEmpty()) {
            charges += "[L]-------------------------------\n"
            charges += "[C] Additional Charges \n"
            charges += "[L]-------------------------------\n"

            for (charge in additionalCharges) {
                charges += "[L]${charge.chargesName}[R]${charge.chargesPrice}\n"
            }
        }

        return charges
    }

    private fun printSubTotalAndDiscount(orderPrice: OrderPrice): String {
        return "[L]-------------------------------\n" +
            "[L]Sub Total[R]${orderPrice.basePrice}\n" +
            "[L]Discount[R]${orderPrice.discountPrice}\n"
    }

    private fun printDeliveryReport(date: String, partnerId: Int? = null) {
        viewModelScope.launch {
            try {
                bluetoothPrinter.connectBluetoothPrinter()
                val printer = bluetoothPrinter.printer

                printer?.let {
                    val deliveryReports = printRepository.getDeliveryReports(date, partnerId)

                    var printItems = ""
                    printItems += bluetoothPrinter.getPrintableHeader("DELIVERY REPORTS", date)
                    printItems += getPrintableOrders(deliveryReports)
                    printItems += "[L]-------------------------------\n"
                    printItems += "[C]{^..^}--END OF REPORTS--{^..^}\n"
                    printItems += "[L]-------------------------------\n"

                    printer.printFormattedText(printItems, 10f)
                }
            } catch (e: Exception) {
                Log.d("Print", e.message ?: "Error printing delivery report")
                _eventFlow.emit(UiEvent.OnError("Error printing delivery reports"))
            }
        }
    }

    private fun getPrintableOrders(deliveryReports: List<DeliveryReport>): String {
        var printableText = ""

        if (deliveryReports.isNotEmpty()) {
            val totalAmt = deliveryReports.sumOf { it.orderPrice }
            val date = deliveryReports.first().orderDate.toBarDate

            val groupByPartner = deliveryReports.groupBy { it.partnerName ?: "Unmanaged" }

            groupByPartner.forEach { (partnerName, orders) ->
                val totalAmount = orders.sumOf { it.orderPrice }

                printableText += "[L]-------------------------------\n"
                printableText += "[L]<b>${partnerName.uppercase()}</b>[R]Rs.$totalAmount | ${orders.size}\n"

                printableText += "[L]-------------------------------\n"
                printableText += "[L]ID[C]Address[R]Time[R]Price\n"
                printableText += "[L]-------------------------------\n"

                orders.forEach { cart ->
                    printableText += "[L]${cart.orderId}[C]${cart.customerAddress}[R]${cart.orderDate.toTime}[R]${cart.orderPrice}\n"
                    printableText += "[L]-------------------------------\n"
                }

                printableText += "[L]\n"
            }

            printableText += "[L]\n"
            printableText += "[L]-------------------------------\n"
            printableText += "[L]<b>Total Orders</b>[R]${deliveryReports.size}\n"
            printableText += "[L]Rs.$totalAmt[R]${date}\n"
            printableText += "[L]-------------------------------\n"
        } else {
            printableText += "[C]You have not place any order.\n"
        }

        printableText += "[L]\n"

        return printableText
    }
}
