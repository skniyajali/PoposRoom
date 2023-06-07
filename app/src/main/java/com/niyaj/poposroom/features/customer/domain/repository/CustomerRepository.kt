package com.niyaj.poposroom.features.customer.domain.repository

import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.customer.domain.model.Customer
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {

    suspend fun getAllCustomer(searchText: String): Flow<List<Customer>>

    suspend fun getCustomerById(customerId: Int): Resource<Customer?>

    suspend fun addOrIgnoreCustomer(newCustomer: Customer): Resource<Boolean>

    suspend fun updateCustomer(newCustomer: Customer): Resource<Boolean>

    suspend fun upsertCustomer(newCustomer: Customer): Resource<Boolean>

    suspend fun deleteCustomer(customerId: Int): Resource<Boolean>

    suspend fun deleteCustomers(customerIds: List<Int>): Resource<Boolean>
}