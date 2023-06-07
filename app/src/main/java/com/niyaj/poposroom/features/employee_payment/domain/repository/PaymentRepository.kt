package com.niyaj.poposroom.features.employee_payment.domain.repository

import com.niyaj.poposroom.features.employee.domain.model.Employee
import com.niyaj.poposroom.features.employee_payment.domain.model.CalculatedSalary
import com.niyaj.poposroom.features.employee_payment.domain.model.EmployeeWithPayment
import com.niyaj.poposroom.features.employee_payment.domain.model.Payment
import com.niyaj.poposroom.features.employee_payment.domain.model.SalaryCalculableDate
import com.niyaj.poposroom.features.employee_payment.domain.model.SalaryCalculation
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {

    fun getAllEmployee(): Flow<List<Employee>>

    suspend fun getAllEmployeePayments(searchText: String): Flow<List<EmployeeWithPayment>>

    suspend fun getAllPayment(searchText: String): Flow<List<Payment>>

    suspend fun getPaymentById(paymentId: Int): Payment?

    suspend fun addOrIgnorePayment(newPayment: Payment): Boolean

    suspend fun updatePayment(newPayment: Payment): Boolean

    suspend fun upsertPayment(newPayment: Payment): Boolean

    suspend fun deletePayment(paymentId: Int): Boolean

    suspend fun deletePayments(paymentId: List<Int>): Boolean

    suspend fun getPaymentByEmployeeId(employeeId: Int, selectedDate: Pair<String, String>): CalculatedSalary?

    suspend fun getEmployeePayment(employeeId: Int): Flow<List<SalaryCalculation>>

    suspend fun getPaymentCalculableDate(employeeId: Int): Flow<List<SalaryCalculableDate>>
}