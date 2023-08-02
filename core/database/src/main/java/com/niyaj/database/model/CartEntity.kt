package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.niyaj.model.Cart
import java.util.Date

@Entity(
    tableName = "cart",
    foreignKeys = [
        ForeignKey(
            entity = CartOrderEntity::class,
            parentColumns = arrayOf("orderId"),
            childColumns = arrayOf("orderId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = arrayOf("productId"),
            childColumns = arrayOf("productId"),
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class CartEntity(
    @PrimaryKey(autoGenerate = true)
    val cartId: Int = 0,

    @ColumnInfo(index = true)
    val orderId: Int,

    @ColumnInfo(index = true)
    val productId: Int,

    val quantity: Int,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date = Date(),

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val updatedAt: Date? = null,
)

fun CartEntity.asExternalModel(): Cart {
    return Cart(
        cartId = this.cartId,
        orderId = this.orderId,
        productId = this.productId,
        quantity = this.quantity,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}