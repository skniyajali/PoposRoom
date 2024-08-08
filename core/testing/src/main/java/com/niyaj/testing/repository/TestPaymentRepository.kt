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
import com.niyaj.data.repository.PaymentRepository
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeWithPayments
import com.niyaj.model.Payment
import com.niyaj.model.searchEmployeeWithPayments
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import org.jetbrains.annotations.TestOnly

class TestPaymentRepository: PaymentRepository {

    /**
     * The backing employee, payment, employee with payment list for testing
     */
    private val employeeList = MutableStateFlow(mutableListOf<Employee>())
    private val paymentList = MutableStateFlow(mutableListOf<Payment>())
    private val employeePayments = MutableStateFlow(mutableListOf<EmployeeWithPayments>())

    override fun getAllEmployee(): Flow<List<Employee>> = employeeList

    override suspend fun getEmployeeById(employeeId: Int): Employee? {
        return employeeList.value.find { it.employeeId == employeeId }
    }

    override suspend fun getAllEmployeePayments(searchText: String): Flow<List<EmployeeWithPayments>> {
        return employeePayments.mapLatest { it.searchEmployeeWithPayments(searchText) }
    }

    override suspend fun getPaymentById(paymentId: Int): Resource<Payment?> {
        return Resource.Success(paymentList.value.find { it.paymentId == paymentId })
    }

    override suspend fun upsertPayment(newPayment: Payment): Resource<Boolean> {
        val result = paymentList.value.find { it.paymentId == newPayment.paymentId }

        return Resource.Success(
            if (result == null) {
                paymentList.value.add(newPayment)
            } else {
                paymentList.value.remove(result)
                paymentList.value.add(newPayment)
            },
        )
    }

    override suspend fun deletePayments(paymentIds: List<Int>): Resource<Boolean> {
        return Resource.Success(paymentList.value.removeAll { it.paymentId in paymentIds })
    }

    override suspend fun importPaymentsToDatabase(payments: List<EmployeeWithPayments>): Resource<Boolean> {
        employeePayments.update { payments.toMutableList() }

        return Resource.Success(true)
    }

    @TestOnly
    fun updateEmployeePayments(payments: List<EmployeeWithPayments>) {
        employeePayments.update { payments.toMutableList() }
    }

    @TestOnly
    fun updateEmployeeData(employees: List<Employee>) {
        employeeList.update { employees.toMutableList() }
    }

    @TestOnly
    fun updatePaymentData(payments: List<Payment>) {
        paymentList.update { payments.toMutableList() }
    }
}