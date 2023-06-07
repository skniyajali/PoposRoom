package com.niyaj.poposroom.features.address.domain.repository

import com.niyaj.poposroom.features.common.utils.ValidationResult

interface AddressValidationRepository {

    suspend fun validateAddressName(addressName: String, addressId: Int? = null): ValidationResult

    fun validateAddressShortName(addressShortName: String): ValidationResult
}