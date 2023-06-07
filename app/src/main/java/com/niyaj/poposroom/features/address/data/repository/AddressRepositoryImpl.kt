package com.niyaj.poposroom.features.address.data.repository

import com.niyaj.poposroom.features.address.data.dao.AddressDao
import com.niyaj.poposroom.features.address.domain.model.Address
import com.niyaj.poposroom.features.address.domain.model.searchAddress
import com.niyaj.poposroom.features.address.domain.repository.AddressRepository
import com.niyaj.poposroom.features.address.domain.repository.AddressValidationRepository
import com.niyaj.poposroom.features.address.domain.utils.AddressTestTags
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.common.utils.ValidationResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class AddressRepositoryImpl(
    private val addressDao: AddressDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher
) : AddressRepository, AddressValidationRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAllAddress(searchText: String): Flow<List<Address>> {
        return withContext(ioDispatcher) {
            addressDao.getAllAddresses().mapLatest { it.searchAddress(searchText) }
        }
    }

    override suspend fun getAddressById(addressId: Int): Resource<Address?> {
        return try {
            withContext(ioDispatcher){
                Resource.Success(addressDao.getAddressById(addressId))
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to new address")
        }
    }

    override suspend fun addOrIgnoreAddress(newAddress: Address): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                val validateAddressName = validateAddressName(newAddress.addressName, newAddress.addressId)
                val validateAddressShortName = validateAddressShortName(newAddress.shortName)

                val hasError = listOf(validateAddressName, validateAddressShortName).any { !it.successful}

                if (!hasError) {
                    val result = addressDao.insertOrIgnoreAddress(newAddress)

                    Resource.Success(result > 0)
                }else {
                    Resource.Error("Unable to create address")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to new address")
        }
    }

    override suspend fun updateAddress(newAddress: Address): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                val validateAddressName = validateAddressName(newAddress.addressName, newAddress.addressId)
                val validateAddressShortName = validateAddressShortName(newAddress.shortName)

                val hasError = listOf(validateAddressName, validateAddressShortName).any { !it.successful}

                if (!hasError) {
                    val result = addressDao.updateAddress(newAddress)

                    Resource.Success(result > 0)
                }else {
                    Resource.Error("Unable to update address")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update address")
        }
    }

    override suspend fun upsertAddress(newAddress: Address): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                val validateAddressName = validateAddressName(newAddress.addressName, newAddress.addressId)
                val validateAddressShortName = validateAddressShortName(newAddress.shortName)

                val hasError = listOf(validateAddressName, validateAddressShortName).any { !it.successful}

                if (!hasError) {
                    val result = addressDao.upsertAddress(newAddress)

                    Resource.Success(result > 0)
                }else {
                    Resource.Error("Unable to create or update address")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to new address")
        }
    }

    override suspend fun deleteAddress(addressId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                val result = addressDao.deleteAddress(addressId)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to new address")
        }
    }

    override suspend fun deleteAddresses(addressIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                val result = addressDao.deleteAddresses(addressIds)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to new address")
        }
    }

    override suspend fun validateAddressName(addressName: String, addressId: Int?): ValidationResult {
        if(addressName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = AddressTestTags.ADDRESS_NAME_EMPTY_ERROR,
            )
        }

        if(addressName.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = AddressTestTags.ADDRESS_NAME_LENGTH_ERROR
            )
        }

        val serverResult = withContext(ioDispatcher) {
            addressDao.findAddressByName(addressName, addressId) != null
        }

        if (serverResult) {
            return ValidationResult(
                successful = false,
                errorMessage = AddressTestTags.ADDRESS_NAME_ALREADY_EXIST_ERROR
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
                errorMessage = AddressTestTags.ADDRESS_SHORT_NAME_EMPTY_ERROR
            )
        }

        if(addressShortName.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = AddressTestTags.ADDRESS_PRICE_LESS_THAN_TWO_ERROR
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}