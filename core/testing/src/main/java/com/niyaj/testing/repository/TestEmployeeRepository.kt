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

package com.niyaj.testing.repository

import com.niyaj.common.result.Resource
import com.niyaj.common.utils.getStartTime
import com.niyaj.data.repository.EmployeeRepository
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeAbsentDates
import com.niyaj.model.EmployeeMonthlyDate
import com.niyaj.model.EmployeePayments
import com.niyaj.model.EmployeeSalaryEstimation
import com.niyaj.model.EmployeeSalaryType.Monthly
import com.niyaj.model.EmployeeType.FullTime
import com.niyaj.model.searchEmployee
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import org.jetbrains.annotations.TestOnly

class TestEmployeeRepository: EmployeeRepository {

    /**
     * The backing employee list for testing
     */
    private val employeeList = MutableStateFlow(mutableListOf<Employee>())
    private val employeePayments = MutableStateFlow(listOf<EmployeePayments>())
    private val employeeAbsentDates = MutableStateFlow(listOf<EmployeeAbsentDates>())
    private val employeeMonthlyDate = MutableStateFlow(listOf<EmployeeMonthlyDate>())
    private val employeeSalaryEstimation = MutableStateFlow(EmployeeSalaryEstimation())

    override suspend fun getAllEmployee(searchText: String): Flow<List<Employee>> {
        return employeeList.mapLatest { it.searchEmployee(searchText) }
    }

    override suspend fun getEmployeeById(employeeId: Int): Employee? {
        return employeeList.value.find { it.employeeId == employeeId }
    }

    override suspend fun getEmployeeSalaryEstimation(
        employeeId: Int,
        selectedDate: Pair<String, String>?,
    ): Flow<EmployeeSalaryEstimation> {
        return employeeSalaryEstimation
    }

    override suspend fun getEmployeeAbsentDates(employeeId: Int): Flow<List<EmployeeAbsentDates>> {
        return employeeAbsentDates
    }

    override suspend fun getEmployeePayments(employeeId: Int): Flow<List<EmployeePayments>> {
        return employeePayments
    }

    override suspend fun getSalaryCalculableDate(employeeId: Int): List<EmployeeMonthlyDate> {
        return employeeMonthlyDate.value
    }

    override suspend fun upsertEmployee(newEmployee: Employee): Resource<Boolean> {
        val result = employeeList.value.find { it.employeeId == newEmployee.employeeId }

        return Resource.Success(
            if (result == null) {
                employeeList.value.add(newEmployee)
            } else {
                employeeList.value.remove(result)
                employeeList.value.add(newEmployee)
            },
        )
    }

    override suspend fun deleteEmployees(employeeIds: List<Int>): Resource<Boolean> {
        return Resource.Success(employeeList.value.removeAll { it.employeeId in employeeIds })
    }

    override suspend fun findEmployeeByPhone(phone: String, employeeId: Int?): Boolean {
        return employeeList.value.any {
            if (employeeId != null) {
                it.employeePhone == phone && it.employeeId != employeeId
            } else {
                it.employeePhone == phone
            }
        }
    }

    override suspend fun findEmployeeByName(name: String, employeeId: Int?): Boolean {
        return employeeList.value.any {
            if (employeeId != null) {
                it.employeeName == name && it.employeeId != employeeId
            } else {
                it.employeeName == name
            }
        }
    }

    override suspend fun importEmployeesToDatabase(employees: List<Employee>): Resource<Boolean> {
        employees.forEach { upsertEmployee(it) }

        return Resource.Success(true)
    }

    @TestOnly
    fun updateEmployeePayments(payments: List<EmployeePayments>) {
        employeePayments.update { payments.toMutableList() }
    }

    @TestOnly
    fun updateEmployeeData(employees: List<Employee>) {
        employeeList.update { employees.toMutableList() }
    }

    @TestOnly
    fun updateAbsentDates(absentDates: List<EmployeeAbsentDates>) {
        employeeAbsentDates.update { absentDates.toMutableList() }
    }

    @TestOnly
    fun updateMonthlyDate(monthlyDate: List<EmployeeMonthlyDate>) {
        employeeMonthlyDate.update { monthlyDate.toMutableList() }
    }

    @TestOnly
    fun updateSalaryEstimation(salaryEstimation: EmployeeSalaryEstimation) {
        employeeSalaryEstimation.update { salaryEstimation }
    }

    fun createTestItem(): Employee {
        val newEmployee =  Employee(
            employeeId = 1,
            employeeName = "Test Employee",
            employeePhone = "1234567890",
            employeeSalary = "10000",
            employeePosition = "Chef",
            employeeJoinedDate = getStartTime,
            employeeEmail = "test@gmail.com",
            employeeSalaryType = Monthly,
            employeeType = FullTime,
            isDeliveryPartner = true,
        )

        employeeList.value.add(newEmployee)

        return newEmployee
    }
}