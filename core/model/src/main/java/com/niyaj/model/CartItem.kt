package com.niyaj.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

data class CartItem(
    val orderId: Int = 0,
    val orderType: OrderType = OrderType.DineIn,
    val cartProducts: List<CartProductItem> = emptyList(),
    val addOnItems: Flow<List<Int>> = emptyFlow(),
    val charges: Flow<List<Int>> = emptyFlow(),
    val customerPhone: String? = null,
    val customerAddress: String? = null,
    val updatedAt: String = "",
    val orderPrice: Flow<OrderPrice> = flowOf(OrderPrice()),
)

data class OrderPrice(
    val totalPrice: Int = 0,
    val discountPrice: Int = 0,
)


data class CartItems(
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