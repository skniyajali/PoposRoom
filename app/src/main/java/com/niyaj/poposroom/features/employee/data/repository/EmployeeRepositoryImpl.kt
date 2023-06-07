package com.niyaj.poposroom.features.employee.data.repository

import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.common.utils.ValidationResult
import com.niyaj.poposroom.features.employee.data.dao.EmployeeDao
import com.niyaj.poposroom.features.employee.domain.model.Employee
import com.niyaj.poposroom.features.employee.domain.model.searchEmployee
import com.niyaj.poposroom.features.employee.domain.repository.EmployeeRepository
import com.niyaj.poposroom.features.employee.domain.repository.EmployeeValidationRepository
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class EmployeeRepositoryImpl(
    private val employeeDao: EmployeeDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher
) : EmployeeRepository, EmployeeValidationRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAllEmployee(searchText: String): Flow<List<Employee>> {
        return withContext(ioDispatcher) {
            employeeDao.getAllEmployee().mapLatest { it.searchEmployee(searchText) }
        }
    }

    override suspend fun getEmployeeById(employeeId: Int): Resource<Employee?> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(employeeDao.getEmployeeById(employeeId))
            }
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun addOrIgnoreEmployee(newEmployee: Employee): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateEmployeeName = validateEmployeeName(newEmployee.employeeName)
                val validateEmployeePhone = validateEmployeePhone(newEmployee.employeePhone)
                val validateEmployeePosition = validateEmployeePosition(newEmployee.employeePosition)
                val validateEmployeeSalary = validateEmployeeSalary(newEmployee.employeeSalary)

                val hasError = listOf(validateEmployeeName, validateEmployeePhone, validateEmployeePosition, validateEmployeeSalary).any { !it.successful }

                if (!hasError) {
                    withContext(ioDispatcher) {
                        val result = employeeDao.insertOrIgnoreEmployee(newEmployee)
                        Resource.Success(result > 0)
                    }
                }else {
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
                val validateEmployeePhone = validateEmployeePhone(newEmployee.employeePhone, newEmployee.employeeId)
                val validateEmployeePosition = validateEmployeePosition(newEmployee.employeePosition)
                val validateEmployeeSalary = validateEmployeeSalary(newEmployee.employeeSalary)

                val hasError = listOf(validateEmployeeName, validateEmployeePhone, validateEmployeePosition, validateEmployeeSalary).any { !it.successful }

                if (!hasError) {
                    withContext(ioDispatcher) {
                        val result = employeeDao.updateEmployee(newEmployee)
                        Resource.Success(result > 0)
                    }
                }else {
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
                val validateEmployeeName = validateEmployeeName(newEmployee.employeeName)
                val validateEmployeePhone = validateEmployeePhone(newEmployee.employeePhone, newEmployee.employeeId)
                val validateEmployeePosition = validateEmployeePosition(newEmployee.employeePosition)
                val validateEmployeeSalary = validateEmployeeSalary(newEmployee.employeeSalary)

                val hasError = listOf(validateEmployeeName, validateEmployeePhone, validateEmployeePosition, validateEmployeeSalary).any { !it.successful }

                if (!hasError) {
                    withContext(ioDispatcher) {
                        val result = employeeDao.upsertEmployee(newEmployee)
                        Resource.Success(result > 0)
                    }
                }else {
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
        }catch (e: Exception) {
            Resource.Error("Unable to delete Employee")
        }
    }

    override suspend fun deleteEmployees(employeeIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = employeeDao.deleteEmployee(employeeIds)

                Resource.Success(result > 0)
            }
        }catch (e: Exception) {
            Resource.Error("Unable to delete Employee")
        }
    }

    override suspend fun validateEmployeeName(name: String, employeeId: Int?): ValidationResult {
        if(name.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_NAME_EMPTY_ERROR,
            )
        }

        if(name.length < 4){
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_NAME_LENGTH_ERROR,
            )
        }

        if(name.any { it.isDigit() }){
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_NAME_DIGIT_ERROR
            )
        }

        val serverResult = withContext(ioDispatcher) {
            employeeDao.findEmployeeByName(name, employeeId) != null
        }

        if(serverResult){
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
        employeeId: Int?
    ): ValidationResult {
        if(phone.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_PHONE_EMPTY_ERROR
            )
        }

        if(phone.length != 10){
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_PHONE_LENGTH_ERROR
            )
        }

        if(phone.any { it.isLetter() }){
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_PHONE_LETTER_ERROR
            )
        }

        val serverResult = withContext(ioDispatcher) {
            employeeDao.findEmployeeByPhone(phone, employeeId) != null
        }

        if(serverResult){
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
        if (position.isEmpty()){
            return ValidationResult(false, EmployeeTestTags.EMPLOYEE_POSITION_EMPTY_ERROR)
        }

        return ValidationResult(true)
    }

    override fun validateEmployeeSalary(salary: String): ValidationResult {
        if (salary.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_SALARY_EMPTY_ERROR
            )
        }

        if(salary.length != 5){
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_SALARY_LENGTH_ERROR
            )
        }

        if(salary.any { it.isLetter() }){
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_SALARY_LETTER_ERROR
            )
        }

        return ValidationResult(
            successful = true,
        )
    }
}