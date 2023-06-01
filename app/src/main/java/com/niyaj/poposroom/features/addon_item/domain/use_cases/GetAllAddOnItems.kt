package com.niyaj.poposroom.features.addon_item.domain.use_cases

import com.niyaj.poposroom.features.addon_item.dao.AddOnItemDao
import com.niyaj.poposroom.features.addon_item.domain.model.AddOnItem
import com.niyaj.poposroom.features.addon_item.domain.model.searchAddOnItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetAllAddOnItems @Inject constructor(
    private val addOnItemDao: AddOnItemDao
) {

    operator fun invoke(searchText: String): Flow<List<AddOnItem>> {
        return addOnItemDao.getAllAddOnItems().mapLatest { it.searchAddOnItem(searchText) }
    }
}