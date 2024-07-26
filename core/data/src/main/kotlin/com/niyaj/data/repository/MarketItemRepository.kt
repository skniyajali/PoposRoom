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

package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.MarketItem
import com.niyaj.model.MarketTypeIdAndName
import com.niyaj.model.MeasureUnit
import kotlinx.coroutines.flow.Flow

interface MarketItemRepository {

    suspend fun getAllMarketItems(searchText: String): Flow<List<MarketItem>>

    suspend fun getAllMeasureUnits(searchText: String): Flow<List<MeasureUnit>>

    suspend fun getAllMarketItemLists(
        searchText: String,
        removedItems: List<Int>,
    ): Flow<List<MarketItem>>

    suspend fun getMarketItemById(itemId: Int): Resource<MarketItem?>

    suspend fun getAllItemType(searchText: String): Flow<List<MarketTypeIdAndName>>

    suspend fun upsertMarketItem(newMarketItem: MarketItem): Resource<Boolean>

    suspend fun deleteMarketItems(itemIds: List<Int>): Resource<Boolean>

    suspend fun findItemByName(itemName: String, itemId: Int?): Boolean

    suspend fun importMarketItemsToDatabase(marketItems: List<MarketItem>): Resource<Boolean>
}
