/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.niyaj.model.Cart
import java.util.Date

@Entity(
    tableName = "cart",
    indices = [Index(value = ["orderId", "productId"])],
    foreignKeys = [
        ForeignKey(
            entity = CartOrderEntity::class,
            parentColumns = arrayOf("orderId"),
            childColumns = arrayOf("orderId"),
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = arrayOf("productId"),
            childColumns = arrayOf("productId"),
            onDelete = ForeignKey.CASCADE,
        ),
    ],
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
        updatedAt = this.updatedAt,
    )
}
