package com.niyaj.poposroom.features.cart.domain.model

data class CartProductItem(
    val productId: Int = 0,
    val productName: String = "",
    val productPrice: Int = 0,
    val productQuantity: Int = 0,
)
