package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.MarketList
import kotlinx.coroutines.flow.Flow

interface MarketListRepository {

    suspend fun getAllMarketList(searchText: String): Flow<List<MarketList>>

    suspend fun getMarketListById(itemId: Int): Resource<MarketList?>

    suspend fun getAllItemType(searchText: String): Flow<List<String>>

    suspend fun addOrIgnoreMarketList(newMarketList: MarketList): Resource<Boolean>

    suspend fun updateMarketList(newMarketList: MarketList): Resource<Boolean>

    suspend fun upsertMarketList(newMarketList: MarketList): Resource<Boolean>

    suspend fun deleteMarketList(itemId: Int): Resource<Boolean>

    suspend fun deleteMarketLists(itemIds: List<Int>): Resource<Boolean>

    suspend fun importMarketListsToDatabase(marketLists: List<MarketList>): Resource<Boolean>
}