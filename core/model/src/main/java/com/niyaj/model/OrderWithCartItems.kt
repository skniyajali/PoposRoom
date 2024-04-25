package com.niyaj.model

import kotlinx.collections.immutable.ImmutableList

data class OrderWithCartItems(
    val cartOrder: CartOrder,

    val cartItems: ImmutableList<ProductAndQuantity>,
)

data class ProductAndQuantity(val productId: Int, val quantity: Int)
