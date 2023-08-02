package com.niyaj.data.repository.validation

import com.niyaj.common.result.ValidationResult


interface ChargesValidationRepository {

    suspend fun validateChargesName(chargesName: String, chargesId: Int? = null): ValidationResult

    fun validateChargesPrice(chargesPrice: Int): ValidationResult
}