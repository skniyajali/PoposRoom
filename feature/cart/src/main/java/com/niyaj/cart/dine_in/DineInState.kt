package com.niyaj.cart.dine_in

import com.niyaj.model.CartItem

data class DineInState(
    val isLoading: Boolean = true,
    val items: List<CartItem> = emptyList(),
)
