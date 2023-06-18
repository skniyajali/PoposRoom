package com.niyaj.poposroom.features.cart_order.domain.repository

import com.niyaj.poposroom.features.common.utils.ValidationResult

interface CartOrderValidationRepository {

    suspend fun validateCustomerPhone(customerPhone: String): ValidationResult

    suspend fun validateAddressName(addressName:String): ValidationResult

    fun validateAddressShortName(addressShortName: String): ValidationResult
}