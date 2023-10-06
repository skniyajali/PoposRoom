package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.Absent
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeAbsentDates
import com.niyaj.model.EmployeeMonthlyDate
import com.niyaj.model.EmployeePayments
import com.niyaj.model.EmployeeSalaryEstimation
import com.niyaj.model.Payment
import kotlinx.coroutines.flow.Flow

interface EmployeeRepository {

    suspend fun getAllEmployee(searchText: String): Flow<List<Employee>>

    suspend fun getEmployeeById(employeeId: Int): Employee?

    suspend fun getEmployeePaymentById(employeeId: Int): Resource<Payment?>

    suspend fun getEmployeeAbsentById(employeeId: Int): Resource<Absent?>

    suspend fun findEmployeeAttendanceByAbsentDate(
        absentDate: String,
        employeeId: Int,
        absentId: Int? = null,
    ): Boolean

    suspend fun getEmployeeSalaryEstimation(
        employeeId: Int,
        selectedDate: Pair<String, String>?,
    ): Flow<EmployeeSalaryEstimation>

    suspend fun getEmployeeAbsentDates(employeeId: Int): Flow<List<EmployeeAbsentDates>>

    suspend fun getEmployeePayments(employeeId: Int): Flow<List<EmployeePayments>>

    suspend fun getSalaryCalculableDate(employeeId: Int): List<EmployeeMonthlyDate>

    suspend fun addOrIgnoreEmployee(newEmployee: Employee): Resource<Boolean>

    suspend fun updateEmployee(newEmployee: Employee): Resource<Boolean>

    suspend fun upsertEmployee(newEmployee: Employee): Resource<Boolean>

    suspend fun deleteEmployee(employeeId: Int): Resource<Boolean>

    suspend fun deleteEmployees(employeeIds: List<Int>): Resource<Boolean>

    suspend fun importEmployeesToDatabase(employees: List<Employee>): Resource<Boolean>
}