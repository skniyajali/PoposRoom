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

    suspend fun findEmployeeByDate(absentDate: String, employeeId: Int, absentId: Int?): Boolean

    suspend fun importAbsentDataToDatabase(absentees: List<EmployeeWithAbsents>): Resource<Boolean>
}
