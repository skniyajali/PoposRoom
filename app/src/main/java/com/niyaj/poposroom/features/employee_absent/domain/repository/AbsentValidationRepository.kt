package com.niyaj.poposroom.features.employee_absent.domain.repository

import com.niyaj.poposroom.features.common.utils.ValidationResult

interface AbsentValidationRepository {

    suspend fun validateAbsentDate(absentDate: String, employeeId: Int? = null, absentId: Int? = null): ValidationResult

    fun validateAbsentEmployee(employeeId: Int): ValidationResult
}