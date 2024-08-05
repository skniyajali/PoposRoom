/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.testing.repository

import com.niyaj.common.result.Resource
import com.niyaj.data.repository.AddressRepository
import com.niyaj.model.Address
import com.niyaj.model.AddressWiseOrder
import com.niyaj.model.searchAddress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import org.jetbrains.annotations.TestOnly

class TestAddressRepository : AddressRepository {

    /**
     * The backing address list for testing
     */
    private val items = MutableStateFlow(mutableListOf<Address>())

    private val addressDetails = MutableStateFlow(emptyList<AddressWiseOrder>())

    override suspend fun getAllAddress(searchText: String): Flow<List<Address>> {
        return items.mapLatest { it.searchAddress(searchText) }
    }

    override suspend fun getAddressById(addressId: Int): Resource<Address?> {
        return Resource.Success(items.value.find { it.addressId == addressId })
    }

    override suspend fun upsertAddress(newAddress: Address): Resource<Boolean> {
        val result = items.value.find { it.addressId == newAddress.addressId }

        return Resource.Success(
            if (result == null) {
                items.value.add(newAddress)
            } else {
                items.value.remove(result)
                items.value.add(newAddress)
            },
        )
    }

    override suspend fun deleteAddresses(addressIds: List<Int>): Resource<Boolean> {
        return Resource.Success(items.value.removeAll { it.addressId in addressIds })
    }

    override suspend fun findAddressNameByNameAndId(addressName: String, addressId: Int?): Boolean {
        return items.value.any {
            if (addressId != null) {
                it.addressName == addressName && it.addressId != addressId
            } else {
                it.addressName == addressName
            }
        }
    }

    override suspend fun getAddressWiseOrders(addressId: Int): Flow<List<AddressWiseOrder>> =
        addressDetails

    override suspend fun importAddressesToDatabase(addresses: List<Address>): Resource<Boolean> {
        addresses.forEach { upsertAddress(it) }

        return Resource.Success(true)
    }

    @TestOnly
    fun updateAddressData(addressList: List<Address>) {
        items.value = addressList.toMutableList()
    }

    @TestOnly
    fun createTestAddress(): Address {
        val address = Address(
            addressId = 1,
            addressName = "Test Address",
            shortName = "TA",
        )

        items.value.add(address)
        return address
    }

    @TestOnly
    fun updateAddressWiseOrders(orders: List<AddressWiseOrder>) {
        addressDetails.update { orders }
    }
}
