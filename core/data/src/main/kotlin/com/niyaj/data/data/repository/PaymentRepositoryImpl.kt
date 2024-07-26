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
import com.niyaj.data.repository.PaymentRepository
import com.niyaj.database.dao.PaymentDao
import com.niyaj.database.model.EmployeeWithPaymentCrossRef
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeWithPayments
import com.niyaj.model.Payment
import com.niyaj.model.searchEmployeeWithPayments
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentRepositoryImpl @Inject constructor(
    private val paymentDao: PaymentDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : PaymentRepository {

    override fun getAllEmployee(): Flow<List<Employee>> {
        return paymentDao.getAllEmployee().mapLatest { list ->
            list.map {
                it.asExternalModel()
            }
        }
    }

    override suspend fun getEmployeeById(employeeId: Int): Employee? {
        return withContext(ioDispatcher) {
            paymentDao.getEmployeeById(employeeId)?.asExternalModel()
        }
    }

    override suspend fun getAllEmployeePayments(searchText: String): Flow<List<EmployeeWithPayments>> {
        return withContext(ioDispatcher) {
            paymentDao.getAllEmployeePayment().mapLatest { list ->
                list.filter { it.payments.isNotEmpty() }
                    .map { it.asExternalModel() }
                    .searchEmployeeWithPayments(searchText)
            }
        }
    }

    override suspend fun getPaymentById(paymentId: Int): Resource<Payment?> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(paymentDao.getPaymentById(paymentId)?.asExternalModel())
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun upsertPayment(newPayment: Payment): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = withContext(ioDispatcher) {
                    paymentDao.upsertPayment(newPayment.toEntity())
                }

                if (result > 0) {
                    paymentDao.upsertEmployeeWithPaymentCrossReference(
                        EmployeeWithPaymentCrossRef(newPayment.employeeId, result.toInt()),
                    )
                }

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error("Unable to add or update employee payment")
        }
    }

    override suspend fun deletePayments(paymentIds: List<Int>): Resource<Boolean> {
        return try {
            val result = withContext(ioDispatcher) {
                paymentDao.deletePayments(paymentIds)
            }

            Resource.Success(result > 0)
        } catch (e: Exception) {
            Resource.Error("Unable to delete employee payments")
        }
    }

    override suspend fun importPaymentsToDatabase(payments: List<EmployeeWithPayments>): Resource<Boolean> {
        try {
            payments.forEach { empWithPayment ->
                val findEmployee = withContext(ioDispatcher) {
                    paymentDao.findEmployeeByName(
                        empWithPayment.employee.employeeName,
                        empWithPayment.employee.employeeId,
                    )
                }

                if (findEmployee != null) {
                    empWithPayment.payments.forEach { payment ->
                        upsertPayment(payment)
                    }
                } else {
                    val result = withContext(ioDispatcher) {
                        paymentDao.upsertEmployee(empWithPayment.employee.toEntity())
                    }

                    if (result > 0) {
                        empWithPayment.payments.forEach { payment ->
                            upsertPayment(payment)
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
