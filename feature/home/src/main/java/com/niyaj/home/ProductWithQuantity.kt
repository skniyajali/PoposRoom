package com.niyaj.home

data class ProductWithQuantity(
    val categoryId: Int,
    val productId: Int,
    val productName: String,
    val productPrice: Int,
    val quantity: Int = 0,
)