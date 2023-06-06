package com.niyaj.poposroom.features.employee_payment.data.repository

import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.ValidationResult
import com.niyaj.poposroom.features.employee.domain.utils.PaymentMode
import com.niyaj.poposroom.features.employee.domain.utils.PaymentType
import com.niyaj.poposroom.features.employee_payment.data.dao.PaymentDao
import com.niyaj.poposroom.features.employee_payment.domain.model.CalculatedSalary
import com.niyaj.poposroom.features.employee_payment.domain.model.EmployeeWithPayment
import com.niyaj.poposroom.features.employee_payment.domain.model.EmployeeWithPaymentCrossRef
import com.niyaj.poposroom.features.employee_payment.domain.model.Payment
import com.niyaj.poposroom.features.employee_payment.domain.model.SalaryCalculableDate
import com.niyaj.poposroom.features.employee_payment.domain.model.SalaryCalculation
import com.niyaj.poposroom.features.employee_payment.domain.model.searchEmployeeWithPayments
import com.niyaj.poposroom.features.employee_payment.domain.model.searchPayment
import com.niyaj.poposroom.features.employee_payment.domain.repository.PaymentRepository
import com.niyaj.poposroom.features.employee_payment.domain.repository.PaymentValidationRepository
import com.niyaj.poposroom.features.employee_payment.domain.utils.PaymentScreenTags
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentRepositoryImpl(
    private val paymentDao: PaymentDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher
) : PaymentRepository, PaymentValidationRepository {

    override suspend fun getAllEmployeePayments(searchText: String): Flow<List<EmployeeWithPayment>> {
        return withContext(ioDispatcher) {
            paymentDao.getAllEmployeePayment().mapLatest {
                it.searchEmployeeWithPayments(searchText)
            }
        }
    }

    override suspend fun getAllPayment(searchText: String): Flow<List<Payment>> {
        return withContext(ioDispatcher) {
            paymentDao.getAllPayment().mapLatest { it.searchPayment(searchText) }
        }
    }

    override suspend fun getPaymentById(paymentId: Int): Payment?  {
        return withContext(ioDispatcher) {
            paymentDao.getPaymentById(paymentId)
        }
    }

    override suspend fun addOrIgnorePayment(newPayment: Payment): Boolean {
        val result = withContext(ioDispatcher) {
            paymentDao.insertOrIgnorePayment(newPayment)
        }

        return result > 0
    }

    override suspend fun updatePayment(newPayment: Payment): Boolean {
        val result = withContext(ioDispatcher) {
            paymentDao.updatePayment(newPayment)
        }

        return result > 0
    }

    override suspend fun upsertPayment(newPayment: Payment): Boolean {
        val result = withContext(ioDispatcher) {
            paymentDao.upsertPayment(newPayment)
        }

        if (result > 0) {
            paymentDao.upsertEmployeeWithPaymentCrossReference(
                EmployeeWithPaymentCrossRef(newPayment.employeeId, result.toInt())
            )
        }

        return result > 0
    }

    override suspend fun deletePayment(paymentId: Int): Boolean {
        val result = withContext(ioDispatcher) {
            paymentDao.deletePayment(paymentId)
        }

        return result > 0
    }

    override suspend fun deletePayments(paymentId: List<Int>): Boolean {
        val result = withContext(ioDispatcher) {
            paymentDao.deletePayments(paymentId)
        }

        return result > 0
    }

    override suspend fun getPaymentByEmployeeId(
        employeeId: Int,
        selectedDate: Pair<String, String>
    ): CalculatedSalary? {
        return null
    }

    override suspend fun getEmployeePayment(
        employeeId: Int
    ): Flow<List<SalaryCalculation>> {
        return flow {  }
    }

    override suspend fun getPaymentCalculableDate(
        employeeId: Int
    ): Flow<List<SalaryCalculableDate>> {
        return flow {  }
    }

    override fun validateEmployee(employeeId: Int): ValidationResult {
        if (employeeId == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = PaymentScreenTags.PAYMENT_EMPLOYEE_NAME_EMPTY,
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override fun validateGivenDate(givenDate: String): ValidationResult {
        if (givenDate.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = PaymentScreenTags.PAYMENT_GIVEN_DATE_EMPTY
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override fun validatePaymentMode(paymentMode: PaymentMode): ValidationResult {
        if (paymentMode.name.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = PaymentScreenTags.PAYMENT_MODE_EMPTY
            )
        }

        return ValidationResult(true)
    }

    override fun validateGivenAmount(salary: String): ValidationResult {
        if (salary.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = PaymentScreenTags.GIVEN_AMOUNT_EMPTY,
            )
        }

        if (salary.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = PaymentScreenTags.GIVEN_AMOUNT_LENGTH_ERROR,
            )
        }

        if (salary.any { it.isLetter() }) {
            return ValidationResult(
                successful = false,
                errorMessage = PaymentScreenTags.GIVEN_AMOUNT_LETTER_ERROR,
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override fun validatePaymentNote(paymentNote: String, isRequired: Boolean): ValidationResult {
        if (isRequired) {
            if (paymentNote.isEmpty()){
                return ValidationResult(
                    successful = false,
                    errorMessage = PaymentScreenTags.PAYMENT_NOTE_EMPTY
                )
            }
        }

        return ValidationResult(true)
    }

    override fun validatePaymentType(paymentType: PaymentType): ValidationResult {
        if (paymentType.name.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = PaymentScreenTags.PAYMENT_TYPE_EMPTY,
            )
        }

        return ValidationResult(true)
    }
}