package com.niyaj.model

import com.niyaj.model.utils.toTime
import java.util.Date

data class CartOrder(
    val orderId: Int = 0,

    val orderType: OrderType = OrderType.DineIn,

    val orderStatus: OrderStatus = OrderStatus.PROCESSING,

    val doesChargesIncluded: Boolean = false,

    val customer: Customer = Customer(),

    val address: Address = Address(),

    val createdAt: Date = Date(),

    val updatedAt: Date? = null,
)

fun List<CartOrder>.filterCartOrder(searchText: String): List<CartOrder> {
    return if (searchText.isNotEmpty()) {
        this.filter { cartOrder ->
            cartOrder.orderStatus.name.contains(searchText, true) ||
                    cartOrder.customer.customerPhone.contains(searchText, true) ||
                    cartOrder.customer.customerName?.contains(searchText, true) == true ||
                    cartOrder.address.addressName.contains(searchText, true) ||
                    cartOrder.address.shortName.contains(searchText, true) ||
                    cartOrder.orderType.name.contains(searchText, true) ||
                    cartOrder.orderId.toString().contains(searchText, true) ||
                    cartOrder.createdAt.toTime.contains(searchText, true) ||
                    cartOrder.updatedAt?.toTime?.contains(searchText, true) == true
        }
    } else this
}