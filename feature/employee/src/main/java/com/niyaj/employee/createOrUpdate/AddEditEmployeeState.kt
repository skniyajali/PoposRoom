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

package com.niyaj.employee.createOrUpdate

import com.niyaj.common.utils.toMilliSecond
import com.niyaj.model.EmployeeSalaryType
import com.niyaj.model.EmployeeType
import java.time.LocalDate

data class AddEditEmployeeState(
    val employeePhone: String = "",
    val employeeName: String = "",
    val employeeSalary: String = "",
    val employeePosition: String = "",
    val employeeEmail: String? = null,
    val employeeSalaryType: EmployeeSalaryType = EmployeeSalaryType.Monthly,
    val employeeType: EmployeeType = EmployeeType.FullTime,
    val employeeJoinedDate: String = LocalDate.now().toMilliSecond,
)

val positions = listOf(
    "Master",
    "Assistant",
    "Captain",
    "Manager",
    "Cleaner",
    "Senior Cook",
    "Junior Cook",
    "Chef",
)
