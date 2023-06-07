package com.niyaj.poposroom.features.employee.domain.repository

import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.employee.domain.model.Employee
import kotlinx.coroutines.flow.Flow

interface EmployeeRepository {

    suspend fun getAllEmployee(searchText: String): Flow<List<Employee>>

    suspend fun getEmployeeById(employeeId: Int): Resource<Employee?>

    suspend fun addOrIgnoreEmployee(newEmployee: Employee): Resource<Boolean>

    suspend fun updateEmployee(newEmployee: Employee): Resource<Boolean>

    suspend fun upsertEmployee(newEmployee: Employee): Resource<Boolean>

    suspend fun deleteEmployee(employeeId: Int): Resource<Boolean>

    suspend fun deleteEmployees(employeeIds: List<Int>): Resource<Boolean>
}