package com.niyaj.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class OrderDetails(
    val cartOrder: CartOrder = CartOrder(),
    val cartProducts: List<CartProductItem> = emptyList(),
    val addOnItems: Flow<List<AddOnItem>> = emptyFlow(),
    val charges: Flow<List<Charges>> = emptyFlow(),
    val orderPrice: OrderPrice = OrderPrice()
)
