package com.niyaj.model

data class AddressWiseOrder(
    val orderId: Int,
    val customerPhone: String,
    val totalPrice: Long,
    val updatedAt: String,
    val customerName: String? = null,
)
