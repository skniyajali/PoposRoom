package com.niyaj.poposroom.features.cart.domain.model

import com.niyaj.poposroom.features.cart_order.domain.utils.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class CartItem(
    val orderId: Int = 0,
    val orderType: OrderType = OrderType.DineIn,
    val cartProducts: List<CartProductItem> = emptyList(),
    val addOnItems: Flow<List<Int>> = emptyFlow(),
    val charges: Flow<List<Int>> = emptyFlow(),
    val customerPhone: String? = null,
    val customerAddress: String? = null,
    val updatedAt: String = "",
    val orderPrice : OrderPrice = OrderPrice(),
)

data class OrderPrice(
    val totalPrice: Int = 0,
    val discountPrice: Int = 0,
)