package com.niyaj.poposroom.features.cart.domain.model

import androidx.room.Embedded
import androidx.room.Relation
import com.niyaj.poposroom.features.cart_order.domain.model.CartOrderEntity

data class OrderWithCart(
    @Embedded
    val cartOrder: CartOrderEntity,

    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId",
        entity = CartEntity::class,
        projection = ["productId", "quantity"]
    )
    val cartItems: List<ProductAndQuantity>,
)


data class ProductAndQuantity(val productId: Int, val quantity: Int)