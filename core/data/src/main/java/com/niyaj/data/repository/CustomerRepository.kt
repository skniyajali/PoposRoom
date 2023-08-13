package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.Customer
import com.niyaj.model.CustomerWiseOrder
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {

    suspend fun getAllCustomer(searchText: String): Flow<List<Customer>>

    suspend fun getCustomerById(customerId: Int): Resource<Customer?>

    suspend fun addOrIgnoreCustomer(newCustomer: Customer): Int

    suspend fun updateCustomer(newCustomer: Customer): Resource<Boolean>

    suspend fun upsertCustomer(newCustomer: Customer): Resource<Boolean>

    suspend fun deleteCustomer(customerId: Int): Resource<Boolean>

    suspend fun deleteCustomers(customerIds: List<Int>): Resource<Boolean>

    suspend fun getCustomerWiseOrders(customerId: Int): Flow<List<CustomerWiseOrder>>
}