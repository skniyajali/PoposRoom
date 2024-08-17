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
import com.niyaj.data.repository.MarketTypeRepository
import com.niyaj.database.dao.MarketTypeDao
import com.niyaj.database.model.MarketTypeEntity
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.MarketType
import com.niyaj.model.searchMarketType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MarketTypeRepositoryImpl @Inject constructor(
    private val marketTypeDao: MarketTypeDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : MarketTypeRepository {
    override suspend fun getAllMarketTypes(searchText: String): Flow<List<MarketType>> {
        return withContext(ioDispatcher) {
            marketTypeDao.getAllMarketTypes()
                .mapLatest(List<MarketTypeEntity>::asExternalModel)
                .mapLatest { it.searchMarketType(searchText) }
        }
    }

    override suspend fun getMarketTypeById(id: Int): MarketType? {
        return withContext(ioDispatcher) {
            marketTypeDao.getMarketTypeById(id)?.asExternalModel()
        }
    }

    override suspend fun createOrUpdateMarketType(marketType: MarketType): Resource<Boolean> {
        return try {
            val newMarketType = marketType.toEntity()
            val result = marketTypeDao.upsertMarketType(newMarketType)

            Resource.Success(result > 0)
        } catch (e: Exception) {
            Resource.Error("An error occurred while saving market type")
        }
    }

    override suspend fun deleteMarketTypes(items: List<Int>): Resource<Boolean> {
        return try {
            val result = marketTypeDao.deleteMarketTypes(items)
            Resource.Success(result > 0)
        } catch (e: Exception) {
            Resource.Error("An error occurred while deleting market types")
        }
    }

    override suspend fun findMarketTypeByName(typeName: String, typeId: Int?): Boolean {
        return withContext(ioDispatcher) {
            marketTypeDao.findMarketTypeByName(typeName, typeId) != null
        }
    }

    override suspend fun importDataFromFilesToDatabase(data: List<MarketType>): Resource<Boolean> {
        return withContext(ioDispatcher) {
            try {
                data.forEach { marketType ->
                    createOrUpdateMarketType(marketType)
                }

                Resource.Success(true)
            } catch (e: Exception) {
                Resource.Error("An error occurred while importing data")
            }
        }
    }
}
