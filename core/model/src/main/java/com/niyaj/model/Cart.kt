package com.niyaj.model

import java.util.Date

data class Cart(
    val cartId: Int = 0,

    val orderId: Int = 0,

    val productId: Int = 0,

    val quantity: Int = 0,

    val createdAt: Date = Date(),

    val updatedAt: Date? = null,
)

data class ProductPriceWithQuantity(

    val productPrice: Int,

    val quantity: Int,
)