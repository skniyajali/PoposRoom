package com.niyaj.model

data class CartItem(
    val orderId: Int = 0,
    val orderType: OrderType = OrderType.DineIn,
    val cartProducts: List<CartProductItem> = emptyList(),
    val addOnItems: List<Int> = emptyList(),
    val charges: List<Int> = emptyList(),
    val customerPhone: String? = null,
    val customerAddress: String? = null,
    val updatedAt: String = "",
    val orderPrice: OrderPrice = OrderPrice(),
)