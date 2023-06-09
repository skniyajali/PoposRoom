package com.niyaj.poposroom.features.employee_payment.data.repository

import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.common.utils.ValidationResult
import com.niyaj.poposroom.features.employee.domain.model.Employee
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

    override fun getAllEmployee(): Flow<List<Employee>> = paymentDao.getAllEmployee()

    override suspend fun getEmployeeById(employeeId: Int): Employee? {
        return withContext(ioDispatcher) {
            paymentDao.getEmployeeById(employeeId)
        }
    }

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

    override suspend fun getPaymentById(paymentId: Int): Resource<Payment?>  {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(paymentDao.getPaymentById(paymentId))
            }
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun addOrIgnorePayment(newPayment: Payment): Resource<Boolean> {
        return try {
            val validateEmployee = validateEmployee(newPayment.employeeId)
            val validateGivenDate = validateGivenDate(newPayment.paymentDate)
            val validatePaymentType = validatePaymentType(newPayment.paymentType)
            val validateSalary = validateGivenAmount(newPayment.paymentAmount)
            val validateSalaryNote = validatePaymentNote(
                paymentNote = newPayment.paymentNote,
                isRequired = newPayment.paymentMode == PaymentMode.Both
            )
            val validatePaymentMode = validatePaymentMode(newPayment.paymentMode)

            val hasError = listOf(
                validateEmployee,
                validateSalary,
                validateSalaryNote,
                validatePaymentMode,
                validatePaymentType,
                validateGivenDate
            ).any { !it.successful }

            if (!hasError) {
                withContext(ioDispatcher) {
                    val result = withContext(ioDispatcher) {
                        paymentDao.insertOrIgnorePayment(newPayment)
                    }

                    if (result > 0) {
                        paymentDao.upsertEmployeeWithPaymentCrossReference(
                            EmployeeWithPaymentCrossRef(newPayment.employeeId, result.toInt())
                        )
                    }

                    Resource.Success(result > 0)
                }
            }else {
                Resource.Error("Unable to validate employee payment")
            }
        }catch (e: Exception){
            Resource.Error("Unable to add employee payment")
        }
    }

    override suspend fun updatePayment(newPayment: Payment): Resource<Boolean> {
        return try {
            val validateEmployee = validateEmployee(newPayment.employeeId)
            val validateGivenDate = validateGivenDate(newPayment.paymentDate)
            val validatePaymentType = validatePaymentType(newPayment.paymentType)
            val validateSalary = validateGivenAmount(newPayment.paymentAmount)
            val validateSalaryNote = validatePaymentNote(
                paymentNote = newPayment.paymentNote,
                isRequired = newPayment.paymentMode == PaymentMode.Both
            )
            val validatePaymentMode = validatePaymentMode(newPayment.paymentMode)

            val hasError = listOf(
                validateEmployee,
                validateSalary,
                validateSalaryNote,
                validatePaymentMode,
                validatePaymentType,
                validateGivenDate
            ).any { !it.successful }

            if (!hasError) {
                withContext(ioDispatcher) {
                    val result = withContext(ioDispatcher) {
                        paymentDao.updatePayment(newPayment)
                    }

                    Resource.Success(result > 0)
                }
            }else {
                Resource.Error("Unable to validate employee payment")
            }
        }catch (e: Exception){
            Resource.Error("Unable to update employee payment")
        }
    }

    override suspend fun upsertPayment(newPayment: Payment): Resource<Boolean> {
        return try {
            val validateEmployee = validateEmployee(newPayment.employeeId)
            val validateGivenDate = validateGivenDate(newPayment.paymentDate)
            val validatePaymentType = validatePaymentType(newPayment.paymentType)
            val validateSalary = validateGivenAmount(newPayment.paymentAmount)
            val validateSalaryNote = validatePaymentNote(
                paymentNote = newPayment.paymentNote,
                isRequired = newPayment.paymentMode == PaymentMode.Both
            )
            val validatePaymentMode = validatePaymentMode(newPayment.paymentMode)

            val hasError = listOf(
                validateEmployee,
                validateSalary,
                validateSalaryNote,
                validatePaymentMode,
                validatePaymentType,
                validateGivenDate
            ).any { !it.successful }

            if (!hasError) {
                withContext(ioDispatcher) {
                    val result = withContext(ioDispatcher) {
                        paymentDao.upsertPayment(newPayment)
                    }

                    if (result > 0) {
                        paymentDao.upsertEmployeeWithPaymentCrossReference(
                            EmployeeWithPaymentCrossRef(newPayment.employeeId, result.toInt())
                        )
                    }

                    Resource.Success(result > 0)
                }
            }else {
                Resource.Error("Unable to validate employee payment")
            }
        }catch (e: Exception){
            Resource.Error("Unable to add or update employee payment")
        }
    }

    override suspend fun deletePayment(paymentId: Int): Resource<Boolean> {
        return try {
            val result = withContext(ioDispatcher) {
                paymentDao.deletePayment(paymentId)
            }

            Resource.Success(result > 0)
        }catch (e: Exception) {
            Resource.Error("Unable to delete employee payment")
        }
    }

    override suspend fun deletePayments(paymentIds: List<Int>): Resource<Boolean> {
        return try {
            val result = withContext(ioDispatcher) {
                paymentDao.deletePayments(paymentIds)
            }

            Resource.Success(result > 0)
        }catch (e: Exception) {
            Resource.Error("Unable to delete employee payments")
        }
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