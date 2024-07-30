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

import java.util.Date

data class DeliveryReport(
    val orderId: Int,

    val customerAddress: String,

    val customerPhone: String,

    val orderPrice: Long,

    val partnerId: Int = 0,

    val partnerName: String? = null,

    val orderDate: Date,
)

fun List<DeliveryReport>.searchOrder(searchText: String): List<DeliveryReport> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.customerAddress.contains(searchText, ignoreCase = true) ||
                it.customerPhone.contains(searchText, ignoreCase = true) ||
                it.orderId.toString().contains(searchText, ignoreCase = true) ||
                it.orderPrice.toString().contains(searchText, ignoreCase = true)
        }
    } else {
        this
    }
}
