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
data class Employee(
    val employeeId: Int = 0,

    val employeeName: String = "",

    val employeePhone: String = "",

    val employeeSalary: String = "",

    val employeePosition: String = "",

    val employeeJoinedDate: String = "",

    val employeeEmail: String? = null,

    val employeeSalaryType: EmployeeSalaryType = EmployeeSalaryType.Daily,

    val employeeType: EmployeeType = EmployeeType.FullTime,

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long? = null,
)

fun List<Employee>.searchEmployee(searchText: String): List<Employee> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.employeeName.contains(searchText, true) ||
                it.employeePosition.contains(searchText, true) ||
                it.employeePhone.contains(searchText, true) ||
                it.employeeSalaryType.name.contains(searchText, true) ||
                it.employeeSalary.contains(searchText, true) ||
                it.employeeJoinedDate.contains(searchText, true)
        }
    } else {
        this
    }
}

/**
 * Filter employee by search text
 * @param searchText String
 * @return Boolean
 */
fun Employee.filterEmployee(searchText: String): Boolean {
    return this.employeeName.contains(searchText, true) ||
        this.employeePosition.contains(searchText, true) ||
        this.employeePhone.contains(searchText, true) ||
        this.employeeSalaryType.name.contains(searchText, true) ||
        this.employeeSalary.contains(searchText, true) ||
        this.employeeJoinedDate.contains(searchText, true)
}
