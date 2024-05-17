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
import com.niyaj.common.result.ValidationResult
import com.niyaj.model.MarketItemAndQuantity
import com.niyaj.model.MarketList
import com.niyaj.model.MarketListAndType
import com.niyaj.model.MarketListWithTypes
import com.niyaj.model.MarketTypeIdAndListTypes
import kotlinx.coroutines.flow.Flow

interface MarketListRepository {

    suspend fun getAllMarketLists(searchText: String): Flow<List<MarketListWithTypes>>

    suspend fun getAllMarketTypes(): Flow<List<MarketTypeIdAndListTypes>>

    suspend fun getMarketListById(marketId: Int): Flow<MarketListWithTypes?>

    suspend fun getMarketDetail(marketId: Int): Flow<MarketListAndType>

    suspend fun getShareableMarketItems(listTypeIds: List<Int>): Flow<List<MarketItemAndQuantity>>

    suspend fun upsertMarketList(newMarketList: MarketListWithTypes): Resource<Boolean>

    suspend fun deleteMarketLists(marketIds: List<Int>): Resource<Boolean>

    suspend fun importMarketListsToDatabase(marketLists: List<MarketList>): Resource<Boolean>

    fun validateItemQuantity(quantity: String): ValidationResult

    fun validateMarketDate(date: Long): ValidationResult

}