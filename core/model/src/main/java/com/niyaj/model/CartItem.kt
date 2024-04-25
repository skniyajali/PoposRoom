package com.niyaj.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class CartItem(
    val orderId: Int = 0,
    val orderType: OrderType = OrderType.DineIn,
    val cartProducts: ImmutableList<CartProductItem> = persistentListOf(),
    val addOnItems: ImmutableList<Int> = persistentListOf(),
    val charges: ImmutableList<Int> = persistentListOf(),
    val customerPhone: String? = null,
    val customerAddress: String? = null,
    val updatedAt: String = "",
    val orderPrice: Long = 0,
)