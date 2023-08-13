package com.niyaj.model

data class OrderDetails(
    val cartOrder: CartOrder = CartOrder(),
    val cartProducts: List<CartProductItem> = emptyList(),
    val addOnItems: List<AddOnItem> = emptyList(),
    val charges: List<Charges> = emptyList(),
    val orderPrice: OrderPrice = OrderPrice(),
)
