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