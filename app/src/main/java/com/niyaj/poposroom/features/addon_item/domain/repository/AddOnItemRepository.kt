package com.niyaj.poposroom.features.addon_item.domain.repository

import com.niyaj.poposroom.features.addon_item.domain.model.AddOnItem
import com.niyaj.poposroom.features.common.utils.Resource
import kotlinx.coroutines.flow.Flow


interface AddOnItemRepository {

    suspend fun getAllAddOnItem(searchText: String): Flow<List<AddOnItem>>

    suspend fun getAddOnItemById(itemId: Int): Resource<AddOnItem?>

    suspend fun addOrIgnoreAddOnItem(newAddOnItem: AddOnItem): Resource<Boolean>

    suspend fun updateAddOnItem(newAddOnItem: AddOnItem): Resource<Boolean>

    suspend fun upsertAddOnItem(newAddOnItem: AddOnItem): Resource<Boolean>

    suspend fun deleteAddOnItem(itemId: Int): Resource<Boolean>

    suspend fun deleteAddOnItems(itemIds: List<Int>): Resource<Boolean>
}