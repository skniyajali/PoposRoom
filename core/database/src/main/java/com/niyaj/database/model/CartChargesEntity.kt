package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.Relation
import java.util.Date

@Entity(
    tableName = "cart_charges",
    primaryKeys = ["orderId", "chargesId"],
    foreignKeys = [
        ForeignKey(
            entity = CartOrderEntity::class,
            parentColumns = arrayOf("orderId"),
            childColumns = arrayOf("orderId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = ChargesEntity::class,
            parentColumns = arrayOf("chargesId"),
            childColumns = arrayOf("chargesId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class CartChargesEntity(
    @ColumnInfo(index = true)
    val orderId: Int,

    @ColumnInfo(index = true)
    val chargesId: Int,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date = Date(),
)


data class CartOrderWithChargesIdDto(
    @Embedded
    val cartOrderEntity: CartOrderEntity,

    @Relation(
        parentColumn = "orderId",
        entity = ChargesEntity::class,
        entityColumn = "chargesId",
        associateBy = Junction(CartChargesEntity::class),
        projection = ["chargesId"]
    )
    val items: List<Int> = emptyList()
)

data class CartOrderWithChargesPriceDto(
    @Embedded
    val cartOrderEntity: CartOrderEntity,

    @Relation(
        parentColumn = "orderId",
        entity = ChargesEntity::class,
        entityColumn = "chargesId",
        associateBy = Junction(CartChargesEntity::class),
        projection = ["chargesId"]
    )
    val items: List<Int> = emptyList()
)