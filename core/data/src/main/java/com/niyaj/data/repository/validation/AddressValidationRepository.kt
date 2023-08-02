package com.niyaj.data.repository.validation

import com.niyaj.common.result.ValidationResult

interface AddressValidationRepository {

    suspend fun validateAddressName(addressName: String, addressId: Int? = null): ValidationResult

    fun validateAddressShortName(addressShortName: String): ValidationResult
}