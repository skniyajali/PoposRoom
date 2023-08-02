package com.niyaj.model

import java.util.Date

data class CartAddOnItems(
    val orderId: Int,

    val itemId: Int,

    val createdAt: Date = Date(),
)

data class CartOrderWithAddOnItemsId(
    val cartOrderEntity: CartOrder,

    val items: List<Int> = emptyList()
)

data class CartOrderWithAddOnItemsPrice(
    val cartOrderEntity: CartOrder,

    val items: List<Int> = emptyList()
)