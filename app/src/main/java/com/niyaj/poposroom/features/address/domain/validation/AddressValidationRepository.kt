package com.niyaj.poposroom.features.address.domain.validation

import com.niyaj.poposroom.features.common.utils.ValidationResult

interface AddressValidationRepository {

    suspend fun validateAddressName(addressId: Int? = null, addressName: String): ValidationResult

    fun validateAddressShortName(addressShortName: String): ValidationResult
}