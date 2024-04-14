package com.niyaj.print

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.utils.createDottedString
import com.niyaj.common.utils.toFormattedDate
import com.niyaj.common.utils.toFormattedTime
import com.niyaj.common.utils.toRupee
import com.niyaj.common.utils.toTime
import com.niyaj.data.repository.ChargesRepository
import com.niyaj.data.repository.OrderRepository
import com.niyaj.data.repository.PrinterRepository
import com.niyaj.data.repository.ProfileRepository
import com.niyaj.model.AddOnItem
import com.niyaj.model.CartOrder
import com.niyaj.model.CartProductItem
import com.niyaj.model.Charges
import com.niyaj.model.OrderPrice
import com.niyaj.model.OrderType
import com.niyaj.model.Printer
import com.niyaj.model.Profile
import com.niyaj.print.utils.FileExtension.getImageFromDeviceOrDefault
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A ViewModel that handles printing orders and delivery reports.
 *
 * @param orderUseCases The use cases for accessing order data.
 * @param chargesRepository The repository for accessing charges data.
 * @param repository The repository for accessing profile data.
 * @param printerRepository The repository for accessing printer information.
 * @param application The application context.
 * @param ioDispatcher The coroutine dispatcher for IO operations.
 */
@HiltViewModel
class OrderPrintViewModel @Inject constructor(
    private val orderUseCases: OrderRepository,
    private val chargesRepository: ChargesRepository,
    repository: ProfileRepository,
    private val printerRepository: PrinterRepository,
    private val application: Application,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    /**
     * The profile information.
     */
    private val resInfo = repository.getProfileInfo().flowOn(ioDispatcher).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Profile.defaultProfileInfo
    )

    /**
     * The printer information.
     */
    private val info = printerRepository.getPrinter(Printer.defaultPrinterInfo.printerId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Printer.defaultPrinterInfo
    )

    /**
     * The ESC/POS printer.
     */
    private var escposPrinter = mutableStateOf(defaultPrinter())

    /**
     * The list of charges.
     */
    private var chargesList = mutableStateListOf<Charges>()

    /**
     * Initializes the ViewModel and fetches all charges.
     */
    init {
        connectBluetoothPrinter()
        getAllCharges()
    }

    /**
     * Handles a print event.
     *
     * @param event The print event.
     */
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
                printDeliveryReport(event.date)
            }
        }
    }

    /**
     * Prints multiple orders.
     *
     * @param cartOrders The list of order IDs.
     */
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

    /**
     * Prints a single order.
     *
     * @param cartOrderId The order ID.
     */
    private fun printOrder(cartOrderId: Int) {
        viewModelScope.launch(ioDispatcher) {
            var printItems = ""

            orderUseCases.getOrderDetails(cartOrderId).collectLatest { itemDetails ->
                printItems += printRestaurantDetails()
                printItems += printOrderDetails(itemDetails.cartOrder)
                printItems += printProductDetails(itemDetails.cartProducts.toList())

                if (itemDetails.addOnItems.isNotEmpty()) {
                    printItems += printAddOnItems(itemDetails.addOnItems.toList())
                }

                if (itemDetails.cartOrder.doesChargesIncluded && itemDetails.cartOrder.orderType != OrderType.DineIn) {
                    printItems += printCharges()
                }

                printItems += printSubTotalAndDiscount(itemDetails.orderPrice)
                printItems += printTotalPrice(itemDetails.orderPrice.totalPrice)
                printItems += printFooterInfo()
                printItems += printQrCode()

                try {
                    escposPrinter.value.printFormattedTextAndCut(
                        printItems,
                        info.value.printerWidth
                    )
                } catch (e: Exception) {
                    Log.d("Printer", e.message ?: "Error printing order details")
                }
            }
        }
    }

    /**
     * Prints the restaurant details.
     *
     * @return The formatted restaurant details string
     */
    private fun printRestaurantDetails(): String {
        var details = ""

        viewModelScope.launch {
            val logo = application.applicationContext.getImageFromDeviceOrDefault(
                resInfo.value.printLogo,
                ioDispatcher
            )

            logo?.let {
                val imagePrint = PrinterTextParserImg.bitmapToHexadecimalString(escposPrinter.value, it)

                details = if (info.value.printResLogo) {
                    "[C]<img>$imagePrint</img>\n\n"
                } else " \n"
            }
        }

        details += "[C]--------- ORDER BILL ---------\n\n"

        return details
    }

    private fun printOrderDetails(cartOrder: CartOrder): String {
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

        return order
    }

    private fun printProductDetails(orderedProduct: List<CartProductItem>): String {
        var products = ""

        products += "[L]-------------------------------\n"

        products += "[L]Name[R]Qty[R]Price\n"

        products += "[L]-------------------------------\n"


        orderedProduct.forEach {
            Log.d("product", "${it.productName} - ${info.value.productNameLength}")

            val productName = createDottedString(it.productName, info.value.productNameLength)

            products += "[L]${productName}[R]${it.productQuantity}[R]${it.productPrice}\n"
        }

        return products
    }

    private fun printTotalPrice(orderPrice: Long): String {
        return "[L]-------------------------------\n" +
                "[L]Total[R] Rs. ${orderPrice}\n" +
                "[L]-------------------------------\n\n"
    }

    private fun printQrCode(): String {
        return if (info.value.printQRCode) {
            "[C]Pay by scanning this QR code\n\n" +
                    "[L]\n" +
                    "[C]<qrcode size ='40'>${resInfo.value.paymentQrCode}</qrcode>\n\n\n" +
                    "[C]Good Food, Good Mood\n\n" +
                    "[L]-------------------------------\n"
        } else ""
    }

    private fun printFooterInfo(): String {
        return if (info.value.printWelcomeText) {
            "[C]Thank you for ordering!\n" +
                    "[C]For order and inquiry, Call.\n" +
                    "[C]${resInfo.value.primaryPhone} / ${resInfo.value.secondaryPhone}\n\n"
        } else ""
    }

    private fun printAddOnItems(addOnItemList: List<AddOnItem>): String {
        var addOnItems = ""

        if (addOnItemList.isNotEmpty()) {
            addOnItems += "[L]-------------------------------\n"
            for (addOnItem in addOnItemList) {
                addOnItems += "[L]${addOnItem.itemName}[R]${addOnItem.itemPrice}\n"
            }

        }

        return addOnItems
    }

    private fun printCharges(): String {
        var charges = ""

        if (chargesList.isNotEmpty()) {
            charges += "[L]-------------------------------\n"
            for (charge in chargesList) {
                charges += "[L]${charge.chargesName}[R]${charge.chargesPrice.toString().toRupee}\n"
            }
        }

        return charges
    }

    private fun printSubTotalAndDiscount(orderPrice: OrderPrice): String {
        return "[L]-------------------------------\n" +
                "[L]Sub Total[R]${orderPrice.basePrice}\n" +
                "[L]Discount[R]${orderPrice.discountPrice}\n"
    }

    private fun getAllCharges() {
        viewModelScope.launch(ioDispatcher) {
            chargesRepository.getAllCharges("").collect { result ->
                chargesList = result.toMutableStateList()
            }
        }
    }

    private fun printDeliveryReport(date: String) {
        try {
            var printItems = ""

            printItems += getPrintableHeader(date)
            printItems += getPrintableOrders(date)

            escposPrinter.value.printFormattedText(printItems, 50)
        } catch (e: Exception) {
            Log.e("Print Exception", e.message ?: "Unable to print")
        }
    }

    private fun getPrintableHeader(date: String): String {
        var header = "[C]<b><font size='big'>DELIVERY</font></b>\n\n"

        header += if (date.isEmpty()) {
            "[C]--------- ${System.currentTimeMillis().toString().toFormattedDate} --------\n"
        } else {
            "[C]----------${date.toFormattedDate}---------\n"
        }

        header += "[L]\n"

        return header
    }

    private fun getPrintableOrders(date: String): String {
        var order = ""

        viewModelScope.launch(ioDispatcher) {
            orderUseCases.getAllOrders(date, OrderType.DineOut, "").collectLatest { dineOutOrders ->
                if (dineOutOrders.isNotEmpty()) {
                    order += "[L]ID[C]Address[R]Time[R]Price\n"
                    order += "[L]-------------------------------\n"

                    dineOutOrders.forEach { cart ->
                        order += "[L]${cart.orderId}[C]${cart.customerAddress}[R]${cart.orderDate.toTime}[R]${cart.orderPrice.totalPrice}\n"
                        order += "[L]-------------------------------\n"
                    }
                } else {
                    order += "[C]You have not place any order.\n"
                }
            }
        }

        order += "[L]\n"

        return order
    }

    private fun connectBluetoothPrinter() {
        viewModelScope.launch {
            val data = BluetoothPrintersConnections.selectFirstPaired()
            if (data?.isConnected == false) {
                data.connect()
            }

            escposPrinter.value = EscPosPrinter(
                data,
                info.value.printerDpi,
                info.value.printerWidth,
                info.value.printerNbrLines
            )
        }
    }

    companion object {
        fun defaultPrinter(): EscPosPrinter {
            val data = BluetoothPrintersConnections.selectFirstPaired()
            if (data?.isConnected == false) {
                data.connect()
            }

            return EscPosPrinter(
                data,
                Printer.defaultPrinterInfo.printerDpi,
                Printer.defaultPrinterInfo.printerWidth,
                Printer.defaultPrinterInfo.printerNbrLines
            )
        }
    }
}