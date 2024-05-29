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

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Customer(
    val customerId: Int = 0,

    val customerPhone: String = "",

    val customerName: String? = null,

    val customerEmail: String? = null,

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long? = null,
)

fun List<Customer>.searchCustomer(searchText: String): List<Customer> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.customerPhone.contains(searchText, true) ||
                it.customerEmail.toString().contains(searchText, true) ||
                it.customerName.toString().contains(searchText, true)
        }
    } else {
        this
    }
}
