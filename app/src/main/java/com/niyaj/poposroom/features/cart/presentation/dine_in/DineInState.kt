package com.niyaj.poposroom.features.cart.presentation.dine_in

import com.niyaj.poposroom.features.cart.domain.model.CartItem

data class DineInState(
    val isLoading: Boolean = true,
    val items: List<CartItem> = emptyList()
)
