package com.niyaj.data.repository.validation

import com.niyaj.common.result.ValidationResult

interface MeasureUnitValidationRepository {

    suspend fun validateUnitName(unitName: String, unitId: Int? = 0): ValidationResult

    fun validateUnitValue(unitValue: String): ValidationResult
}