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
import com.niyaj.data.repository.CustomerRepository
import com.niyaj.database.dao.CustomerDao
import com.niyaj.database.model.CustomerEntity
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Customer
import com.niyaj.model.CustomerWiseOrder
import com.niyaj.model.searchCustomer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CustomerRepositoryImpl @Inject constructor(
    private val customerDao: CustomerDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : CustomerRepository {

    override suspend fun getAllCustomer(searchText: String): Flow<List<Customer>> {
        return withContext(ioDispatcher) {
            customerDao.getAllCustomer().mapLatest {
                it.map(CustomerEntity::asExternalModel).searchCustomer(searchText)
            }
        }
    }

    override suspend fun getCustomerById(customerId: Int): Resource<Customer?> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(customerDao.getCustomerById(customerId)?.asExternalModel())
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun upsertCustomer(newCustomer: Customer): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = customerDao.upsertCustomer(newCustomer.toEntity())

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create or update customer")
        }
    }

    override suspend fun deleteCustomers(customerIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = customerDao.deleteCustomer(customerIds)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete customers")
        }
    }

    override suspend fun findCustomerByPhone(customerPhone: String, customerId: Int?): Boolean {
        return withContext(ioDispatcher) {
            customerDao.findCustomerByPhone(customerPhone, customerId) != null
        }
    }

    override suspend fun getCustomerWiseOrders(customerId: Int): Flow<List<CustomerWiseOrder>> {
        return withContext(ioDispatcher) {
            customerDao.getCustomerWiseOrder(customerId)
        }
    }

    override suspend fun importCustomerToDatabase(customers: List<Customer>): Resource<Boolean> {
        try {
            customers.forEach { newCustomer ->
                withContext(ioDispatcher) {
                    customerDao.upsertCustomer(newCustomer.toEntity())
                }
            }

            return Resource.Success(true)
        } catch (e: Exception) {
            return Resource.Error(e.message ?: "Unable to create or update customer")
        }
    }
}
