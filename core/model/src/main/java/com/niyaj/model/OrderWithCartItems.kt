package com.niyaj.model

data class OrderWithCartItems(
    val cartOrder: CartOrder,

    val cartItems: List<ProductAndQuantity>,
)


data class ProductAndQuantity(val productId: Int, val quantity: Int)