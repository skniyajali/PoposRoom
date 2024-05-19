package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.Absent
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeWithAbsents
import kotlinx.coroutines.flow.Flow

interface AbsentRepository {

    fun getAllEmployee(): Flow<List<Employee>>

    suspend fun getEmployeeById(employeeId: Int): Employee?

    suspend fun getAllEmployeeAbsents(searchText: String): Flow<List<EmployeeWithAbsents>>

    suspend fun getAbsentById(absentId: Int): Resource<Absent?>

    suspend fun upsertAbsent(newAbsent: Absent): Resource<Boolean>

    suspend fun deleteAbsents(absentIds: List<Int>): Resource<Boolean>

    suspend fun importAbsentDataToDatabase(absentees: List<EmployeeWithAbsents>): Resource<Boolean>
}