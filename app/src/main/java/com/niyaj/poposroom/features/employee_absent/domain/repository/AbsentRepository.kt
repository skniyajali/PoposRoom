package com.niyaj.poposroom.features.employee_absent.domain.repository

import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.employee.domain.model.Employee
import com.niyaj.poposroom.features.employee_absent.domain.model.Absent
import com.niyaj.poposroom.features.employee_absent.domain.model.EmployeeWithAbsent
import kotlinx.coroutines.flow.Flow

interface AbsentRepository {

    fun getAllEmployee(): Flow<List<Employee>>

    suspend fun getEmployeeById(employeeId: Int) : Employee?

    suspend fun getAllEmployeeAbsents(searchText: String): Flow<List<EmployeeWithAbsent>>

    suspend fun getAllAbsent(searchText: String): Flow<List<Absent>>

    suspend fun getAbsentById(absentId: Int): Resource<Absent?>

    suspend fun addOrIgnoreAbsent(newAbsent: Absent): Resource<Boolean>

    suspend fun updateAbsent(newAbsent: Absent): Resource<Boolean>

    suspend fun upsertAbsent(newAbsent: Absent): Resource<Boolean>

    suspend fun deleteAbsent(absentId: Int): Resource<Boolean>

    suspend fun deleteAbsents(absentIds: List<Int>): Resource<Boolean>
}