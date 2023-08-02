package com.niyaj.data.repository.validation

import com.niyaj.common.result.ValidationResult

interface AbsentValidationRepository {

    suspend fun validateAbsentDate(absentDate: String, employeeId: Int? = null, absentId: Int? = null): ValidationResult

    fun validateAbsentEmployee(employeeId: Int): ValidationResult
}