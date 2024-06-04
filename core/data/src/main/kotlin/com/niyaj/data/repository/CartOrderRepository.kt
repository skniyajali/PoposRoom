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
import com.niyaj.model.AddOnItem
import com.niyaj.model.Address
import com.niyaj.model.CartOrder
import com.niyaj.model.CartOrderWithAddOnAndCharges
import com.niyaj.model.Charges
import com.niyaj.model.Customer
import com.niyaj.model.EmployeeNameAndId
import com.niyaj.model.Selected
import kotlinx.coroutines.flow.Flow

interface CartOrderRepository {

    suspend fun getAllProcessingCartOrders(): Flow<List<CartOrder>>

    fun getSelectedCartOrder(): Flow<Selected?>

    suspend fun getAllAddOnItem(): Flow<List<AddOnItem>>

    suspend fun getAllCharges(): Flow<List<Charges>>

    suspend fun getDeliveryPartners(): Flow<List<EmployeeNameAndId>>

    suspend fun insertOrUpdateSelectedOrder(selected: Selected): Resource<Boolean>

    suspend fun getAllAddresses(searchText: String): Flow<List<Address>>

    suspend fun getAllCustomer(searchText: String): Flow<List<Customer>>

    suspend fun getAllCartOrders(searchText: String, viewAll: Boolean): Flow<List<CartOrder>>

    suspend fun getCartOrderById(orderId: Int): Resource<CartOrderWithAddOnAndCharges?>

    suspend fun getLastCreatedOrderId(orderId: Int): Int

    suspend fun addOrIgnoreAddress(newAddress: Address): Int

    suspend fun addOrIgnoreCustomer(newCustomer: Customer): Int

    suspend fun createOrUpdateCartOrder(newCartOrder: CartOrderWithAddOnAndCharges): Resource<Boolean>

    suspend fun deleteCartOrder(orderId: Int): Resource<Boolean>

    suspend fun deleteCartOrders(orderIds: List<Int>): Resource<Boolean>

    suspend fun getCartOrderIdsByAddressId(addressId: Int): List<Int>

    suspend fun getCartOrderIdsByCustomerId(customerId: Int): List<Int>
}
