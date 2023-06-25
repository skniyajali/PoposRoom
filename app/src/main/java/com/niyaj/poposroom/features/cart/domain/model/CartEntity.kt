package com.niyaj.poposroom.features.cart.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.niyaj.poposroom.features.cart_order.domain.model.CartOrderEntity
import com.niyaj.poposroom.features.product.domain.model.Product
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
            entity = Product::class,
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
    val orderId: Int = 0,

    @ColumnInfo(index = true)
    val productId: Int = 0,

    val quantity: Int = 0,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date = Date(),

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val updatedAt: Date? = null,
)


data class ProductPriceWithQuantity(

    val productPrice: Int,

    val quantity: Int
)