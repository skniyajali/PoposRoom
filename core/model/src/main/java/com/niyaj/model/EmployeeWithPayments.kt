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

import com.niyaj.model.utils.toDate
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EmployeeWithPayments(
    val employee: Employee,

    val payments: List<Payment> = emptyList(),
)

fun List<Payment>.searchPayment(searchText: String): List<Payment> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.paymentAmount.contains(searchText, true) ||
                it.paymentType.name.contains(searchText, true) ||
                it.paymentDate.toDate.contains(searchText, true) ||
                it.paymentMode.name.contains(searchText, true) ||
                it.paymentNote.contains(searchText, true)
        }
    } else {
        this
    }
}

fun List<EmployeeWithPayments>.searchEmployeeWithPayments(searchText: String): List<EmployeeWithPayments> {
    return if (searchText.isNotEmpty()) {
        this.map {
            EmployeeWithPayments(
                employee = it.employee,
                payments = it.payments.searchPayment(searchText),
            )
        }
    } else {
        this
    }
}
