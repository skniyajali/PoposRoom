package com.niyaj.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.niyaj.model.ProductAndQuantity

data class OrderDto(
    @Embedded
    val cartOrder: CartOrderEntity,
    
    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId",
        entity = CartPriceEntity::class,
        associateBy = Junction(CartPriceEntity::class)
    )
    val orderPrice: CartPriceEntity,
)