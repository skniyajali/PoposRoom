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

package com.niyaj.model

import com.niyaj.model.utils.toTime
import java.util.Date

data class Order(
    val orderId: Int = 0,
    val orderType: OrderType = OrderType.DineIn,
    val customerPhone: String? = null,
    val customerAddress: String? = null,
    val deliveryPartnerName: String? = null,
    val orderDate: Date = Date(),
    val orderPrice: Long = 0,
)

fun List<Order>.searchOrder(searchText: String): List<Order> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.orderId.toString().contains(searchText, true) ||
                it.orderType.name.contains(searchText, true) ||
                it.customerPhone?.contains(searchText, true) == true ||
                it.customerAddress?.contains(searchText, true) == true ||
                it.orderDate.toTime.contains(searchText, true) ||
                it.orderPrice.toString().contains(searchText, true)
        }
    } else {
        this
    }
}
