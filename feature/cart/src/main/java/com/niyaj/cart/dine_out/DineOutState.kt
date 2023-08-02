package com.niyaj.cart.dine_out

import com.niyaj.model.CartItem

data class DineOutState(
    val isLoading: Boolean = true,
    val items: List<CartItem> = emptyList(),
)
