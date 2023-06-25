package com.niyaj.poposroom.features.cart_order.presentation.add_edit

import com.niyaj.poposroom.features.cart_order.domain.utils.OrderType

data class AddEditCartOrderState(
    val orderType: OrderType = OrderType.DineIn,
    val doesChargesIncluded: Boolean = false,
)
