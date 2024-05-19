package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.Address
import com.niyaj.model.AddressWiseOrder
import kotlinx.coroutines.flow.Flow

interface AddressRepository {

    suspend fun getAllAddress(searchText: String): Flow<List<Address>>

    suspend fun getAddressById(addressId: Int): Resource<Address?>

    suspend fun upsertAddress(newAddress: Address): Resource<Boolean>

    suspend fun deleteAddresses(addressIds: List<Int>): Resource<Boolean>

    suspend fun getAddressWiseOrders(addressId: Int): Flow<List<AddressWiseOrder>>

    suspend fun importAddressesToDatabase(addresses: List<Address>): Resource<Boolean>
}