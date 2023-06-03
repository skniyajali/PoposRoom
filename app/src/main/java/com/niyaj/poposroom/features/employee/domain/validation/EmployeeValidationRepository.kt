package com.niyaj.poposroom.features.employee.domain.validation

import com.niyaj.poposroom.features.common.utils.ValidationResult


interface EmployeeValidationRepository {

    suspend fun validateEmployeeName(name: String, employeeId: Int? = null): ValidationResult

    suspend fun validateEmployeePhone(phone: String, employeeId: Int? = null): ValidationResult

    fun validateEmployeePosition(position: String): ValidationResult

    fun validateEmployeeSalary(salary: String): ValidationResult
}