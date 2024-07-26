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
import com.niyaj.data.repository.AddOnItemRepository
import com.niyaj.database.dao.AddOnItemDao
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.AddOnItem
import com.niyaj.model.searchAddOnItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddOnItemRepositoryImpl @Inject constructor(
    private val addOnItemDao: AddOnItemDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : AddOnItemRepository {

    override suspend fun getAllAddOnItem(searchText: String): Flow<List<AddOnItem>> {
        return withContext(ioDispatcher) {
            addOnItemDao.getAllAddOnItems()
                .mapLatest { list ->
                    list.map { it.asExternalModel() }.searchAddOnItem(searchText)
                }
        }
    }

    override suspend fun getAddOnItemById(itemId: Int): Resource<AddOnItem?> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(addOnItemDao.getAddOnItemById(itemId)?.asExternalModel())
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun upsertAddOnItem(newAddOnItem: AddOnItem): Resource<Boolean> {
        return try {
            val result = withContext(ioDispatcher) {
                addOnItemDao.upsertAddOnItem(newAddOnItem.toEntity())
            }

            Resource.Success(result > 0)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteAddOnItems(itemIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = addOnItemDao.deleteAddOnItems(itemIds)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun findAddOnItemByName(name: String, addOnItemId: Int?): Boolean {
        return withContext(ioDispatcher) {
            addOnItemDao.findAddOnItemByName(name, addOnItemId) != null
        }
    }

    override suspend fun importAddOnItemsToDatabase(addOnItems: List<AddOnItem>): Resource<Boolean> {
        try {
            addOnItems.forEach { newAddOnItem ->
                withContext(ioDispatcher) {
                    addOnItemDao.upsertAddOnItem(newAddOnItem.toEntity())
                }
            }

            return Resource.Success(true)
        } catch (e: Exception) {
            return Resource.Error(e.message)
        }
    }
}
