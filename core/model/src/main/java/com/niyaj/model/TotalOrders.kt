package com.niyaj.model

import androidx.compose.runtime.Stable

@Stable
data class TotalOrders(
    val totalOrders: Long = 0,
    val totalAmount: Long = 0
)
