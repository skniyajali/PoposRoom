package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.niyaj.model.OrderPrice

@Entity(
    tableName = "cart_price",
    primaryKeys = ["orderId"],
    foreignKeys = [
        ForeignKey(
            entity = CartOrderEntity::class,
            parentColumns = arrayOf("orderId"),
            childColumns = arrayOf("orderId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class CartPriceEntity(
    @ColumnInfo(index = true)
    val orderId: Int,

    val basePrice: Long = 0,

    val discountPrice: Long = 0,

    val totalPrice: Long = 0,

    val createdAt: String = System.currentTimeMillis().toString(),
)


fun CartPriceEntity.toExternalModel(): OrderPrice {
    return OrderPrice(
        orderId = this.orderId,
        basePrice = this.basePrice,
        discountPrice = this.discountPrice,
        totalPrice = this.totalPrice,
    )
}