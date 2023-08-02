package com.niyaj.model

import java.util.Date


data class CartCharges(
    val orderId: Int,

    val chargesId: Int,

    val createdAt: Date = Date(),
)

data class CartOrderWithChargesId(
    val cartOrderEntity: CartOrder,

    val items: List<Int> = emptyList()
)

data class CartOrderWithChargesPrice(
    val cartOrderEntity: CartOrder,

    val items: List<Int> = emptyList()
)