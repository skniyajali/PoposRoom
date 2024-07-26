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

package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.AbsentRepository
import com.niyaj.database.dao.AbsentDao
import com.niyaj.database.model.EmployeeEntity
import com.niyaj.database.model.EmployeeWithAbsentCrossRef
import com.niyaj.database.model.EmployeeWithAbsentsDto
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Absent
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeWithAbsents
import com.niyaj.model.filterEmployeeWithAbsent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AbsentRepositoryImpl @Inject constructor(
    private val absentDao: AbsentDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : AbsentRepository {

    override fun getAllEmployee(): Flow<List<Employee>> =
        absentDao.getAllEmployee().mapLatest { list ->
            list.map(EmployeeEntity::asExternalModel)
        }

    override suspend fun getEmployeeById(employeeId: Int): Employee? {
        return withContext(ioDispatcher) {
            absentDao.getEmployeeById(employeeId)?.asExternalModel()
        }
    }

    override suspend fun getAllEmployeeAbsents(searchText: String): Flow<List<EmployeeWithAbsents>> {
        return withContext(ioDispatcher) {
            absentDao.getAllAbsentEmployee().mapLatest { list ->
                list.filter { it.absents.isNotEmpty() }
                    .map(EmployeeWithAbsentsDto::asExternalModel)
                    .filterEmployeeWithAbsent(searchText)
            }
        }
    }

    override suspend fun getAbsentById(absentId: Int): Resource<Absent?> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(absentDao.getAbsentById(absentId)?.asExternalModel())
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun upsertAbsent(newAbsent: Absent): Resource<Boolean> {
        return try {
            val result = absentDao.upsertAbsent(newAbsent.toEntity())

            if (result > 0) {
                absentDao.upsertEmployeeWithAbsentCrossReference(
                    EmployeeWithAbsentCrossRef(newAbsent.employeeId, result.toInt()),
                )
            }

            Resource.Success(result > 0)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to add or update absent entry.")
        }
    }

    override suspend fun deleteAbsents(absentIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = absentDao.deleteAbsents(absentIds)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error("Unable to delete absent entries")
        }
    }

    override suspend fun findEmployeeByDate(
        absentDate: String,
        employeeId: Int,
        absentId: Int?,
    ): Boolean {
        return withContext(ioDispatcher) {
            absentDao.findEmployeeByDate(absentDate, employeeId, absentId) != null
        }
    }

    override suspend fun importAbsentDataToDatabase(absentees: List<EmployeeWithAbsents>): Resource<Boolean> {
        try {
            absentees.forEach { empWithAbsents ->
                val findEmployee = withContext(ioDispatcher) {
                    absentDao.findEmployeeByName(
                        empWithAbsents.employee.employeeName,
                        empWithAbsents.employee.employeeId,
                    )
                }

                if (findEmployee != null) {
                    empWithAbsents.absents.forEach { newAbsent ->
                        upsertAbsent(newAbsent)
                    }
                } else {
                    val result = withContext(ioDispatcher) {
                        absentDao.upsertEmployee(empWithAbsents.employee.toEntity())
                    }

                    if (result > 0) {
                        empWithAbsents.absents.forEach { newAbsent ->
                            upsertAbsent(newAbsent)
                        }
                    } else {
                        return Resource.Error("Something went wrong inserting employee!")
                    }
                }
            }

            return Resource.Success(true)
        } catch (e: Exception) {
            return Resource.Error(e.message ?: "Unable to add or update absent entry.")
        }
    }
}
