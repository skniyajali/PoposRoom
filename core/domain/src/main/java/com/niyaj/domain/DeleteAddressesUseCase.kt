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

package com.niyaj.domain

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.AddressRepository
import com.niyaj.data.repository.CartOrderRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteAddressesUseCase @Inject constructor(
    private val addressRepository: AddressRepository,
    private val cartOrderRepository: CartOrderRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(addressIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                addressRepository.deleteAddresses(addressIds).data?.let {
                    if (it) {
                        addressIds.forEach { addressId ->
                            val orderIds = cartOrderRepository.getCartOrderIdsByAddressId(addressId)
                            cartOrderRepository.deleteCartOrders(orderIds)
                        }
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}
