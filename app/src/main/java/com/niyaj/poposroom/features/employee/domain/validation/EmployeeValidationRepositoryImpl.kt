package com.niyaj.poposroom.features.employee.domain.validation

import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.ValidationResult
import com.niyaj.poposroom.features.employee.dao.EmployeeDao
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_NAME_ALREADY_EXIST_ERROR
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_NAME_DIGIT_ERROR
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_NAME_EMPTY_ERROR
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_NAME_LENGTH_ERROR
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_PHONE_ALREADY_EXIST_ERROR
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_PHONE_EMPTY_ERROR
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_PHONE_LENGTH_ERROR
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_PHONE_LETTER_ERROR
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_POSITION_EMPTY_ERROR
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_SALARY_EMPTY_ERROR
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_SALARY_LENGTH_ERROR
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_SALARY_LETTER_ERROR
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EmployeeValidationRepositoryImpl @Inject constructor(
    private val employeeDao: EmployeeDao,
    @Dispatcher(PoposDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : EmployeeValidationRepository {

    override suspend fun validateEmployeeName(name: String, employeeId: Int?): ValidationResult {
        if(name.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = EMPLOYEE_NAME_EMPTY_ERROR,
            )
        }

        if(name.length < 4){
            return ValidationResult(
                successful = false,
                errorMessage = EMPLOYEE_NAME_LENGTH_ERROR,
            )
        }

        if(name.any { it.isDigit() }){
            return ValidationResult(
                successful = false,
                errorMessage = EMPLOYEE_NAME_DIGIT_ERROR
            )
        }

        val serverResult = withContext(ioDispatcher) {
            employeeDao.findEmployeeByName(name, employeeId) != null
        }

        if(serverResult){
            return ValidationResult(
                successful = false,
                errorMessage = EMPLOYEE_NAME_ALREADY_EXIST_ERROR,
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override suspend fun validateEmployeePhone(
        phone: String,
        employeeId: Int?
    ): ValidationResult {
        if(phone.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = EMPLOYEE_PHONE_EMPTY_ERROR
            )
        }

        if(phone.length != 10){
            return ValidationResult(
                successful = false,
                errorMessage = EMPLOYEE_PHONE_LENGTH_ERROR
            )
        }

        if(phone.any { it.isLetter() }){
            return ValidationResult(
                successful = false,
                errorMessage = EMPLOYEE_PHONE_LETTER_ERROR
            )
        }

        val serverResult = withContext(ioDispatcher) {
            employeeDao.findEmployeeByPhone(phone, employeeId) != null
        }

        if(serverResult){
            return ValidationResult(
                successful = false,
                errorMessage = EMPLOYEE_PHONE_ALREADY_EXIST_ERROR
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override fun validateEmployeePosition(position: String): ValidationResult {
        if (position.isEmpty()){
            return ValidationResult(false, EMPLOYEE_POSITION_EMPTY_ERROR)
        }

        return ValidationResult(true)
    }

    override fun validateEmployeeSalary(salary: String): ValidationResult {
        if (salary.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = EMPLOYEE_SALARY_EMPTY_ERROR
            )
        }

        if(salary.length != 5){
            return ValidationResult(
                successful = false,
                errorMessage = EMPLOYEE_SALARY_LENGTH_ERROR
            )
        }

        if(salary.any { it.isLetter() }){
            return ValidationResult(
                successful = false,
                errorMessage = EMPLOYEE_SALARY_LETTER_ERROR
            )
        }

        return ValidationResult(
            successful = true,
        )
    }
}