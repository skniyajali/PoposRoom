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

package com.niyaj.feature.printer.bluetoothPrinter

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.network.di.ApplicationScope
import com.niyaj.common.utils.createDottedString
import com.niyaj.common.utils.toBarDate
import com.niyaj.common.utils.toFormattedDate
import com.niyaj.common.utils.toFormattedTime
import com.niyaj.common.utils.toTime
import com.niyaj.data.repository.PrintRepository
import com.niyaj.data.repository.PrinterRepository
import com.niyaj.data.repository.UserDataRepository
import com.niyaj.feature.printer.bluetoothPrinter.utils.FileExtension.getImageFromDeviceOrDefault
import com.niyaj.model.AddOnItem
import com.niyaj.model.BluetoothDeviceState
import com.niyaj.model.CartOrder
import com.niyaj.model.CartProductItem
import com.niyaj.model.Charges
import com.niyaj.model.ChargesNameAndPrice
import com.niyaj.model.DeliveryReport
import com.niyaj.model.EmployeeNameAndId
import com.niyaj.model.OrderPrice
import com.niyaj.model.OrderType
import com.niyaj.model.Printer
import com.niyaj.model.Profile
import com.niyaj.ui.utils.UiEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class BluetoothPrinter @Inject constructor(
    private val repository: PrinterRepository,
    private val userDataRepository: UserDataRepository,
    private val printRepository: PrintRepository,
    private val application: Application,
    @ApplicationScope
    private val externalScope: CoroutineScope,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {
    private val bluetoothConnections by lazy { BluetoothPrintersConnections() }

    private val profileInfo = userDataRepository.loggedInUserId.flatMapLatest {
        repository.getProfileInfo(it)
    }.stateIn(
        scope = externalScope,
        started = SharingStarted.Eagerly,
        initialValue = Profile.defaultProfileInfo,
    )

    val printerInfo = repository.getPrinter(Printer.PRINTER_ID).stateIn(
        scope = externalScope,
        started = SharingStarted.Eagerly,
        initialValue = Printer.defaultPrinterInfo,
    )

    var printer: EscPosPrinter? = null

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val handler = CoroutineExceptionHandler { _, exception ->
        _eventFlow.tryEmit(UiEvent.OnError("An unexpected error occurred: ${exception.message}"))
    }

    @SuppressLint("MissingPermission")
    fun getBluetoothPrintersAsFlow(): Flow<List<BluetoothDeviceState>> {
        return channelFlow {
            try {
                val data = bluetoothConnections.list?.map {
                    BluetoothDeviceState(
                        name = it.device.name,
                        address = it.device.address,
                        bondState = it.device.bondState,
                        type = it.device.type,
                        connected = it.isConnected,
                    )
                }

                data?.let { send(it) }
            } catch (e: IOException) {
                _eventFlow.tryEmit(UiEvent.OnError("Unable to connect printer"))
                send(emptyList())
            } catch (e: EscPosConnectionException) {
                _eventFlow.tryEmit(UiEvent.OnError("Unable to connect printer"))
                send(emptyList())
            } catch (e: Exception) {
                _eventFlow.tryEmit(UiEvent.OnError("Unable to connect printer"))
                send(emptyList())
            }
        }
    }

    fun connectBluetoothPrinter(address: String) {
        try {
            val device = bluetoothConnections.list?.find { it.device.address == address }
            device?.connect()

            printer = EscPosPrinter(
                device,
                printerInfo.value.printerDpi,
                printerInfo.value.printerWidth,
                printerInfo.value.printerNbrLines,
            )
        } catch (e: Exception) {
            when (e) {
                is IOException -> {
                    _eventFlow.tryEmit(UiEvent.OnError("Unable to connect printer"))
                }

                is EscPosConnectionException -> {
                    _eventFlow.tryEmit(UiEvent.OnError("Unable to connect printer"))
                }

                else -> {
                    Log.e("Print Exception", e.message ?: "Unable to print")
                    _eventFlow.tryEmit(UiEvent.OnError("Something went wrong, please try again later."))
                }
            }
        }
    }

    suspend fun connectAndGetBluetoothPrinterAsync(): Result<EscPosPrinter?> {
        return withContext(Dispatchers.IO) {
            runCatching {
                try {
                    val bluetoothConnections = BluetoothPrintersConnections.selectFirstPaired()
                    bluetoothConnections?.let { data ->
                        EscPosPrinter(
                            data,
                            printerInfo.value.printerDpi,
                            printerInfo.value.printerWidth,
                            printerInfo.value.printerNbrLines,
                        )
                    } ?: throw IOException("No printer found")
                } catch (e: Exception) {
                    throw IOException("Unable to connect printer")
                }
            }
        }
    }

    fun printTestData() {
        externalScope.launch(handler) {
            val result = connectAndGetBluetoothPrinterAsync()

            result.fold(
                onSuccess = { printer ->
                    printer?.printFormattedText("[C]<b><font size='big'>Testing</font></b> \n")
                },
                onFailure = {
                    _eventFlow.emit(UiEvent.OnError("Printer Not Connected"))
                },
            )
        }
    }

    private fun getPrintableRestaurantDetails(posPrinter: EscPosPrinter? = printer): String {
        var details = ""

        try {
            val logo = application.getImageFromDeviceOrDefault(profileInfo.value.printLogo)

            logo?.let {
                val imagePrint =
                    PrinterTextParserImg.bitmapToHexadecimalString(posPrinter, it)

                imagePrint?.let {
                    details += if (printerInfo.value.printResLogo) {
                        "[C]<img>$imagePrint</img>\n\n"
                    } else {
                        " \n"
                    }
                }
            }
        } catch (e: Exception) {
            return " \n"
        }

        return details
    }

    private fun getPrintableQrCode(
        usePartnerQr: Boolean,
        partner: EmployeeNameAndId? = null,
        slogan: String = DEFAULT_SLOGAN,
    ): String {
        val data: String = partner?.let {
            if (usePartnerQr) {
                if (partner.partnerQRCode.isNullOrEmpty()) profileInfo.value.paymentQrCode else partner.partnerQRCode
            } else {
                profileInfo.value.paymentQrCode
            }
        } ?: profileInfo.value.paymentQrCode

        val name = partner?.let {
            if (usePartnerQr) {
                if (partner.partnerQRCode.isNullOrEmpty()) profileInfo.value.name else partner.employeeName
            } else {
                profileInfo.value.name
            }
        } ?: profileInfo.value.name

        return if (printerInfo.value.printQRCode) {
            "[C]Pay by scanning this QR code\n" +
                    "[C]${name}\n" +
                    "[L]\n" +
                    "[C]<qrcode size ='40'>$data</qrcode>\n\n\n" +
                    "[C]$slogan \n\n" +
                    "[L]-------------------------------\n"
        } else {
            ""
        }
    }

    private fun getPrintableFooterInfo(): String {
        return if (printerInfo.value.printWelcomeText) {
            "[C]Thank you for ordering!\n" +
                    "[C]For order and inquiry, Call.\n" +
                    "[C]${profileInfo.value.primaryPhone} / ${profileInfo.value.secondaryPhone}\n\n"
        } else {
            ""
        }
    }

    fun getPrintableHeader(title: String, date: String): String {
        var header = "[C]<b><font size='big'>$title</font></b>\n\n"

        header += if (date.isEmpty()) {
            "[C]--------- ${System.currentTimeMillis().toString().toFormattedDate} --------\n"
        } else {
            "[C]----------${date.toFormattedDate}---------\n"
        }

        header += "[L]\n"

        return header
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
                createDottedString(it.productName, printerInfo.value.productNameLength)
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

    suspend fun printOrders(cartOrders: List<Int>) {
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

    suspend fun printOrder(orderId: Int) {
        externalScope.launch {
            try {
                connectAndGetBluetoothPrinterAsync()
                    .onSuccess {
                        it?.let {
                            var printItems = ""

                            withContext(ioDispatcher) {
                                val orderDetails = printRepository.getOrderDetails(orderId)
                                val usePartnerQr = userDataRepository.usePartnerQRCode()
                                val partner = orderDetails.deliveryPartner

                                printItems += getPrintableRestaurantDetails(it)
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
                                printItems += getPrintableFooterInfo()
                                printItems += getPrintableQrCode(
                                    usePartnerQr,
                                    partner,
                                )
                            }

                            it.printFormattedText(printItems, 10f)
                        }
                    }
                    .onFailure {
                        _eventFlow.emit(UiEvent.OnError("Printer not connected"))
                    }
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.OnError("Printer not connected"))
            }
        }
    }

    suspend fun printOrderWorker(orderId: Int): Result<String> {
        return runCatching {
            try {
                val bluetoothConnections = BluetoothPrintersConnections.selectFirstPaired()

                bluetoothConnections?.let { data ->
                    val printer = EscPosPrinter(
                        data,
                        printerInfo.value.printerDpi,
                        printerInfo.value.printerWidth,
                        printerInfo.value.printerNbrLines,
                    )

                    var printItems = ""

                    val orderDetails = printRepository.getOrderDetails(orderId)
                    val usePartnerQr = userDataRepository.usePartnerQRCode()

                    val partner = orderDetails.deliveryPartner

                    printItems += getPrintableRestaurantDetails(printer)
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
                    printItems += getPrintableFooterInfo()
                    printItems += getPrintableQrCode(
                        usePartnerQr,
                        partner,
                    )

                    printer.printFormattedText(printItems, 10f)

                    return Result.success("Printed")
                } ?: throw IOException("No printer found")
            } catch (e: Exception) {
                throw IOException("Unable to connect printer")
            }
        }
    }

    fun printDeliveryReport(date: String, partnerId: Int? = null) {
        externalScope.launch {
            try {
                connectAndGetBluetoothPrinterAsync()
                    .onSuccess {
                        it?.let {
                            val deliveryReports =
                                printRepository.getDeliveryReports(date, partnerId)

                            var printItems = ""

                            printItems += getPrintableHeader("DELIVERY REPORTS", date)
                            printItems += getPrintableOrders(deliveryReports)

                            printItems += "[L]-------------------------------\n"
                            printItems += "[C]{^..^}--END OF REPORTS--{^..^}\n"
                            printItems += "[L]-------------------------------\n"

                            it.printFormattedText(printItems, 10f)
                        }
                    }.onFailure {
                        _eventFlow.emit(UiEvent.OnError("Printer Not Connected"))
                    }
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.OnError("Printer Not Connected"))
            }
        }
    }

    companion object {
        const val DEFAULT_SLOGAN = "Good Food, Good Mood"
    }
}
