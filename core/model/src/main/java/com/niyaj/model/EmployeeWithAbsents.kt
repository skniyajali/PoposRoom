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
import com.niyaj.model.utils.toDateString
import com.niyaj.model.utils.toJoinedDate
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EmployeeWithAbsents(
    val employee: Employee,

    val absents: List<Absent> = emptyList(),
)


/**
 * Filter absent employee by date and absent reason
 */
fun Absent.filterAbsent(searchText: String): Boolean {
    return this.absentDate.toJoinedDate.contains(searchText, true) ||
            this.absentReason.contains(searchText, true) ||
            this.absentDate.toDate.contains(searchText, true) ||
            this.createdAt.toDateString.contains(searchText, true)
}

fun List<Absent>.filterAbsent(searchText: String): List<Absent> {
    return if (this.isNotEmpty()) {
        this.filter { it.filterAbsent(searchText) }
    }else this
}

fun List<EmployeeWithAbsents>.filterEmployeeWithAbsent(searchText: String): List<EmployeeWithAbsents> {
    return if (searchText.isNotEmpty()) {
        this.map {
            EmployeeWithAbsents(
                employee = it.employee,
                absents = it.absents.filterAbsent(searchText)
            )
        }
    }else this
}