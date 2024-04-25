package com.niyaj.model

data class ProductWiseReport(
    val productId: Int,
    val productName: String,
    val quantity: Int = 0,
)
