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

package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.AddressRepository
import com.niyaj.database.dao.AddressDao
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Address
import com.niyaj.model.AddressWiseOrder
import com.niyaj.model.searchAddress
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(
    private val addressDao: AddressDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : AddressRepository {

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

    override suspend fun upsertAddress(newAddress: Address): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = addressDao.upsertAddress(newAddress.toEntity())

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

    override suspend fun findAddressNameByNameAndId(addressName: String, addressId: Int?): Boolean {
        return withContext(ioDispatcher) {
            addressDao.findAddressByName(addressName, addressId) != null
        }
    }

    override suspend fun getAddressWiseOrders(addressId: Int): Flow<List<AddressWiseOrder>> {
        return withContext(ioDispatcher) {
            addressDao.getAddressWiseOrder(addressId)
        }
    }

    override suspend fun importAddressesToDatabase(addresses: List<Address>): Resource<Boolean> {
        try {
            addresses.forEach { newAddress ->
                withContext(ioDispatcher) {
                    addressDao.upsertAddress(newAddress.toEntity())
                }
            }
            return Resource.Success(true)
        } catch (e: Exception) {
            return Resource.Error(e.message)
        }
    }
}
