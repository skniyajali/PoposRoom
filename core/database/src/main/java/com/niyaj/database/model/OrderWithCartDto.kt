package com.niyaj.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.niyaj.model.Address
import com.niyaj.model.CartOrder
import com.niyaj.model.Customer
import com.niyaj.model.OrderWithCartItems
import com.niyaj.model.ProductAndQuantity

data class OrderWithCartDto(
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