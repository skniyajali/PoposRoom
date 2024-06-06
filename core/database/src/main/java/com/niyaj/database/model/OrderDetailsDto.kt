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
import com.niyaj.model.EmployeeNameAndId
import com.niyaj.model.OrderPrice
import com.niyaj.model.ProductAndQuantity

data class OrderDetailsDto(
    @Embedded
    val cartOrder: CartOrderDto,

    @Relation(
        parentColumn = "orderId",
        entity = AddOnItemEntity::class,
        entityColumn = "itemId",
        associateBy = Junction(CartAddOnItemsEntity::class),
    )
    val addOnItems: List<AddOnItemEntity> = emptyList(),

    @Relation(
        parentColumn = "orderId",
        entity = ChargesEntity::class,
        entityColumn = "chargesId",
        associateBy = Junction(CartChargesEntity::class),
    )
    val charges: List<ChargesEntity> = emptyList(),

    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId",
        entity = CartPriceEntity::class,
    )
    val orderPrice: OrderPrice,

    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId",
        entity = CartEntity::class,
        projection = ["productId", "quantity"],
    )
    val cartItems: List<ProductAndQuantity>,

    @Relation(
        parentColumn = "deliveryPartnerId",
        entityColumn = "employeeId",
        entity = EmployeeEntity::class,
        projection = ["employeeId", "employeeName", "partnerQRCode"],
    )
    val deliveryPartner: EmployeeNameAndId? = null,
)
