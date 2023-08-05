package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.EmployeeSalaryEstimation
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeWithPayments
import com.niyaj.model.Payment
import com.niyaj.model.EmployeeMonthlyDate
import com.niyaj.model.EmployeePayments
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {

    fun getAllEmployee(): Flow<List<Employee>>

    suspend fun getEmployeeById(employeeId: Int) : Employee?

    suspend fun getAllEmployeePayments(searchText: String): Flow<List<EmployeeWithPayments>>

    suspend fun getAllPayment(searchText: String): Flow<List<Payment>>

    suspend fun getPaymentById(paymentId: Int): Resource<Payment?>

    suspend fun addOrIgnorePayment(newPayment: Payment): Resource<Boolean>

    suspend fun updatePayment(newPayment: Payment): Resource<Boolean>

    suspend fun upsertPayment(newPayment: Payment): Resource<Boolean>

    suspend fun deletePayment(paymentId: Int): Resource<Boolean>

    suspend fun deletePayments(paymentIds: List<Int>): Resource<Boolean>

    suspend fun getPaymentByEmployeeId(employeeId: Int, selectedDate: Pair<String, String>): EmployeeSalaryEstimation?

    suspend fun getEmployeePayment(employeeId: Int): Flow<List<EmployeePayments>>

    suspend fun getPaymentCalculableDate(employeeId: Int): Flow<List<EmployeeMonthlyDate>>
}