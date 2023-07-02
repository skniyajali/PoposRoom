package com.niyaj.poposroom.features.order.domain.model

import com.niyaj.poposroom.features.cart.domain.model.OrderPrice
import com.niyaj.poposroom.features.cart_order.domain.utils.OrderType
import com.niyaj.poposroom.features.common.utils.toTime
import java.util.Date

data class Order(
    val orderId: Int = 0,
    val orderType: OrderType = OrderType.DineIn,
    val customerPhone: String? = null,
    val customerAddress: String? = null,
    val orderDate: Date = Date(),
    val orderPrice : OrderPrice = OrderPrice()
)


fun List<Order>.searchOrder(searchText: String): List<Order> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.orderId.toString().contains(searchText,true) ||
            it.orderType.name.contains(searchText,true) ||
            it.customerPhone?.contains(searchText,true) == true ||
            it.customerAddress?.contains(searchText,true) == true ||
            it.orderDate.toTime.contains(searchText,true) ||
            it.orderPrice.totalPrice.plus(it.orderPrice.discountPrice).toString()
                .contains(searchText,true)
        }
    }else this
}