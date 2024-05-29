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
import com.niyaj.common.result.ValidationResult
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.mapper.toExternalModel
import com.niyaj.data.repository.MarketListRepository
import com.niyaj.database.dao.MarketListDao
import com.niyaj.database.dao.MarketListWIthTypeDao
import com.niyaj.database.model.MarketListWithTypeEntity
import com.niyaj.model.MarketItemAndQuantity
import com.niyaj.model.MarketList
import com.niyaj.model.MarketListAndType
import com.niyaj.model.MarketListWithTypes
import com.niyaj.model.MarketTypeIdAndListTypes
import com.niyaj.model.asExternalModel
import com.niyaj.model.toExternalModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import timber.log.Timber

class MarketListRepositoryImpl(
    private val marketListDao: MarketListDao,
    private val listTypeDao: MarketListWIthTypeDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : MarketListRepository {

    override suspend fun getAllMarketLists(searchText: String): Flow<List<MarketListWithTypes>> {
        return withContext(ioDispatcher) {
            marketListDao.getMarketItems().mapLatest { typeList ->
                typeList.toExternalModel()
            }
        }
    }

    override suspend fun getAllMarketTypes(): Flow<List<MarketTypeIdAndListTypes>> {
        return withContext(ioDispatcher) {
            marketListDao.getAllMarketTypes().map { listTypes ->
                listTypes.sortedBy { it.listTypes.size }
            }
        }
    }

    override suspend fun getMarketListById(marketId: Int): Flow<MarketListWithTypes?> {
        return withContext(ioDispatcher) {
            marketListDao.getMarketListByMarketId(marketId).map {
                it?.asExternalModel()
            }
        }
    }

    override suspend fun getMarketDetail(marketId: Int): Flow<MarketListAndType> {
        return withContext(ioDispatcher) {
            marketListDao.getMarketDetailsById(marketId)
        }
    }

    override suspend fun getShareableMarketItems(listTypeIds: List<Int>): Flow<List<MarketItemAndQuantity>> {
        return withContext(ioDispatcher) {
            marketListDao.getShareableMarketItems(listTypeIds)
        }
    }

    override suspend fun upsertMarketList(newMarketList: MarketListWithTypes): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateDate: ValidationResult =
                    validateMarketDate(newMarketList.marketList.marketDate)

                if (validateDate.successful && newMarketList.marketTypes.isNotEmpty()) {
                    val marketId: Int = newMarketList.marketList.marketId

                    val result = marketListDao.upsertMarketList(newMarketList.marketList.toEntity())

                    if (marketId == 0) {
                        val listTypes: List<MarketListWithTypeEntity> =
                            newMarketList.marketTypes.toExternalModel(result.toInt())

                        async {
                            listTypes.forEach { it: MarketListWithTypeEntity ->
                                listTypeDao.insertMarketListWithType(it)
                            }
                        }.await()
                    } else {
                        val listTypes: List<MarketListWithTypeEntity> =
                            newMarketList.marketTypes.toExternalModel(marketId)

                        val allTypes =
                            async { listTypeDao.getAllMarketListWithTypeById(marketId) }.await()
                        val existingTypesSet = allTypes.map { it.typeId to it.listType }.toSet()

                        val newTypesToInsert = async {
                            listTypes.filter { newType ->
                                val typeIdAndListType = newType.typeId to newType.listType
                                !existingTypesSet.contains(typeIdAndListType)
                            }
                        }.await()

                        val typesToDelete = async {
                            allTypes.filter { existingType ->
                                !listTypes.any { it.typeId == existingType.typeId && it.listType == existingType.listType }
                            }
                        }.await()

                        // Insert new types and delete obsolete types concurrently
                        awaitAll(
                            async {
                                newTypesToInsert.forEach {
                                    listTypeDao.insertMarketListWithType(it)
                                }
                            },
                            async {
                                typesToDelete.forEach {
                                    listTypeDao.deleteMarketListWithType(
                                        marketId,
                                        it.typeId,
                                        it.listType,
                                    )
                                }
                            },
                        )
                    }

                    Resource.Success(true)
                } else {
                    Resource.Error("Unable to create or update item")
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteMarketLists(marketIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = marketListDao.deleteMarketLists(marketIds)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun importMarketListsToDatabase(marketLists: List<MarketList>): Resource<Boolean> {
        TODO("Not yet implemented")
    }

    override fun validateItemQuantity(quantity: String): ValidationResult {
        return ValidationResult(true)
    }

    override fun validateMarketDate(date: Long): ValidationResult {
        return ValidationResult(true)
    }
}
