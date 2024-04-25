package com.niyaj.model

data class OrderPrice(
    val orderId: Int = 0,
    val basePrice: Long = 0,
    val discountPrice: Long = 0,
    val totalPrice: Long = 0,
)
