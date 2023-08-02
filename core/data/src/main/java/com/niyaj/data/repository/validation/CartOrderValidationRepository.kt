package com.niyaj.data.repository.validation

import com.niyaj.common.result.ValidationResult

interface CartOrderValidationRepository {

    suspend fun validateCustomerPhone(customerPhone: String): ValidationResult

    suspend fun validateAddressName(addressName:String): ValidationResult

    fun validateAddressShortName(addressShortName: String): ValidationResult
}