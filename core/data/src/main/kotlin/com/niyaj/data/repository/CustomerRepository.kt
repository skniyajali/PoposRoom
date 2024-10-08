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
import com.niyaj.model.Customer
import com.niyaj.model.CustomerWiseOrder
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {

    suspend fun getAllCustomer(searchText: String): Flow<List<Customer>>

    suspend fun getCustomerById(customerId: Int): Resource<Customer?>

    suspend fun upsertCustomer(newCustomer: Customer): Resource<Boolean>

    suspend fun deleteCustomers(customerIds: List<Int>): Resource<Boolean>

    suspend fun findCustomerByPhone(customerPhone: String, customerId: Int?): Boolean

    suspend fun getCustomerWiseOrders(customerId: Int): Flow<List<CustomerWiseOrder>>

    suspend fun importCustomerToDatabase(customers: List<Customer>): Resource<Boolean>
}
