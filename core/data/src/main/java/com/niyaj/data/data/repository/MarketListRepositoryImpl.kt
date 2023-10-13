package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.result.ValidationResult
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.MarketListRepository
import com.niyaj.database.dao.MarketListDao
import com.niyaj.database.model.MarketListWithItemEntity
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.MarketList
import com.niyaj.model.MarketListType
import com.niyaj.model.MarketListWithItems
import com.niyaj.model.searchMarketListItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class MarketListRepositoryImpl(
    private val marketListDao: MarketListDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : MarketListRepository {

    override suspend fun getAllMarketLists(searchText: String): Flow<List<MarketListWithItems>> {
        return withContext(ioDispatcher) {
            marketListDao.getAllMarketLists()
                .mapLatest { list ->
                    list.map { it.asExternalModel() }.searchMarketListItem(searchText)
                }
        }
    }

    override suspend fun getMarketListById(marketId: Int): Resource<MarketListWithItems?> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(marketListDao.getMarketListById(marketId)?.asExternalModel())
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun addOrIgnoreMarketList(newMarketList: MarketList): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateDate = validateMarketDate(newMarketList.marketDate)
                if (!validateDate.successful) {
                    val result = marketListDao.insertOrIgnoreMarketList(newMarketList.toEntity())

                    Resource.Success(result > 0)
                } else {
                    Resource.Error("Unable to create item")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun updateMarketList(newMarketList: MarketList): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateDate = validateMarketDate(newMarketList.marketDate)

                if (!validateDate.successful) {
                    val result = marketListDao.updateMarketList(newMarketList.toEntity())

                    Resource.Success(result > 0)
                } else {
                    Resource.Error("Unable to update item")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun upsertMarketList(newMarketList: MarketList): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateDate = validateMarketDate(newMarketList.marketDate)

                if (!validateDate.successful) {
                    val result = marketListDao.upsertMarketList(newMarketList.toEntity())

                    Resource.Success(result > 0)
                } else {
                    Resource.Error("Unable to create or update item")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteMarketList(marketId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = marketListDao.deleteMarketList(marketId)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
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

    override suspend fun addMarketListItem(marketId: Int, itemId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val item = marketListDao.getItemByMarketIdAndItemId(marketId, itemId)

                if (item != null) {
                    val newQuantity = item.itemQuantity + 0.5

                    val result = marketListDao.updateMarketListWithItemQuantity(marketId, itemId, newQuantity)

                    Resource.Success(result > 0)
                }else {
                    val newItem = MarketListWithItemEntity(
                        marketId = marketId,
                        itemId = itemId,
                        itemQuantity = 0.5,
                        marketListType = MarketListType.Needed
                    )

                    val result = marketListDao.insertOrIgnoreMarketListWithItem(newItem)

                    Resource.Success(result > 0)
                }
            }
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun removeMarketListItem(marketId: Int, itemId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val item = marketListDao.getItemByMarketIdAndItemId(marketId, itemId)

                if (item != null) {
                    if (item.itemQuantity == 0.5){
                        val result = marketListDao.deleteMarketListWithItem(marketId, itemId)

                        Resource.Success(result > 0)
                    }else{
                        val newQuantity = item.itemQuantity - 0.5
                        val result = marketListDao.updateMarketListWithItemQuantity(marketId,itemId, newQuantity)

                        Resource.Success(result > 0)
                    }
                }else {
                    Resource.Error("Unable to find item")
                }
            }
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun addOrUpdateMarketListItems(listWithItems: MarketListWithItems): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {

                val result = marketListDao.upsertMarketList(listWithItems.marketList.toEntity())

                if (result > 0) {
                    val newItems = listWithItems.items.map {
                        MarketListWithItemEntity(
                            listId = it.listId,
                            marketId = result.toInt(),
                            itemId = it.itemId,
                            itemQuantity = it.itemQuantity,
                            marketListType = it.marketListType
                        )
                    }

                    withContext(ioDispatcher) {
                        marketListDao.upsertMarketListsWithItem(newItems)
                    }

                    Resource.Success(true)
                } else {
                    Resource.Error("Unable to add market list items")
                }
            }
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override fun validateItemQuantity(quantity: String): ValidationResult {
        return ValidationResult(true)
    }

    override fun validateMarketDate(date: Long): ValidationResult {
        return ValidationResult(true)
    }
}