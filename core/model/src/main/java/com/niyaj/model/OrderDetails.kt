package com.niyaj.model

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class OrderDetails(
    val cartOrder: CartOrder = CartOrder(),
    val cartProducts: ImmutableList<CartProductItem> = persistentListOf(),
    val addOnItems: ImmutableList<AddOnItem> = persistentListOf(),
    val charges: ImmutableList<Charges> = persistentListOf(),
    val orderPrice: OrderPrice = OrderPrice(),
)
