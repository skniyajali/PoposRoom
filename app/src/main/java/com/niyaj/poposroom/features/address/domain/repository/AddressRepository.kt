package com.niyaj.poposroom.features.address.domain.repository

import com.niyaj.poposroom.features.address.domain.model.Address
import com.niyaj.poposroom.features.common.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AddressRepository {

    suspend fun getAllAddress(searchText: String): Flow<List<Address>>

    suspend fun getAddressById(addressId: Int): Resource<Address?>

    suspend fun addOrIgnoreAddress(newAddress: Address): Int

    suspend fun updateAddress(newAddress: Address): Resource<Boolean>

    suspend fun upsertAddress(newAddress: Address): Resource<Boolean>

    suspend fun deleteAddress(addressId: Int): Resource<Boolean>

    suspend fun deleteAddresses(addressIds: List<Int>): Resource<Boolean>
}