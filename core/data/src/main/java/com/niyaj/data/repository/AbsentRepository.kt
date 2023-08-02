package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.Absent
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeWithAbsents
import kotlinx.coroutines.flow.Flow

interface AbsentRepository {

    fun getAllEmployee(): Flow<List<Employee>>

    suspend fun getEmployeeById(employeeId: Int) : Employee?

    suspend fun getAllEmployeeAbsents(searchText: String): Flow<List<EmployeeWithAbsents>>

    suspend fun getAllAbsent(searchText: String): Flow<List<Absent>>

    suspend fun getAbsentById(absentId: Int): Resource<Absent?>

    suspend fun addOrIgnoreAbsent(newAbsent: Absent): Resource<Boolean>

    suspend fun updateAbsent(newAbsent: Absent): Resource<Boolean>

    suspend fun upsertAbsent(newAbsent: Absent): Resource<Boolean>

    suspend fun deleteAbsent(absentId: Int): Resource<Boolean>

    suspend fun deleteAbsents(absentIds: List<Int>): Resource<Boolean>
}