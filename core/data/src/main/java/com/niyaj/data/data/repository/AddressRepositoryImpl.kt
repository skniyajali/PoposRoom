package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.result.ValidationResult
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.AddressRepository
import com.niyaj.data.repository.validation.AddressValidationRepository
import com.niyaj.data.utils.AddressTestTags
import com.niyaj.database.dao.AddressDao
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Address
import com.niyaj.model.AddressWiseOrder
import com.niyaj.model.searchAddress
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class AddressRepositoryImpl(
    private val addressDao: AddressDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : AddressRepository, AddressValidationRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAllAddress(searchText: String): Flow<List<Address>> {
        return withContext(ioDispatcher) {
            addressDao.getAllAddresses()
                .mapLatest { list -> list.map { it.asExternalModel() }.searchAddress(searchText) }
        }
    }

    override suspend fun getAddressById(addressId: Int): Resource<Address?> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(addressDao.getAddressById(addressId)?.asExternalModel())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to new address")
        }
    }

    override suspend fun addOrIgnoreAddress(newAddress: Address): Int {
        return try {
            withContext(ioDispatcher) {
                val validateAddressName = validateAddressName(newAddress.addressName)
                val validateAddressShortName = validateAddressShortName(newAddress.shortName)

                val hasError =
                    listOf(validateAddressName, validateAddressShortName).any { !it.successful }

                if (!hasError) {
                    addressDao.insertOrIgnoreAddress(newAddress.toEntity()).toInt()
                } else {
                    0
                }
            }
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun updateAddress(newAddress: Address): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateAddressName =
                    validateAddressName(newAddress.addressName, newAddress.addressId)
                val validateAddressShortName = validateAddressShortName(newAddress.shortName)

                val hasError =
                    listOf(validateAddressName, validateAddressShortName).any { !it.successful }

                if (!hasError) {
                    val result = addressDao.updateAddress(newAddress.toEntity())

                    Resource.Success(result > 0)
                } else {
                    Resource.Error("Unable to update address")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update address")
        }
    }

    override suspend fun upsertAddress(newAddress: Address): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateAddressName =
                    validateAddressName(newAddress.addressName, newAddress.addressId)
                val validateAddressShortName = validateAddressShortName(newAddress.shortName)

                val hasError =
                    listOf(validateAddressName, validateAddressShortName).any { !it.successful }

                if (!hasError) {
                    val result = addressDao.upsertAddress(newAddress.toEntity())

                    Resource.Success(result > 0)
                } else {
                    Resource.Error("Unable to create or update address")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to new address")
        }
    }

    override suspend fun deleteAddress(addressId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = addressDao.deleteAddress(addressId)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to new address")
        }
    }

    override suspend fun deleteAddresses(addressIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = addressDao.deleteAddresses(addressIds)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to new address")
        }
    }

    override suspend fun validateAddressName(
        addressName: String,
        addressId: Int?,
    ): ValidationResult {
        if (addressName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = AddressTestTags.ADDRESS_NAME_EMPTY_ERROR,
            )
        }

        if (addressName.length < 2) {
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
        if (addressShortName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = AddressTestTags.ADDRESS_SHORT_NAME_EMPTY_ERROR
            )
        }

        if (addressShortName.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = AddressTestTags.ADDRESS_PRICE_LESS_THAN_TWO_ERROR
            )
        }

        return ValidationResult(
            successful = true
        )
    }

    override suspend fun getAddressWiseOrders(addressId: Int): Flow<List<AddressWiseOrder>> {
        return withContext(ioDispatcher) {
            addressDao.getAddressOrderDetails(addressId).mapLatest { list ->
                list.map {
                    AddressWiseOrder(
                        orderId = it.orderId,
                        customerPhone = it.customer.customerPhone,
                        totalPrice = it.orderPrice.totalPrice,
                        updatedAt = (it.updatedAt ?: it.createdAt).toString(),
                        customerName = it.customer.customerName
                    )
                }
            }
        }
    }
}