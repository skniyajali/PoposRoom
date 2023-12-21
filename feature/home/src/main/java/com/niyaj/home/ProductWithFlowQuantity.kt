package com.niyaj.home

import kotlinx.coroutines.flow.StateFlow

data class ProductWithFlowQuantity(
    val categoryId: Int,
    val productId: Int,
    val productName: String,
    val productPrice: Int,
    val quantity: StateFlow<Int>
)