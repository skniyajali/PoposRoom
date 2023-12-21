package com.niyaj.model

import androidx.compose.runtime.Stable

@Stable
data class AddressWiseOrder(
    val orderId: Int,
    val customerPhone: String,
    val totalPrice: Long,
    val updatedAt: String,
    val customerName: String? = null,
)
