package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.AddOnItem
import kotlinx.coroutines.flow.Flow


interface AddOnItemRepository {

    suspend fun getAllAddOnItem(searchText: String): Flow<List<AddOnItem>>

    suspend fun getAddOnItemById(itemId: Int): Resource<AddOnItem?>

    suspend fun upsertAddOnItem(newAddOnItem: AddOnItem): Resource<Boolean>

    suspend fun deleteAddOnItems(itemIds: List<Int>): Resource<Boolean>

    suspend fun importAddOnItemsToDatabase(addOnItems: List<AddOnItem>): Resource<Boolean>
}