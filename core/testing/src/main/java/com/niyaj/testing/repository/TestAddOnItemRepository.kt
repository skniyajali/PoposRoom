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
import com.niyaj.data.repository.AddOnItemRepository
import com.niyaj.model.AddOnItem
import com.niyaj.model.searchAddOnItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import org.jetbrains.annotations.TestOnly

class TestAddOnItemRepository : AddOnItemRepository {

    /**
     * The backing addon item list for testing
     */
    private val addOnItems = MutableStateFlow(mutableListOf<AddOnItem>())

    override suspend fun getAllAddOnItem(searchText: String): Flow<List<AddOnItem>> {
        return addOnItems.mapLatest { it.searchAddOnItem(searchText) }
    }

    override suspend fun getAddOnItemById(itemId: Int): Resource<AddOnItem?> {
        return Resource.Success(addOnItems.value.find { it.itemId == itemId })
    }

    override suspend fun upsertAddOnItem(newAddOnItem: AddOnItem): Resource<Boolean> {
        val result = addOnItems.value.find { it.itemId == newAddOnItem.itemId }

        return Resource.Success(
            if (result == null) {
                addOnItems.value.add(newAddOnItem)
            } else {
                addOnItems.value.remove(result)
                addOnItems.value.add(newAddOnItem)
            },
        )
    }

    override suspend fun deleteAddOnItems(itemIds: List<Int>): Resource<Boolean> {
        return Resource.Success(addOnItems.value.removeAll { it.itemId in itemIds })
    }

    override suspend fun findAddOnItemByName(name: String, addOnItemId: Int?): Boolean {
        return addOnItems.value.any {
            if (addOnItemId != null) {
                it.itemName == name && it.itemId != addOnItemId
            } else {
                it.itemName == name
            }
        }
    }

    override suspend fun importAddOnItemsToDatabase(addOnItems: List<AddOnItem>): Resource<Boolean> {
        addOnItems.forEach { upsertAddOnItem(it) }

        return Resource.Success(true)
    }

    @TestOnly
    fun updateAddOnData(items: List<AddOnItem>) {
        addOnItems.value = items.toMutableList()
    }

    @TestOnly
    suspend fun createTestItem(): AddOnItem {
        val newItem = AddOnItem(
            itemId = 1,
            itemName = "Test Item",
            itemPrice = 10,
            isApplicable = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = null,
        )
        upsertAddOnItem(newItem)

        return newItem
    }
}
