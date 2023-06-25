package com.niyaj.poposroom.features.cart.presentation.dine_out

import com.niyaj.poposroom.features.cart.domain.model.CartItem

data class DineOutState(
    val isLoading: Boolean = true,
    val items: List<CartItem> = emptyList(),
)
