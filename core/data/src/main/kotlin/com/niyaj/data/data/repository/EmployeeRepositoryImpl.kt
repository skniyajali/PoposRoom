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
import com.niyaj.common.utils.Constants.NOT_PAID
import com.niyaj.common.utils.Constants.PAID
import com.niyaj.common.utils.compareSalaryDates
import com.niyaj.common.utils.getSalaryDates
import com.niyaj.common.utils.toRupee
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.EmployeeRepository
import com.niyaj.database.dao.EmployeeDao
import com.niyaj.database.model.EmployeeEntity
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeAbsentDates
import com.niyaj.model.EmployeeMonthlyDate
import com.niyaj.model.EmployeePayments
import com.niyaj.model.EmployeeSalaryEstimation
import com.niyaj.model.searchEmployee
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EmployeeRepositoryImpl @Inject constructor(
    private val employeeDao: EmployeeDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : EmployeeRepository {

    override suspend fun getAllEmployee(searchText: String): Flow<List<Employee>> {
        return withContext(ioDispatcher) {
            employeeDao.getAllEmployee().mapLatest {
                it.map(EmployeeEntity::asExternalModel).searchEmployee(searchText)
            }
        }
    }

    override suspend fun getEmployeeById(employeeId: Int): Employee? {
        return try {
            withContext(ioDispatcher) {
                employeeDao.getEmployeeById(employeeId)?.asExternalModel()
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun upsertEmployee(newEmployee: Employee): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                withContext(ioDispatcher) {
                    val result = employeeDao.upsertEmployee(newEmployee.toEntity())
                    Resource.Success(result > 0)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error creating Employee Item")
        }
    }

    override suspend fun deleteEmployees(employeeIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = employeeDao.deleteEmployee(employeeIds)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error("Unable to delete Employee")
        }
    }

    override suspend fun findEmployeeByPhone(phone: String, employeeId: Int?): Boolean {
        return withContext(ioDispatcher) {
            employeeDao.findEmployeeByPhone(phone, employeeId) != null
        }
    }

    override suspend fun findEmployeeByName(name: String, employeeId: Int?): Boolean {
        return withContext(ioDispatcher) {
            employeeDao.findEmployeeByName(name, employeeId) != null
        }
    }

    override suspend fun getEmployeeSalaryEstimation(
        employeeId: Int,
        selectedDate: Pair<String, String>?,
    ): Flow<EmployeeSalaryEstimation> {
        return channelFlow {
            try {
                val employeeSalary = withContext(ioDispatcher) {
                    employeeDao.getEmployeeSalary(employeeId)
                }
                val joinedDate = withContext(ioDispatcher) {
                    employeeDao.getEmployeeJoinedDate(employeeId)
                }
                val salaryDate = joinedDate?.let { getSalaryDates(it).first() } ?: Pair("", "")

                val firstDate = selectedDate?.first ?: salaryDate.first
                val secondDate = selectedDate?.second ?: salaryDate.second

                if (employeeSalary != null) {
                    var amountPaid: Long = 0
                    var noOfPayments: Long = 0
                    var noOfAbsents: Long = 0

                    val empSalary = employeeSalary.toLong()
                    val perDaySalary = empSalary.div(30)

                    employeeDao.getEmployeePaymentAmountsByDate(
                        employeeId,
                        firstDate,
                        secondDate,
                    ).flowOn(ioDispatcher).produceIn(this).receive().forEach { payment ->
                        amountPaid += payment.toLong()

                        noOfPayments += 1
                    }

                    employeeDao.getEmployeeAbsentDatesByDate(
                        employeeId,
                        firstDate,
                        secondDate,
                    ).flowOn(ioDispatcher).produceIn(this).receive().map {
                        noOfAbsents += 1
                    }

                    val absentSalary = perDaySalary.times(noOfAbsents)
                    val currentSalary = empSalary.minus(absentSalary)

                    val status = if (currentSalary >= amountPaid) NOT_PAID else PAID

                    val message: String? = if (currentSalary < amountPaid) {
                        "Paid Extra ${
                            amountPaid.minus(currentSalary).toString().toRupee
                        } Amount"
                    } else if (currentSalary > amountPaid) {
                        "Remaining  ${
                            currentSalary.minus(amountPaid).toString().toRupee
                        } have to pay."
                    } else {
                        null
                    }

                    val remainingAmount = currentSalary.minus(amountPaid)

                    send(
                        EmployeeSalaryEstimation(
                            startDate = firstDate,
                            endDate = secondDate,
                            status = status,
                            message = message,
                            remainingAmount = remainingAmount.toString(),
                            paymentCount = noOfPayments.toString(),
                            absentCount = noOfAbsents.toString(),
                        ),
                    )
                } else {
                    send(EmployeeSalaryEstimation())
                }
            } catch (e: Exception) {
                send(EmployeeSalaryEstimation())
            }
        }
    }

    override suspend fun getEmployeeAbsentDates(employeeId: Int): Flow<List<EmployeeAbsentDates>> {
        return channelFlow {
            try {
                val joinedDate = withContext(ioDispatcher) {
                    employeeDao.getEmployeeJoinedDate(employeeId)
                }
                val employeeAbsentDates = mutableListOf<EmployeeAbsentDates>()

                if (joinedDate != null) {
                    val dates = getSalaryDates(joinedDate)

                    dates.forEach { date ->
                        if (joinedDate <= date.first) {
                            val absentDates = employeeDao.getEmployeeAbsentDatesByDate(
                                employeeId,
                                date.first,
                                date.second,
                            ).flowOn(ioDispatcher).produceIn(this).receive()

                            employeeAbsentDates.add(
                                EmployeeAbsentDates(
                                    startDate = date.first,
                                    endDate = date.second,
                                    absentDates = absentDates,
                                ),
                            )
                        }
                    }

                    send(employeeAbsentDates)
                } else {
                    send(emptyList())
                }
            } catch (e: Exception) {
                send(emptyList())
            }
        }
    }

    override suspend fun getEmployeePayments(employeeId: Int): Flow<List<EmployeePayments>> {
        return channelFlow {
            try {
                val employeePayments = mutableListOf<EmployeePayments>()
                val joinedDate = withContext(ioDispatcher) {
                    employeeDao.getEmployeeJoinedDate(employeeId)
                }

                if (joinedDate != null) {
                    val dates = getSalaryDates(joinedDate)

                    dates.forEach { date ->
                        if (joinedDate <= date.first) {
                            val result = employeeDao.getEmployeePaymentsByDate(
                                employeeId,
                                date.first,
                                date.second,
                            ).flowOn(ioDispatcher).produceIn(this).receive()
                                .map { it.asExternalModel() }

                            employeePayments.add(
                                EmployeePayments(
                                    startDate = date.first,
                                    endDate = date.second,
                                    payments = result,
                                ),
                            )
                        }
                    }

                    send(employeePayments)
                } else {
                    send(emptyList())
                }
            } catch (e: Exception) {
                send(emptyList())
            }
        }
    }

    override suspend fun getSalaryCalculableDate(employeeId: Int): List<EmployeeMonthlyDate> {
        return try {
            val joinedDate = withContext(ioDispatcher) {
                employeeDao.getEmployeeJoinedDate(employeeId)
            }

            if (joinedDate != null) {
                val list = mutableListOf<EmployeeMonthlyDate>()

                val dates = getSalaryDates(joinedDate)

                dates.forEach { date ->
                    if (compareSalaryDates(joinedDate, date.first)) {
                        list.add(
                            EmployeeMonthlyDate(
                                startDate = date.first,
                                endDate = date.second,
                            ),
                        )
                    }
                }

                list
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun importEmployeesToDatabase(employees: List<Employee>): Resource<Boolean> {
        try {
            employees.forEach { newEmployee ->
                withContext(ioDispatcher) {
                    upsertEmployee(newEmployee)
                }
            }

            return Resource.Success(true)
        } catch (e: Exception) {
            return Resource.Error(e.message ?: "Error creating Employee Item")
        }
    }
}
