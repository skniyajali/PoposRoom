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
import com.niyaj.data.repository.MarketItemRepository
import com.niyaj.model.MarketItem
import com.niyaj.model.MarketType
import com.niyaj.model.MarketTypeIdAndName
import com.niyaj.model.MeasureUnit
import com.niyaj.model.searchItems
import com.niyaj.model.searchMarketItems
import com.niyaj.model.searchMeasureUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import org.jetbrains.annotations.TestOnly

class TestMarketItemRepository : MarketItemRepository {

    /**
     * The backing market item list for testing
     */
    private val items = MutableStateFlow(mutableListOf<MarketItem>())
    private val measureUnits = MutableStateFlow(mutableListOf<MeasureUnit>())
    private val marketTypes = MutableStateFlow(mutableListOf<MarketType>())
    private val marketTypesAndId = MutableStateFlow(mutableListOf<MarketTypeIdAndName>())

    override suspend fun getAllMarketItems(searchText: String): Flow<List<MarketItem>> {
        return items.mapLatest { it.searchMarketItems(searchText) }
    }

    override suspend fun getAllMeasureUnits(searchText: String): Flow<List<MeasureUnit>> {
        return measureUnits.mapLatest { it.searchMeasureUnit(searchText) }
    }

    override suspend fun getAllMarketItemLists(
        searchText: String,
        removedItems: List<Int>,
    ): Flow<List<MarketItem>> {
        return items.mapLatest { list ->
            list.filterNot { it.itemId in removedItems }
                .searchMarketItems(searchText)
        }
    }

    override suspend fun getMarketItemById(itemId: Int): Resource<MarketItem?> {
        val item = items.value.find { it.itemId == itemId }
        return if (item != null) {
            Resource.Success(item)
        } else {
            Resource.Error("Item not found")
        }
    }

    override suspend fun getAllItemType(searchText: String): Flow<List<MarketTypeIdAndName>> {
        return marketTypesAndId.mapLatest {
            it.searchItems(searchText)
        }
    }

    override suspend fun upsertMarketItem(newMarketItem: MarketItem): Resource<Boolean> {
        val index = items.value.indexOfFirst { it.itemId == newMarketItem.itemId }

        if (index != -1) {
            items.value[index] = newMarketItem
        } else {
            items.value.add(newMarketItem)
        }

        return Resource.Success(true)
    }

    override suspend fun deleteMarketItems(itemIds: List<Int>): Resource<Boolean> {
        return Resource.Success(items.value.removeAll { it.itemId in itemIds })
    }

    override suspend fun findItemByName(itemName: String, itemId: Int?): Boolean {
        return items.value.any {
            if (itemId != null) {
                it.itemName == itemName && it.itemId != itemId
            } else {
                it.itemName == itemName
            }
        }
    }

    override suspend fun importMarketItemsToDatabase(marketItems: List<MarketItem>): Resource<Boolean> {
        marketItems.forEach {
            upsertMarketItem(it)
        }

        return Resource.Success(true)
    }

    @TestOnly
    fun updateMarketItemData(marketItems: List<MarketItem>) {
        items.update { marketItems.toMutableList() }
    }

    @TestOnly
    fun updateMeasureUnitData(items: List<MeasureUnit>) {
        measureUnits.update { items.toMutableList() }
    }

    @TestOnly
    fun updateMarketTypeData(items: List<MarketType>) {
        marketTypes.update { items.toMutableList() }
    }

    @TestOnly
    fun updateMarketTypeAndIdData(items: List<MarketTypeIdAndName>) {
        marketTypesAndId.update { items.toMutableList() }
    }

    @TestOnly
    fun createTestItem(): MarketItem {
        val item = MarketItem(
            itemId = 1,
            itemName = "Test Item",
            itemPrice = "100",
            itemType = MarketTypeIdAndName(1, "Test Type"),
            itemMeasureUnit = MeasureUnit(1, "Test Unit", 0.5),
        )

        items.value.add(item)

        return item
    }
}
