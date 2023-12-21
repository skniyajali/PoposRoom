package com.niyaj.model

import androidx.compose.runtime.Stable

@Stable
data class ProductWiseReport(
    val productId: Int,
    val productName: String,
    val quantity: Int = 0
)
