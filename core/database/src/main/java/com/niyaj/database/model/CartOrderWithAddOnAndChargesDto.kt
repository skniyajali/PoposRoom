package com.niyaj.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class CartOrderWithAddOnAndChargesDto(
    @Embedded
    val cartOrder: CartOrderEntity,

    @Relation(
        parentColumn = "orderId",
        entity = AddOnItemEntity::class,
        entityColumn = "itemId",
        associateBy = Junction(CartAddOnItemsEntity::class),
        projection = ["itemId"]
    )
    val addOnItems: List<Int> = emptyList(),

    @Relation(
        parentColumn = "orderId",
        entity = ChargesEntity::class,
        entityColumn = "chargesId",
        associateBy = Junction(CartChargesEntity::class),
        projection = ["chargesId"]
    )
    val charges: List<Int> = emptyList()
)