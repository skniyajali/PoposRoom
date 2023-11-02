package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.result.ValidationResult
import com.niyaj.common.utils.toListString
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.MarketListRepository
import com.niyaj.database.dao.MarketItemDao
import com.niyaj.database.dao.MarketListDao
import com.niyaj.database.model.MarketItemEntity
import com.niyaj.database.model.MarketListWithItemEntity
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.MarketItemAndQuantity
import com.niyaj.model.MarketItemWithQuantity
import com.niyaj.model.MarketList
import com.niyaj.model.MarketListType
import com.niyaj.model.MarketListWithItems
import com.niyaj.model.MarketListWithItemsAndQuantity
import com.niyaj.model.searchItems
import com.niyaj.model.searchMarketListItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class MarketListRepositoryImpl(
    private val marketListDao: MarketListDao,
    private val marketItemDao: MarketItemDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : MarketListRepository {

    override suspend fun getAllMarketLists(searchText: String): Flow<List<MarketListWithItems>> {
        return withContext(ioDispatcher) {
            marketListDao.getAllMarketLists().mapLatest { list ->
                list.map { it.asExternalModel() }.searchMarketListItem(searchText)
            }
        }
    }

    override suspend fun getMarketListById(marketId: Int): Flow<MarketList?> {
        return withContext(ioDispatcher) {
            marketListDao.getMarketListById(marketId).map { it?.asExternalModel() }
        }
    }

    override suspend fun getMarketItemsWithQuantityById(
        marketId: Int,
        searchText: String
    ): Flow<List<MarketItemWithQuantity>> {
        return withContext(ioDispatcher) {
            marketListDao.getMarketItems().mapLatest { list ->
                mapItemToItemWithQuantity(list, marketId).searchItems(searchText)
            }
        }
    }


    override suspend fun getMarketItemsAndQuantity(marketId: Int): Flow<List<MarketItemAndQuantity>> {
        return withContext(ioDispatcher) {
            marketListDao.getItemsWithQuantityByMarketId(marketId).mapLatest { list ->
                list.map {
                    MarketItemAndQuantity(
                        item = it.item.asExternalModel(),
                        quantityAndType = it.itemQuantity
                    )
                }
            }
        }
    }

    override suspend fun addOrIgnoreMarketList(newMarketList: MarketList): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateDate = validateMarketDate(newMarketList.marketDate)
                if (validateDate.successful) {
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

                if (validateDate.successful) {
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

                if (validateDate.successful) {
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
                val item = async {
                    marketListDao.findItemByMarketIdAndItemId(marketId, itemId)
                }.await()

                async {
                    val whiteListItem =
                        marketItemDao.getWhitelistItems(marketId).toListString()

                    if (whiteListItem.contains(itemId)) {
                        whiteListItem.remove(itemId)
                        val newItem = whiteListItem.toListString()

                        marketItemDao.updateWhiteListItems(marketId, newItem)
                    }
                }.await()

                if (item == null) {
                    val newItem = MarketListWithItemEntity(
                        marketId = marketId,
                        itemId = itemId,
                        itemQuantity = 0.0,
                        marketListType = MarketListType.Needed
                    )

                    val result = marketListDao.insertOrIgnoreMarketListWithItem(newItem)

                    Resource.Success(result > 0)
                } else if (item.itemQuantity == 0.0) {
                    val result = marketListDao.deleteMarketListWithItem(marketId, itemId)

                    Resource.Success(result > 0)
                }

                Resource.Success(true)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun removeMarketListItem(marketId: Int, itemId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val item = async {
                    marketListDao.getItemQuantityByMarketIdAndItemId(marketId, itemId)
                }.await()

                async {
                    val items = marketItemDao.getWhitelistItems(marketId).toListString()

                    if (!items.contains(itemId)) {
                        items.add(itemId)
                        val newItem = items.toListString()

                        marketItemDao.updateWhiteListItems(marketId, newItem)
                    } else {
                        items.remove(itemId)
                        val newItem = items.toListString()

                        marketItemDao.updateWhiteListItems(marketId, newItem)
                    }
                }.await()

                if (item != null) {
                    val result = marketListDao.deleteMarketListWithItem(marketId, itemId)

                    Resource.Success(result > 0)
                }

                Resource.Success(true)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun increaseMarketListItemQuantity(
        marketId: Int,
        itemId: Int
    ): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val itemQuantity = async {
                    marketListDao.getItemQuantityByMarketIdAndItemId(marketId, itemId)
                }.await()

                val unitValue = async {
                    marketListDao.getItemMeasureUnitValueItemId(itemId)
                }.await()

                if (itemQuantity != null && unitValue != null) {
                    val result = marketListDao.updateMarketListWithItemQuantity(
                        marketId,
                        itemId,
                        itemQuantity.plus(unitValue)
                    )

                    Resource.Success(result > 0)
                }

                Resource.Success(true)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun decreaseMarketListItemQuantity(
        marketId: Int,
        itemId: Int
    ): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val itemQuantity = async {
                    marketListDao.getItemQuantityByMarketIdAndItemId(marketId, itemId)
                }.await()

                val unitValue = async {
                    marketListDao.getItemMeasureUnitValueItemId(itemId)
                }.await()

                if (itemQuantity != null && unitValue != null) {
                    if (itemQuantity.minus(unitValue) >= 0.0) {
                        val result = marketListDao.updateMarketListWithItemQuantity(
                            marketId,
                            itemId,
                            itemQuantity.minus(unitValue)
                        )

                        Resource.Success(result > 0)
                    }

                    Resource.Success(true)
                } else {
                    Resource.Error("Unable to find item")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun addOrUpdateMarketListItems(listWithItems: MarketListWithItemsAndQuantity): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(true)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override fun validateItemQuantity(quantity: String): ValidationResult {
        return ValidationResult(true)
    }

    override fun validateMarketDate(date: Long): ValidationResult {
        return ValidationResult(true)
    }

    private suspend fun mapItemToItemWithQuantity(
        list: List<MarketItemEntity>,
        marketId: Int
    ): List<MarketItemWithQuantity> {
        return coroutineScope {
            list.map { entity ->
                val doesExist = async(ioDispatcher) {
                    marketListDao.findItemIdByMarketIdAndItemId(
                        marketId,
                        entity.itemId
                    ).distinctUntilChanged()
                }

                val quantity = async(ioDispatcher) {
                    marketListDao.getItemQuantityAndType(marketId, entity.itemId)
                        .distinctUntilChanged()
                }

                MarketItemWithQuantity(
                    item = entity.asExternalModel(),
                    doesExist = doesExist.await().map { it != null },
                    quantity = quantity.await()
                )
            }
        }
    }
}