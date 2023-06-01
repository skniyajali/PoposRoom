package com.niyaj.poposroom.features.charges.domain.use_cases

import com.niyaj.poposroom.features.charges.dao.ChargesDao
import com.niyaj.poposroom.features.charges.domain.model.Charges
import com.niyaj.poposroom.features.charges.domain.model.searchCharges
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class GetAllCharges(
    private val chargesDao: ChargesDao
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(searchText: String): Flow<List<Charges>> {
        return chargesDao.getAllCharges().mapLatest { it.searchCharges(searchText) }
    }
}