package com.niyaj.poposroom.features.address.domain.use_cases

import com.niyaj.poposroom.features.address.dao.AddressDao
import com.niyaj.poposroom.features.address.domain.model.Address
import com.niyaj.poposroom.features.address.domain.model.searchAddress
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetAllAddresses @Inject constructor(
    private val addressDao: AddressDao
) {

    operator fun invoke(searchText: String): Flow<List<Address>> {
        return addressDao.getAllAddresses().mapLatest { it.searchAddress(searchText) }
    }
}