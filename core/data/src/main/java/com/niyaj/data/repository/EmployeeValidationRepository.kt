package com.niyaj.data.repository

import com.niyaj.common.result.ValidationResult


interface EmployeeValidationRepository {

    suspend fun validateEmployeeName(name: String, employeeId: Int? = null): ValidationResult

    suspend fun validateEmployeePhone(phone: String, employeeId: Int? = null): ValidationResult

    fun validateEmployeePosition(position: String): ValidationResult

    fun validateEmployeeSalary(salary: String): ValidationResult
}