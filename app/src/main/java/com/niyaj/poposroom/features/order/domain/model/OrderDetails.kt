package com.niyaj.poposroom.features.order.domain.model

import com.niyaj.poposroom.features.addon_item.domain.model.AddOnItem
import com.niyaj.poposroom.features.cart.domain.model.CartProductItem
import com.niyaj.poposroom.features.cart.domain.model.OrderPrice
import com.niyaj.poposroom.features.cart_order.domain.model.CartOrder
import com.niyaj.poposroom.features.charges.domain.model.Charges
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class OrderDetails(
    val cartOrder: CartOrder = CartOrder(),
    val cartProducts: List<CartProductItem> = emptyList(),
    val addOnItems: Flow<List<AddOnItem>> = emptyFlow(),
    val charges: Flow<List<Charges>> = emptyFlow(),
    val orderPrice: OrderPrice = OrderPrice()
)
