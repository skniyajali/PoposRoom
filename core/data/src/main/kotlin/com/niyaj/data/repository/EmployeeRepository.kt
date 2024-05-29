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

package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeAbsentDates
import com.niyaj.model.EmployeeMonthlyDate
import com.niyaj.model.EmployeePayments
import com.niyaj.model.EmployeeSalaryEstimation
import kotlinx.coroutines.flow.Flow

interface EmployeeRepository {

    suspend fun getAllEmployee(searchText: String): Flow<List<Employee>>

    suspend fun getEmployeeById(employeeId: Int): Employee?

    suspend fun getEmployeeSalaryEstimation(
        employeeId: Int,
        selectedDate: Pair<String, String>?,
    ): Flow<EmployeeSalaryEstimation>

    suspend fun getEmployeeAbsentDates(employeeId: Int): Flow<List<EmployeeAbsentDates>>

    suspend fun getEmployeePayments(employeeId: Int): Flow<List<EmployeePayments>>

    suspend fun getSalaryCalculableDate(employeeId: Int): List<EmployeeMonthlyDate>

    suspend fun upsertEmployee(newEmployee: Employee): Resource<Boolean>

    suspend fun deleteEmployees(employeeIds: List<Int>): Resource<Boolean>

    suspend fun importEmployeesToDatabase(employees: List<Employee>): Resource<Boolean>
}
