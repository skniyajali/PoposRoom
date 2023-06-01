package com.niyaj.poposroom.features.addon_item.domain.validation

import com.niyaj.poposroom.features.common.utils.ValidationResult

interface AddOnItemValidationRepository {

    suspend fun validateItemName(name: String, addOnItemId: Int? = null): ValidationResult

    fun validateItemPrice(price: Int): ValidationResult
}