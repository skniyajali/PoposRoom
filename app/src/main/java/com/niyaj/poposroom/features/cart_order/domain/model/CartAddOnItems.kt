package com.niyaj.poposroom.features.cart_order.domain.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.Relation
import com.niyaj.poposroom.features.addon_item.domain.model.AddOnItem
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
            entity = AddOnItem::class,
            parentColumns = arrayOf("itemId"),
            childColumns = arrayOf("itemId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class CartAddOnItems(
    @ColumnInfo(index = true)
    val orderId: Int,

    @ColumnInfo(index = true)
    val itemId: Int,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date = Date(),
)



data class CartOrderWithAddOnItemsId(
    @Embedded
    val cartOrderEntity: CartOrderEntity,

    @Relation(
        parentColumn = "orderId",
        entity = AddOnItem::class,
        entityColumn = "itemId",
        associateBy = Junction(CartAddOnItems::class),
        projection = ["itemId"]
    )
    val items: List<Int> = emptyList()
)

data class CartOrderWithAddOnItemsPrice(
    @Embedded
    val cartOrderEntity: CartOrderEntity,

    @Relation(
        parentColumn = "orderId",
        entity = AddOnItem::class,
        entityColumn = "itemId",
        associateBy = Junction(CartAddOnItems::class),
        projection = ["itemPrice"]
    )
    val items: List<Int> = emptyList()
)