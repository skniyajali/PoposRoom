package com.niyaj.poposroom.features.address.domain.validation

import com.niyaj.poposroom.features.address.dao.AddressDao
import com.niyaj.poposroom.features.address.domain.utils.AddressTestTags.ADDRESS_NAME_ALREADY_EXIST_ERROR
import com.niyaj.poposroom.features.address.domain.utils.AddressTestTags.ADDRESS_NAME_EMPTY_ERROR
import com.niyaj.poposroom.features.address.domain.utils.AddressTestTags.ADDRESS_NAME_LENGTH_ERROR
import com.niyaj.poposroom.features.address.domain.utils.AddressTestTags.ADDRESS_PRICE_LESS_THAN_TWO_ERROR
import com.niyaj.poposroom.features.address.domain.utils.AddressTestTags.ADDRESS_SHORT_NAME_EMPTY_ERROR
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.ValidationResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddressValidationRepositoryImpl @Inject constructor(
    private val addressDao: AddressDao,
    @Dispatcher(PoposDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : AddressValidationRepository {
    override suspend fun validateAddressName(addressId: Int?, addressName: String): ValidationResult {
        if(addressName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = ADDRESS_NAME_EMPTY_ERROR,
            )
        }

        if(addressName.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = ADDRESS_NAME_LENGTH_ERROR
            )
        }

        val serverResult = withContext(ioDispatcher) {
            addressDao.findAddressByName(addressId, addressName) != null
        }

        if (serverResult) {
            return ValidationResult(
                successful = false,
                errorMessage = ADDRESS_NAME_ALREADY_EXIST_ERROR
            )
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validateAddressShortName(addressShortName: String): ValidationResult {
        if(addressShortName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = ADDRESS_SHORT_NAME_EMPTY_ERROR
            )
        }

        if(addressShortName.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = ADDRESS_PRICE_LESS_THAN_TWO_ERROR
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}