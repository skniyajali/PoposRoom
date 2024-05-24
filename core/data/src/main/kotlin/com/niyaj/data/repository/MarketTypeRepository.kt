/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.MarketType
import kotlinx.coroutines.flow.Flow

interface MarketTypeRepository {

    suspend fun getAllMarketTypes(searchText: String): Flow<List<MarketType>>

    suspend fun getMarketTypeById(id: Int): MarketType?

    suspend fun createOrUpdateMarketType(marketType: MarketType): Resource<Boolean>

    suspend fun deleteMarketTypes(items: List<Int>): Resource<Boolean>

    suspend fun importDataFromFilesToDatabase(data: List<MarketType>): Resource<Boolean>
}