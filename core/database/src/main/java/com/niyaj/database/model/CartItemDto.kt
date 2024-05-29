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

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.niyaj.model.ProductAndQuantity

data class CartItemDto(
    @Embedded
    val cartOrder: CartOrderEntity,

    @Relation(
        parentColumn = "addressId",
        entityColumn = "addressId",
        entity = AddressEntity::class,
        projection = ["shortName"],
    )
    val customerAddress: String?,

    @Relation(
        parentColumn = "customerId",
        entityColumn = "customerId",
        entity = CustomerEntity::class,
        projection = ["customerPhone"],
    )
    val customerPhone: String?,

    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId",
        entity = CartEntity::class,
        projection = ["productId", "quantity"],
    )
    val cartItems: List<ProductAndQuantity>,

    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId",
        entity = CartPriceEntity::class,
        projection = ["totalPrice"],
    )
    val orderPrice: OrderTotalPrice,

    @Relation(
        parentColumn = "orderId",
        entity = AddOnItemEntity::class,
        entityColumn = "itemId",
        associateBy = Junction(CartAddOnItemsEntity::class),
        projection = ["itemId"],
    )
    val addOnItems: List<Int> = emptyList(),

    @Relation(
        parentColumn = "orderId",
        entity = ChargesEntity::class,
        entityColumn = "chargesId",
        associateBy = Junction(CartChargesEntity::class),
        projection = ["chargesId"],
    )
    val charges: List<Int> = emptyList(),
)
