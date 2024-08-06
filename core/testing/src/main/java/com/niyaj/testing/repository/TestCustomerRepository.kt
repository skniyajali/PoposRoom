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
import com.niyaj.data.repository.CustomerRepository
import com.niyaj.model.Customer
import com.niyaj.model.CustomerWiseOrder
import com.niyaj.model.searchCustomer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update

class TestCustomerRepository : CustomerRepository {

    /**
     * The backing customer list for testing
     */
    private val customerList = MutableStateFlow(mutableListOf<Customer>())

    private val customerOrders = MutableStateFlow(emptyList<CustomerWiseOrder>())

    override suspend fun getAllCustomer(searchText: String): Flow<List<Customer>> {
        return customerList.mapLatest { it.searchCustomer(searchText) }
    }

    override suspend fun getCustomerById(customerId: Int): Resource<Customer?> {
        return Resource.Success(customerList.value.find { it.customerId == customerId })
    }

    override suspend fun upsertCustomer(newCustomer: Customer): Resource<Boolean> {
        val result = customerList.value.find { it.customerId == newCustomer.customerId }

        return Resource.Success(
            if (result == null) {
                customerList.value.add(newCustomer)
            } else {
                customerList.value.remove(result)
                customerList.value.add(newCustomer)
            },
        )
    }

    override suspend fun deleteCustomers(customerIds: List<Int>): Resource<Boolean> {
        return Resource.Success(customerList.value.removeAll { it.customerId in customerIds })
    }

    override suspend fun findCustomerByPhone(customerPhone: String, customerId: Int?): Boolean {
        return customerList.value.any {
            if (customerId != null) {
                it.customerPhone == customerPhone && it.customerId != customerId
            } else {
                it.customerPhone == customerPhone
            }
        }
    }

    override suspend fun getCustomerWiseOrders(customerId: Int): Flow<List<CustomerWiseOrder>> =
        customerOrders

    override suspend fun importCustomerToDatabase(customers: List<Customer>): Resource<Boolean> {
        customers.forEach { upsertCustomer(it) }

        return Resource.Success(true)
    }

    fun updateCustomerData(items: List<Customer>) {
        customerList.value = items.toMutableList()
    }

    suspend fun createTestItem(): Customer {
        val customer = Customer(
            customerId = 1,
            customerName = "Test Customer",
            customerPhone = "1234567890",
            customerEmail = "test@gmail.com",
        )

        upsertCustomer(customer)

        return customer
    }

    fun updateCustomerWiseOrderData(orders: List<CustomerWiseOrder>) {
        customerOrders.update { orders }
    }
}
