package com.niyaj.poposroom.features.cart_order.presentation.add_edit

import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderType

data class AddEditCartOrderState(
    val orderType: CartOrderType = CartOrderType.DineIn,
    val doesChargesIncluded: Boolean = false,
)
