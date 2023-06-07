package com.niyaj.poposroom.features.charges.domain.repository

import com.niyaj.poposroom.features.common.utils.ValidationResult


interface ChargesValidationRepository {

    suspend fun validateChargesName(chargesName: String, chargesId: Int? = null): ValidationResult

    fun validateChargesPrice(doesApplicable: Boolean, chargesPrice: Int): ValidationResult
}