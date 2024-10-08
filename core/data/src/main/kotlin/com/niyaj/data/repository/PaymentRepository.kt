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
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeWithPayments
import com.niyaj.model.Payment
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {

    fun getAllEmployee(): Flow<List<Employee>>

    suspend fun getEmployeeById(employeeId: Int): Employee?

    suspend fun getAllEmployeePayments(searchText: String): Flow<List<EmployeeWithPayments>>

    suspend fun getPaymentById(paymentId: Int): Resource<Payment?>

    suspend fun upsertPayment(newPayment: Payment): Resource<Boolean>

    suspend fun deletePayments(paymentIds: List<Int>): Resource<Boolean>

    suspend fun importPaymentsToDatabase(payments: List<EmployeeWithPayments>): Resource<Boolean>
}
