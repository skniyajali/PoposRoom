package com.niyaj.model

import androidx.compose.runtime.Stable

@Stable
data class OrderPrice(
    val orderId: Int = 0,
    val basePrice: Long = 0,
    val discountPrice: Long = 0,
    val totalPrice: Long = 0,
)
