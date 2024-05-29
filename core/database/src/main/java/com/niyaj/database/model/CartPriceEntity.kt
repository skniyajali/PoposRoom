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
            onUpdate = ForeignKey.NO_ACTION,
        ),
    ],
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
