package com.niyaj.print

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.utils.createDottedString
import com.niyaj.common.utils.toFormattedDate
import com.niyaj.common.utils.toFormattedTime
import com.niyaj.common.utils.toTime
import com.niyaj.data.repository.PrintRepository
import com.niyaj.feature.printer.bluetooth_printer.BluetoothPrinter
import com.niyaj.model.AddOnItem
import com.niyaj.model.CartOrder
import com.niyaj.model.CartProductItem
import com.niyaj.model.Charges
import com.niyaj.model.ChargesNameAndPrice
import com.niyaj.model.DeliveryReport
import com.niyaj.model.OrderPrice
import com.niyaj.model.OrderType
import com.niyaj.model.Profile
import com.niyaj.print.utils.FileExtension.getImageFromDeviceOrDefault
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A ViewModel that handles printing orders and delivery reports.
 *
 * @param printRepository The repository for accessing data.
 * @param application The application context.
 * @param ioDispatcher The coroutine dispatcher for IO operations.
 */
@HiltViewModel
class OrderPrintViewModel @Inject constructor(
    private val printRepository: PrintRepository,
    private val application: Application,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
    bluetoothPrinter: BluetoothPrinter,
) : ViewModel() {

    private val resInfo = printRepository.getProfileInfo(Profile.RESTAURANT_ID)
        .flowOn(ioDispatcher).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Profile.defaultProfileInfo
        )

    private val printerInfo = bluetoothPrinter.printerInfo.value
    private val printer = bluetoothPrinter.printer

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
        viewModelScope.launch(ioDispatcher) {
            var printItems = ""

            val orderDetails = printRepository.getOrderDetails(orderId)

            printItems += printRestaurantDetails()
            printItems += printOrderDetails(orderDetails.cartOrder)
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
            printItems += printFooterInfo()
            printItems += printQrCode()

            try {
                printer.printFormattedText(printItems)
            } catch (e: Exception) {
                Log.d("Printer", e.message ?: "Error printing order details")
            }
        }
    }

    private fun printRestaurantDetails(): String {
        var details = ""

        val logo = application.getImageFromDeviceOrDefault(resInfo.value.printLogo)

        logo?.let {
            Log.d("logo", "Image found- ${it.byteCount}")
            Log.d("logo", "Print Image- ${printerInfo.printResLogo}")


            val imagePrint =
                PrinterTextParserImg.bitmapToHexadecimalString(printer, it)

            Log.d("logo", "Print Image String- $imagePrint")

            details += if (printerInfo.printResLogo) {
                "[C]<img>$imagePrint</img>\n\n"
            } else " \n"
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
            Log.d("product", "${it.productName} - ${printerInfo.productNameLength}")

            val productName =
                createDottedString(it.productName, printerInfo.productNameLength)

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
        return if (printerInfo.printQRCode) {
            "[C]Pay by scanning this QR code\n\n" +
                    "[L]\n" +
                    "[C]<qrcode size ='40'>${resInfo.value.paymentQrCode}</qrcode>\n\n\n" +
                    "[C]Good Food, Good Mood\n\n" +
                    "[L]-------------------------------\n"
        } else ""
    }

    private fun printFooterInfo(): String {
        return if (printerInfo.printWelcomeText) {
            "[C]Thank you for ordering!\n" +
                    "[C]For order and inquiry, Call.\n" +
                    "[C]${resInfo.value.primaryPhone} / ${resInfo.value.secondaryPhone}\n\n"
        } else ""
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

    private fun printDeliveryReport(date: String) {
        viewModelScope.launch {
            val deliveryReports = printRepository.getDeliveryReports(date)

            try {
                var printItems = ""
                printItems += getPrintableHeader(date)
                printItems += getPrintableOrders(deliveryReports)

                printer.printFormattedText(printItems, 50)
            } catch (e: Exception) {
                Log.e("Print Exception", e.message ?: "Unable to print")
            }
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

    private fun getPrintableOrders(deliveryReports: List<DeliveryReport>): String {
        var order = ""

        if (deliveryReports.isNotEmpty()) {
            order += "[L]ID[C]Address[R]Time[R]Price\n"
            order += "[L]-------------------------------\n"

            deliveryReports.forEach { cart ->
                val orderDate = (cart.updatedAt ?: cart.createdAt).toTime

                order += "[L]${cart.orderId}[C]${cart.addressName}[R]${orderDate}[R]${cart.orderPrice}\n"
                order += "[L]-------------------------------\n"
            }
        } else {
            order += "[C]You have not place any order.\n"
        }

        order += "[L]\n"

        return order
    }
}