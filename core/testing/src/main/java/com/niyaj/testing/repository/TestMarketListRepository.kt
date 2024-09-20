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
import com.niyaj.common.result.ValidationResult
import com.niyaj.data.repository.MarketListRepository
import com.niyaj.model.MarketItemAndQuantity
import com.niyaj.model.MarketList
import com.niyaj.model.MarketListAndType
import com.niyaj.model.MarketListWithTypes
import com.niyaj.model.MarketTypeIdAndListTypes
import com.niyaj.model.searchMarketList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest

class TestMarketListRepository : MarketListRepository {

    /**
     * The backing market type list for testing
     */
    private val items = MutableStateFlow(mutableListOf<MarketListWithTypes>())
    private val marketTypes = MutableStateFlow(mutableListOf<MarketTypeIdAndListTypes>())
    private val marketListTypes = MutableStateFlow(mutableListOf<MarketListAndType>())
    private val marketItems = MutableStateFlow(mutableListOf<MarketItemAndQuantity>())

    override suspend fun getAllMarketLists(searchText: String): Flow<List<MarketListWithTypes>> {
        return items.mapLatest { it.searchMarketList(searchText) }
    }

    override suspend fun getAllMarketTypes(): Flow<List<MarketTypeIdAndListTypes>> = marketTypes

    override suspend fun getMarketListById(marketId: Int): Flow<MarketListWithTypes?> {
        return items.mapLatest { list -> list.find { it.marketList.marketId == marketId } }
    }

    override suspend fun getMarketDetail(marketId: Int): Flow<MarketListAndType> {
        return marketListTypes.mapLatest { list -> list.find { it.marketId == marketId }!! }
    }

    override suspend fun getShareableMarketItems(
        listTypeIds: List<Int>,
    ): Flow<List<MarketItemAndQuantity>> {
        return marketItems.mapLatest { list -> list.filter { it.listWithTypeId in listTypeIds } }
    }

    override suspend fun upsertMarketList(newMarketList: MarketListWithTypes): Resource<Boolean> {
        val market = items.value.find {
            it.marketList.marketId == newMarketList.marketList.marketId
        }

        return if (market != null) {
            items.value[items.value.indexOf(market)] = newMarketList
            Resource.Success(true)
        } else {
            items.value.add(newMarketList)
            Resource.Success(true)
        }
    }

    override suspend fun deleteMarketLists(marketIds: List<Int>): Resource<Boolean> {
        return Resource.Success(items.value.removeAll { it.marketList.marketId in marketIds })
    }

    override suspend fun importMarketListsToDatabase(marketLists: List<MarketList>): Resource<Boolean> {
        return Resource.Success(true)
    }

    override fun validateItemQuantity(quantity: String): ValidationResult {
        return ValidationResult(true)
    }

    override fun validateMarketDate(date: Long): ValidationResult {
        return ValidationResult(true)
    }
}
