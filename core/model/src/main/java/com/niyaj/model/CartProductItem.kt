package com.niyaj.model

import androidx.compose.runtime.Stable

@Stable
data class CartProductItem(
    val productId: Int = 0,
    val productName: String = "",
    val productPrice: Int = 0,
    val productQuantity: Int = 0,
)
