package com.niyaj.poposroom.features.customer.domain.use_cases

import com.niyaj.poposroom.features.customer.dao.CustomerDao
import com.niyaj.poposroom.features.customer.domain.model.Customer
import com.niyaj.poposroom.features.customer.domain.model.searchCustomer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class GetAllCustomers(
    private val customerDao: CustomerDao
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(searchText: String): Flow<List<Customer>> {
        return customerDao.getAllCustomer().mapLatest { it.searchCustomer(searchText) }
    }
}