package com.niyaj.model

data class CustomerWiseOrder(
    val orderId: Int,
    val totalPrice: Long,
    val updatedAt: String,
    val customerAddress: String,
)
