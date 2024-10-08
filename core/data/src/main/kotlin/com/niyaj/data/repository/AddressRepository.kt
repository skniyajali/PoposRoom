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

    suspend fun findAddressNameByNameAndId(addressName: String, addressId: Int? = null): Boolean

    suspend fun getAddressWiseOrders(addressId: Int): Flow<List<AddressWiseOrder>>

    suspend fun importAddressesToDatabase(addresses: List<Address>): Resource<Boolean>
}
