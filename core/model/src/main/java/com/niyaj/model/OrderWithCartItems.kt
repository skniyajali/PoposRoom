package com.niyaj.model

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList

@Stable
data class OrderWithCartItems(
    val cartOrder: CartOrder,

    val cartItems: ImmutableList<ProductAndQuantity>,
)

@Stable
data class ProductAndQuantity(val productId: Int, val quantity: Int)