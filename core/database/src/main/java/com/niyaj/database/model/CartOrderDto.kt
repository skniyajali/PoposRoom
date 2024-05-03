/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.database.model

import androidx.room.Relation
import com.niyaj.model.Address
import com.niyaj.model.CartOrder
import com.niyaj.model.Customer
import com.niyaj.model.OrderStatus
import com.niyaj.model.OrderType
import java.util.Date

data class CartOrderDto(
    val orderId: Int,

    val orderType: OrderType,

    val orderStatus: OrderStatus,

    val doesChargesIncluded: Boolean,

    val customerId: Int,

    val addressId: Int,

    @Relation(
        parentColumn = "customerId",
        entity = CustomerEntity::class,
        entityColumn = "customerId",
    )
    val customer: Customer = Customer(),

    @Relation(
        parentColumn = "addressId",
        entity = AddressEntity::class,
        entityColumn = "addressId",
    )
    val address: Address = Address(),

    val createdAt: Date,

    val updatedAt: Date? = null,
)

fun CartOrderDto.toExternalModel(): CartOrder {
    return CartOrder(
        orderId = orderId,
        orderType = orderType,
        orderStatus = orderStatus,
        doesChargesIncluded = doesChargesIncluded,
        customer = customer,
        address = address,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}