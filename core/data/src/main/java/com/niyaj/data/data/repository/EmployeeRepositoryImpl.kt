package com.niyaj.data.data.repository

import android.util.Log
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.EmployeeTestTags
import com.niyaj.common.utils.Constants.NOT_PAID
import com.niyaj.common.utils.Constants.PAID
import com.niyaj.common.utils.compareSalaryDates
import com.niyaj.common.utils.getSalaryDates
import com.niyaj.common.utils.toRupee
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.EmployeeRepository
import com.niyaj.data.repository.EmployeeValidationRepository
import com.niyaj.database.dao.EmployeeDao
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Absent
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeAbsentDates
import com.niyaj.model.EmployeeMonthlyDate
import com.niyaj.model.EmployeePayments
import com.niyaj.model.EmployeeSalaryEstimation
import com.niyaj.model.Payment
import com.niyaj.model.searchEmployee
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.withContext

class EmployeeRepositoryImpl(
    private val employeeDao: EmployeeDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : EmployeeRepository, EmployeeValidationRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAllEmployee(searchText: String): Flow<List<Employee>> {
        return withContext(ioDispatcher) {
            employeeDao.getAllEmployee().mapLatest { it ->
                it.map {
                    it.asExternalModel()
                }.searchEmployee(searchText)
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

    override suspend fun addOrIgnoreEmployee(newEmployee: Employee): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateEmployeeName = validateEmployeeName(newEmployee.employeeName)
                val validateEmployeePhone = validateEmployeePhone(newEmployee.employeePhone)
                val validateEmployeePosition =
                    validateEmployeePosition(newEmployee.employeePosition)
                val validateEmployeeSalary = validateEmployeeSalary(newEmployee.employeeSalary)

                val hasError = listOf(
                    validateEmployeeName,
                    validateEmployeePhone,
                    validateEmployeePosition,
                    validateEmployeeSalary
                ).any { !it.successful }

                if (!hasError) {
                    withContext(ioDispatcher) {
                        val result = employeeDao.insertOrIgnoreEmployee(newEmployee.toEntity())
                        Resource.Success(result > 0)
                    }
                } else {
                    Resource.Error("Unable to validate employee")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error creating Employee Item")
        }
    }

    override suspend fun updateEmployee(newEmployee: Employee): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateEmployeeName = validateEmployeeName(newEmployee.employeeName)
                val validateEmployeePhone =
                    validateEmployeePhone(newEmployee.employeePhone, newEmployee.employeeId)
                val validateEmployeePosition =
                    validateEmployeePosition(newEmployee.employeePosition)
                val validateEmployeeSalary = validateEmployeeSalary(newEmployee.employeeSalary)

                val hasError = listOf(
                    validateEmployeeName,
                    validateEmployeePhone,
                    validateEmployeePosition,
                    validateEmployeeSalary
                ).any { !it.successful }

                if (!hasError) {
                    withContext(ioDispatcher) {
                        val result = employeeDao.updateEmployee(newEmployee.toEntity())
                        Resource.Success(result > 0)
                    }
                } else {
                    Resource.Error("Unable to validate employee")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error updating employee")
        }
    }

    override suspend fun upsertEmployee(newEmployee: Employee): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateEmployeeName = validateEmployeeName(newEmployee.employeeName, newEmployee.employeeId)
                val validateEmployeePhone =
                    validateEmployeePhone(newEmployee.employeePhone, newEmployee.employeeId)
                val validateEmployeePosition =
                    validateEmployeePosition(newEmployee.employeePosition)
                val validateEmployeeSalary = validateEmployeeSalary(newEmployee.employeeSalary)

                val hasError = listOf(
                    validateEmployeeName,
                    validateEmployeePhone,
                    validateEmployeePosition,
                    validateEmployeeSalary
                ).any { !it.successful }

                if (!hasError) {
                    withContext(ioDispatcher) {
                        val result = employeeDao.upsertEmployee(newEmployee.toEntity())
                        Resource.Success(result > 0)
                    }
                } else {
                    Resource.Error("Unable to validate employee")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error creating Employee Item")
        }
    }

    override suspend fun deleteEmployee(employeeId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = employeeDao.deleteEmployee(employeeId)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error("Unable to delete Employee")
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

    override suspend fun validateEmployeeName(name: String, employeeId: Int?): ValidationResult {
        if (name.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_NAME_EMPTY_ERROR,
            )
        }

        if (name.length < 4) {
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_NAME_LENGTH_ERROR,
            )
        }

        if (name.any { it.isDigit() }) {
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_NAME_DIGIT_ERROR
            )
        }

        val serverResult = withContext(ioDispatcher) {
            employeeDao.findEmployeeByName(name, employeeId) != null
        }

        if (serverResult) {
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_NAME_ALREADY_EXIST_ERROR,
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override suspend fun validateEmployeePhone(
        phone: String,
        employeeId: Int?,
    ): ValidationResult {
        if (phone.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_PHONE_EMPTY_ERROR
            )
        }

        if (phone.length != 10) {
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_PHONE_LENGTH_ERROR
            )
        }

        if (phone.any { it.isLetter() }) {
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_PHONE_LETTER_ERROR
            )
        }

        val serverResult = withContext(ioDispatcher) {
            employeeDao.findEmployeeByPhone(phone, employeeId) != null
        }

        if (serverResult) {
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_PHONE_ALREADY_EXIST_ERROR
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override fun validateEmployeePosition(position: String): ValidationResult {
        if (position.isEmpty()) {
            return ValidationResult(false, EmployeeTestTags.EMPLOYEE_POSITION_EMPTY_ERROR)
        }

        return ValidationResult(true)
    }

    override fun validateEmployeeSalary(salary: String): ValidationResult {
        if (salary.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_SALARY_EMPTY_ERROR
            )
        }

        if (salary.length != 5) {
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_SALARY_LENGTH_ERROR
            )
        }

        if (salary.any { it.isLetter() }) {
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_SALARY_LETTER_ERROR
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override suspend fun getEmployeePaymentById(employeeId: Int): Resource<Payment?> {
        return try {
            val result = employeeDao.getEmployeePaymentById(employeeId)?.asExternalModel()

            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun getEmployeeAbsentById(employeeId: Int): Resource<Absent?> {
        return try {
            val result = employeeDao.getEmployeeAbsentById(employeeId)?.asExternalModel()

            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun findEmployeeAttendanceByAbsentDate(
        absentDate: String,
        employeeId: Int,
        absentId: Int?,
    ): Boolean {
        return try {
            withContext(ioDispatcher) {
                val result =
                    employeeDao.findEmployeeAbsentDateByIdAndDate(absentDate, employeeId, absentId)

                result != null
            }
        } catch (e: Exception) {
            false
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
                        secondDate
                    ).flowOn(ioDispatcher).produceIn(this).receive().forEach { payment ->
                        amountPaid += payment.toLong()

                        noOfPayments += 1
                    }

                    employeeDao.getEmployeeAbsentDatesByDate(
                        employeeId,
                        firstDate,
                        secondDate
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
                    } else null

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
                        )
                    )
                } else {
                    Log.e("EmployeeEstimation", "Unable to find employee")

                    send(EmployeeSalaryEstimation())
                }
            } catch (e: Exception) {
                Log.e("EmployeeEstimation", e.message ?: "Error")

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
                                date.second
                            ).flowOn(ioDispatcher).produceIn(this).receive()

                            employeeAbsentDates.add(
                                EmployeeAbsentDates(
                                    startDate = date.first,
                                    endDate = date.second,
                                    absentDates = absentDates
                                )
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
                                date.second
                            ).flowOn(ioDispatcher).produceIn(this).receive()
                                .map { it.asExternalModel() }

                            employeePayments.add(
                                EmployeePayments(
                                    startDate = date.first,
                                    endDate = date.second,
                                    payments = result
                                )
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
                                endDate = date.second
                            )
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
                return withContext(ioDispatcher) {
                    upsertEmployee(newEmployee)
                }
            }

            return Resource.Success(true)
        } catch (e: Exception) {
            return Resource.Error(e.message ?: "Error creating Employee Item")
        }
    }
}