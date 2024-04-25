package com.niyaj.model

import com.niyaj.model.utils.toTime
import java.util.Date

data class Order(
    val orderId: Int = 0,
    val orderType: OrderType = OrderType.DineIn,
    val customerPhone: String? = null,
    val customerAddress: String? = null,
    val orderDate: Date = Date(),
    val orderPrice: OrderPrice = OrderPrice(),
)


fun List<Order>.searchOrder(searchText: String): List<Order> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.orderId.toString().contains(searchText, true) ||
                    it.orderType.name.contains(searchText, true) ||
                    it.customerPhone?.contains(searchText, true) == true ||
                    it.customerAddress?.contains(searchText, true) == true ||
                    it.orderDate.toTime.contains(searchText, true) ||
                    it.orderPrice.basePrice.plus(it.orderPrice.discountPrice).toString()
                        .contains(searchText, true)
        }
    } else this
}