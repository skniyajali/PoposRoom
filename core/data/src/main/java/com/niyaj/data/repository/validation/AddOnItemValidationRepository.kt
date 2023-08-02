package com.niyaj.data.repository.validation

import com.niyaj.common.result.ValidationResult

interface AddOnItemValidationRepository {

    suspend fun validateItemName(name: String, addOnItemId: Int? = null): ValidationResult

    fun validateItemPrice(price: Int): ValidationResult
}