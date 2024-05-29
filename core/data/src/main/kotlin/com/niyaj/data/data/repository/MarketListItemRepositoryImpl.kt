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
import com.niyaj.data.repository.MarketListItemRepository
import com.niyaj.database.dao.MarketListWIthItemsDao
import com.niyaj.model.MarketItemAndQuantity
import com.niyaj.model.MarketListAndType
import com.niyaj.model.searchItems
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class MarketListItemRepositoryImpl(
    private val marketListDao: MarketListWIthItemsDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : MarketListItemRepository {

    override suspend fun getAllMarketItemByTypeId(
        listTypeId: Int,
        searchText: String,
    ): Flow<List<MarketItemAndQuantity>> {
        return withContext(ioDispatcher) {
            marketListDao
                .getAllMarketItemsById(listTypeId)
                .distinctUntilChanged()
                .mapLatest { it.searchItems(searchText) }
        }
    }

    override suspend fun getMarketItemsByTypeIds(
        listTypeIds: List<Int>,
        searchText: String,
    ): Flow<List<MarketItemAndQuantity>> {
        return withContext(ioDispatcher) {
            marketListDao.getAllMarketItemsByIds(listTypeIds)
                .distinctUntilChanged()
                .mapLatest { it.searchItems(searchText) }
        }
    }

    override suspend fun getShareableMarketListById(listTypeId: Int): Flow<List<MarketItemAndQuantity>> {
        return withContext(ioDispatcher) {
            marketListDao.getShareableMarketItems(listTypeId)
        }
    }

    override suspend fun getShareableMarketListByIds(listTypeIds: List<Int>): Flow<List<MarketItemAndQuantity>> {
        return withContext(ioDispatcher) {
            marketListDao.getShareableMarketItems(listTypeIds)
        }
    }

    override suspend fun getMarketListById(listTypeId: Int): Flow<MarketListAndType> {
        return withContext(ioDispatcher) {
            marketListDao.getMarketDetailsById(listTypeId)
        }
    }

    override suspend fun addMarketListItem(listId: Int, itemId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                async {
                    val findItem = marketListDao.findMarketListItem(listId, itemId)

                    if (findItem == null) {
                        marketListDao.addMarketListItem(listId, itemId, 0.0)
                    }
                }.await()

                Resource.Success(true)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun removeMarketListItem(listId: Int, itemId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val findItem = marketListDao.findMarketListItem(listId, itemId)

                if (findItem != null) {
                    marketListDao.removeMarketListItem(listId, itemId)
                }

                Resource.Success(true)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun increaseMarketListItemQuantity(
        listId: Int,
        itemId: Int,
    ): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val unitValue = async {
                    marketListDao.getUnitValueByItemId(itemId)
                }.await()

                val itemQuantity = async {
                    marketListDao.getItemQuantityByListIdItemId(listId, itemId)
                }.await()

                val updatedQuantity = itemQuantity?.plus(unitValue) ?: unitValue

                val result = marketListDao.updateItemQuantityByListIdItemId(
                    listId,
                    itemId,
                    updatedQuantity,
                )

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun decreaseMarketListItemQuantity(
        listId: Int,
        itemId: Int,
    ): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val unitValue = async {
                    marketListDao.getUnitValueByItemId(itemId)
                }.await()

                val itemQuantity = async {
                    marketListDao.getItemQuantityByListIdItemId(listId, itemId)
                }.await()

                val updatedQuantity = itemQuantity?.minus(unitValue) ?: unitValue

                val result = if (updatedQuantity <= 0.0) {
                    marketListDao.removeMarketListItem(listId, itemId)
                } else {
                    marketListDao.updateItemQuantityByListIdItemId(
                        listId,
                        itemId,
                        updatedQuantity,
                    )
                }

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}
