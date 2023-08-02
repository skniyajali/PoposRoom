package com.niyaj.print

import androidx.lifecycle.ViewModel
import com.niyaj.common.utils.createDottedString
import com.niyaj.common.utils.toFormattedTime
import com.niyaj.data.repository.PrintRepository
import com.niyaj.model.AddOnItem
import com.niyaj.model.CartOrder
import com.niyaj.model.CartProductItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PrintViewModel @Inject constructor(
    private val printRepository: PrintRepository,
) : ViewModel() {


    fun onEvent(event: PrintEvent) {
        when (event) {
            is PrintEvent.PrintOrder -> {

            }

            is PrintEvent.PrintOrders -> {

            }
        }
    }


    private fun printRestaurantDetails(): String {
//        val imagePrint = PrinterTextParserImg.bitmapToHexadecimalString(escposPrinter, resLogo)

//        var details = if (info.printResLogo) {
//            "[C]<img>$imagePrint</img>\n\n"
//        }else " \n"

        val details = "[C]--------- ORDER BILL ---------\n\n"

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
            val productName = createDottedString(it.productName, 15) // info.productNameLength

            products += "[L]${productName}[R]${it.productQuantity}[R]${it.productPrice}\n"
        }

        return products
    }

    private fun printTotalPrice(orderPrice: Pair<Int, Int>): String {
        return "[L]-------------------------------\n" +
                "[L]Total[R] Rs. ${orderPrice.first.minus(orderPrice.second)}\n" +
                "[L]-------------------------------\n\n"
    }

    private fun printQrCode(): String {
//        return if (info.printQRCode) {
//            "[C]Pay by scanning this QR code\n\n"+
//                    "[L]\n" +
//                    "[C]<qrcode size ='40'>${resInfo.paymentQrCode}</qrcode>\n\n\n" +
//                    "[C]Good Food, Good Mood\n\n" +
//                    "[L]-------------------------------\n"
//        }else ""
        return ""
    }

    private fun printFooterInfo(): String {
//        return if (info.printWelcomeText) {
//            "[C]Thank you for ordering!\n" +
//                    "[C]For order and inquiry, Call.\n" +
//                    "[C]${resInfo.primaryPhone} / ${resInfo.secondaryPhone}\n\n"
//        }else ""

        return ""
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

//        if (chargesList.isNotEmpty()){
//            charges += "[L]-------------------------------\n"
//            for (charge in chargesList) {
//                charges += "[L]${charge.chargesName}[R]${charge.chargesPrice.toString().toRupee}\n"
//            }
//        }

        return charges
    }

    private fun printSubTotalAndDiscount(orderPrice: Pair<Int, Int>): String {
        return "[L]-------------------------------\n" +
                "[L]Sub Total[R]${orderPrice.first}\n" +
                "[L]Discount[R]${orderPrice.second}\n"
    }
}