package com.niyaj.poposroom.features.cart_order.domain.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.Relation
import com.niyaj.poposroom.features.charges.domain.model.Charges
import java.util.Date

@Entity(
    tableName = "cart_charges",
    primaryKeys = ["cartOrderId", "chargesId"],
    foreignKeys = [
        ForeignKey(
            entity = CartOrderEntity::class,
            parentColumns = arrayOf("cartOrderId"),
            childColumns = arrayOf("cartOrderId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = Charges::class,
            parentColumns = arrayOf("chargesId"),
            childColumns = arrayOf("chargesId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class CartCharges(
    @ColumnInfo(index = true)
    val cartOrderId: Int,

    @ColumnInfo(index = true)
    val chargesId: Int,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date = Date(),
)


data class CartOrderWithChargesId(
    @Embedded
    val cartOrderEntity: CartOrderEntity,

    @Relation(
        parentColumn = "cartOrderId",
        entity = Charges::class,
        entityColumn = "chargesId",
        associateBy = Junction(CartCharges::class),
        projection = ["chargesId"]
    )
    val items: List<Int> = emptyList()
)