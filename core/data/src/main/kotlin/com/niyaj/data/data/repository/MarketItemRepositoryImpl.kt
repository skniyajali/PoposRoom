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
import com.niyaj.data.repository.MarketItemRepository
import com.niyaj.database.dao.MarketItemDao
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.MarketItem
import com.niyaj.model.MarketTypeIdAndName
import com.niyaj.model.MeasureUnit
import com.niyaj.model.searchMarketItems
import com.niyaj.model.searchMeasureUnit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MarketItemRepositoryImpl @Inject constructor(
    private val marketItemDao: MarketItemDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : MarketItemRepository {

    override suspend fun getAllMarketItems(searchText: String): Flow<List<MarketItem>> {
        return withContext(ioDispatcher) {
            marketItemDao.getAllMarketItems()
                .mapLatest { list ->
                    list.map { it.asExternalModel() }.searchMarketItems(searchText)
                }
        }
    }

    override suspend fun getAllMeasureUnits(searchText: String): Flow<List<MeasureUnit>> {
        return withContext(ioDispatcher) {
            marketItemDao.getAllMeasureUnits()
                .mapLatest { list ->
                    list.map { it.asExternalModel() }.searchMeasureUnit(searchText)
                }
        }
    }

    override suspend fun getAllMarketItemLists(
        searchText: String,
        removedItems: List<Int>,
    ): Flow<List<MarketItem>> {
        return withContext(ioDispatcher) {
            marketItemDao.getAllMarketItems()
                .mapLatest { list ->
                    list.filterNot {
                        removedItems.contains(it.marketItem.itemId)
                    }.map {
                        it.asExternalModel()
                    }.searchMarketItems(searchText)
                }
        }
    }

    override suspend fun getAllItemType(searchText: String): Flow<List<MarketTypeIdAndName>> {
        return withContext(ioDispatcher) {
            marketItemDao.getAllItemTypes().mapLatest { list ->
                list.filter {
                    if (searchText.isNotEmpty()) {
                        it.typeName.contains(searchText, ignoreCase = true)
                    } else {
                        true
                    }
                }
            }
        }
    }

    override suspend fun getMarketItemById(itemId: Int): Resource<MarketItem?> {
        return try {
            withContext(ioDispatcher) {
                val result = marketItemDao.getMarketItemById(itemId)

                Resource.Success(result?.asExternalModel())
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun upsertMarketItem(newMarketItem: MarketItem): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = marketItemDao.upsertMarketItem(newMarketItem.toEntity())

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteMarketItems(itemIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = marketItemDao.deleteMarketItems(itemIds)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun findItemByName(itemName: String, itemId: Int?): Boolean {
        return withContext(ioDispatcher) {
            marketItemDao.findItemByName(itemName, itemId) != null
        }
    }

    override suspend fun importMarketItemsToDatabase(marketItems: List<MarketItem>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                marketItems.forEach {
                    upsertMarketItem(it)
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}
