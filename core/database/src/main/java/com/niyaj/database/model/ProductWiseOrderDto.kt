package com.niyaj.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.niyaj.model.ProductAndQuantity

data class ProductWiseOrderDto(
    @Embedded
    val cartOrderEntity: CartOrderEntity,

    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId",
        entity = CartEntity::class,
        projection = ["productId", "quantity"]
    )
    val cartItems: List<ProductAndQuantity>,
)
