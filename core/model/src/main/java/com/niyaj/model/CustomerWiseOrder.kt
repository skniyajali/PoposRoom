package com.niyaj.model

import androidx.compose.runtime.Stable

@Stable
data class CustomerWiseOrder(
    val orderId: Int,
    val totalPrice: Long,
    val updatedAt: String,
    val customerAddress: String,
)
