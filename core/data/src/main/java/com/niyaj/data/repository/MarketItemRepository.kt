package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.MarketItem
import kotlinx.coroutines.flow.Flow

interface MarketItemRepository {

    suspend fun getAllMarketItems(searchText: String): Flow<List<MarketItem>>

    suspend fun getAllMarketItemLists(searchText: String, removedItems: List<Int>): Flow<List<MarketItem>>

    suspend fun getMarketItemById(itemId: Int): Resource<MarketItem?>

    suspend fun getAllItemType(searchText: String): Flow<List<String>>

    suspend fun addOrIgnoreMarketItem(newMarketItem: MarketItem): Resource<Boolean>

    suspend fun updateMarketItem(newMarketItem: MarketItem): Resource<Boolean>

    suspend fun upsertMarketItem(newMarketItem: MarketItem): Resource<Boolean>

    suspend fun deleteMarketItem(itemId: Int): Resource<Boolean>

    suspend fun deleteMarketItems(itemIds: List<Int>): Resource<Boolean>

    suspend fun importMarketItemsToDatabase(marketItems: List<MarketItem>): Resource<Boolean>
}