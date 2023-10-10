package com.niyaj.data.repository.validation

import com.niyaj.common.result.ValidationResult

interface MarketItemValidationRepository {

    fun validateItemType(itemType: String): ValidationResult

    suspend fun validateItemName(itemName: String, itemId: Int? = null): ValidationResult

    fun validateItemMeasureUnit(itemMeasureUnit: String): ValidationResult
    
    fun validateItemPrice(itemPrice: String? = null): ValidationResult
}