package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.Relation
import java.util.Date

@Entity(
    tableName = "cart_addon_items",
    primaryKeys = ["orderId", "itemId"],
    foreignKeys = [
        ForeignKey(
            entity = CartOrderEntity::class,
            parentColumns = arrayOf("orderId"),
            childColumns = arrayOf("orderId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = AddOnItemEntity::class,
            parentColumns = arrayOf("itemId"),
            childColumns = arrayOf("itemId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class CartAddOnItemsEntity(
    @ColumnInfo(index = true)
    val orderId: Int,

    @ColumnInfo(index = true)
    val itemId: Int,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date = Date(),
)


data class CartOrderWithAddOnItemsIdDto(
    @Embedded
    val cartOrderEntity: CartOrderEntity,

    @Relation(
        parentColumn = "orderId",
        entity = AddOnItemEntity::class,
        entityColumn = "itemId",
        associateBy = Junction(CartAddOnItemsEntity::class),
        projection = ["itemId"]
    )
    val items: List<Int> = emptyList()
)

data class CartOrderWithAddOnItemsPriceDto(
    @Embedded
    val cartOrderEntity: CartOrderEntity,

    @Relation(
        parentColumn = "orderId",
        entity = AddOnItemEntity::class,
        entityColumn = "itemId",
        associateBy = Junction(CartAddOnItemsEntity::class),
        projection = ["itemPrice"]
    )
    val items: List<Int> = emptyList()
)