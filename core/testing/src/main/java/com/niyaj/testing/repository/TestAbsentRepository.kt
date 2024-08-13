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

package com.niyaj.testing.repository

import com.niyaj.common.result.Resource
import com.niyaj.common.utils.getDateInMilliseconds
import com.niyaj.common.utils.getStartTime
import com.niyaj.data.repository.AbsentRepository
import com.niyaj.model.Absent
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeSalaryType.Monthly
import com.niyaj.model.EmployeeType.FullTime
import com.niyaj.model.EmployeeWithAbsents
import com.niyaj.model.searchAbsentees
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import org.jetbrains.annotations.TestOnly

class TestAbsentRepository : AbsentRepository {

    /**
     * The backing employee, absent, employee with absent list for testing
     */
    private val employeeList = MutableStateFlow(mutableListOf<Employee>())
    private val absentList = MutableStateFlow(mutableListOf<Absent>())
    private val employeeAbsents = MutableStateFlow(mutableListOf<EmployeeWithAbsents>())

    override fun getAllEmployee(): Flow<List<Employee>> = employeeList

    override suspend fun getEmployeeById(employeeId: Int): Employee? {
        return employeeList.value.find { it.employeeId == employeeId }
    }

    override suspend fun getAllEmployeeAbsents(searchText: String): Flow<List<EmployeeWithAbsents>> {
        return employeeAbsents.mapLatest { it.searchAbsentees(searchText) }
    }

    override suspend fun getAbsentById(absentId: Int): Resource<Absent?> {
        return Resource.Success(absentList.value.find { it.absentId == absentId })
    }

    override suspend fun upsertAbsent(newAbsent: Absent): Resource<Boolean> {
        val result = absentList.value.find { it.absentId == newAbsent.absentId }

        return Resource.Success(
            if (result == null) {
                absentList.value.add(newAbsent)
            } else {
                absentList.value.remove(result)
                absentList.value.add(newAbsent)
            },
        )
    }

    override suspend fun deleteAbsents(absentIds: List<Int>): Resource<Boolean> {
        return Resource.Success(absentList.value.removeAll { it.absentId in absentIds })
    }

    override suspend fun findEmployeeByDate(
        absentDate: String,
        employeeId: Int,
        absentId: Int?,
    ): Boolean {
        return absentList.value.any {
            if (absentId == null) {
                it.absentDate == absentDate && it.employeeId == employeeId
            } else {
                it.absentId != absentId && it.absentDate == absentDate &&
                    it.employeeId == employeeId
            }
        }
    }

    override suspend fun importAbsentDataToDatabase(absentees: List<EmployeeWithAbsents>): Resource<Boolean> {
        employeeAbsents.update { absentees.toMutableList() }

        return Resource.Success(true)
    }

    @TestOnly
    fun createTestData(): Absent {
        val newAbsent = Absent(
            absentId = 1,
            employeeId = 1,
            absentDate = getStartTime,
            absentReason = "Sick",
        )

        absentList.value.add(newAbsent)

        return newAbsent
    }

    @TestOnly
    fun updateEmployeeAbsents(absents: List<EmployeeWithAbsents>) {
        employeeAbsents.update { absents.toMutableList() }
    }

    @TestOnly
    fun updateEmployeeData(employees: List<Employee>) {
        employeeList.update { employees.toMutableList() }
    }

    @TestOnly
    fun updateAbsentData(absents: List<Absent>) {
        absentList.update { absents.toMutableList() }
    }

    @TestOnly
    fun createTestItem(): Employee {
        val newEmployee = Employee(
            employeeId = 1,
            employeeName = "Test Employee",
            employeePhone = "1234567890",
            employeeSalary = "10000",
            employeePosition = "Chef",
            employeeJoinedDate = getDateInMilliseconds(hour = 8, day = -30),
            employeeEmail = "test@gmail.com",
            employeeSalaryType = Monthly,
            employeeType = FullTime,
            isDeliveryPartner = true,
        )

        employeeList.value.add(newEmployee)

        return newEmployee
    }
}
