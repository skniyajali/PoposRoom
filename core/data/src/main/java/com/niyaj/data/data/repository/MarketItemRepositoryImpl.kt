package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_MEASURE_EMPTY_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NAME_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NAME_DIGIT_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NAME_EMPTY_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NAME_LENGTH_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_PRICE_INVALID
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_PRICE_LESS_THAN_FIVE_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_TYPE_DIGIT_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_TYPE_EMPTY_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_TYPE_LENGTH_ERROR
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.MarketItemRepository
import com.niyaj.data.repository.validation.MarketItemValidationRepository
import com.niyaj.database.dao.MarketItemDao
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.MarketItem
import com.niyaj.model.searchMarketItems
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class MarketItemRepositoryImpl(
    private val marketItemDao: MarketItemDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : MarketItemRepository, MarketItemValidationRepository {

    override suspend fun getAllMarketItems(searchText: String): Flow<List<MarketItem>> {
        return withContext(ioDispatcher) {
            marketItemDao.getAllMarketLists()
                .mapLatest { list ->
                    list.map { it.asExternalModel() }.searchMarketItems(searchText)
                }
        }
    }

    override suspend fun getAllItemType(searchText: String): Flow<List<String>> {
        return withContext(ioDispatcher) {
            marketItemDao.getAllItemTypes().mapLatest { list ->
                list.filter {
                    it.contains(searchText, true)
                }
            }
        }
    }

    override suspend fun getMarketItemById(itemId: Int): Resource<MarketItem?> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(marketItemDao.getMarketListById(itemId)?.asExternalModel())
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun addOrIgnoreMarketItem(newMarketItem: MarketItem): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateItemType = validateItemType(newMarketItem.itemType)
                val validateName = validateItemName(newMarketItem.itemName, newMarketItem.itemId)
                val validatePrice = validateItemPrice(newMarketItem.itemPrice)
                val validateItemUnit = validateItemMeasureUnit(newMarketItem.itemMeasureUnit)

                val hasError = listOf(
                    validateItemType,
                    validateName,
                    validatePrice,
                    validateItemUnit
                ).any { !it.successful }

                if (!hasError) {
                    val result = marketItemDao.insertOrIgnoreMarketList(newMarketItem.toEntity())

                    Resource.Success(result > 0)
                } else {
                    Resource.Error("Unable to create addon item")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun updateMarketItem(newMarketItem: MarketItem): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateItemType = validateItemType(newMarketItem.itemType)
                val validateName = validateItemName(newMarketItem.itemName, newMarketItem.itemId)
                val validatePrice = validateItemPrice(newMarketItem.itemPrice)
                val validateItemUnit = validateItemMeasureUnit(newMarketItem.itemMeasureUnit)

                val hasError = listOf(
                    validateItemType,
                    validateName,
                    validatePrice,
                    validateItemUnit
                ).any { !it.successful }

                if (!hasError) {
                    val result = marketItemDao.updateMarketList(newMarketItem.toEntity())

                    Resource.Success(result > 0)
                } else {
                    Resource.Error("Unable to update item")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun upsertMarketItem(newMarketItem: MarketItem): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateItemType = validateItemType(newMarketItem.itemType)
                val validateName = validateItemName(newMarketItem.itemName, newMarketItem.itemId)
                val validatePrice = validateItemPrice(newMarketItem.itemPrice)
                val validateItemUnit = validateItemMeasureUnit(newMarketItem.itemMeasureUnit)

                val hasError = listOf(
                    validateItemType,
                    validateName,
                    validatePrice,
                    validateItemUnit
                ).any { !it.successful }

                if (!hasError) {
                    val result = marketItemDao.upsertMarketList(newMarketItem.toEntity())

                    Resource.Success(result > 0)
                } else {
                    Resource.Error("Unable to create or update item")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteMarketItem(itemId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = marketItemDao.deleteMarketList(itemId)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteMarketItems(itemIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = marketItemDao.deleteMarketLists(itemIds)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun importMarketItemsToDatabase(marketItems: List<MarketItem>): Resource<Boolean> {
        TODO("Not yet implemented")
    }

    override fun validateItemType(itemType: String): ValidationResult {
        if (itemType.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = MARKET_ITEM_TYPE_EMPTY_ERROR,
            )
        }

        if (itemType.length < 4) {
            return ValidationResult(
                successful = false,
                errorMessage = MARKET_ITEM_TYPE_LENGTH_ERROR,
            )
        }

        val result = itemType.any { it.isDigit() }

        if (result) {
            return ValidationResult(
                successful = false,
                errorMessage = MARKET_ITEM_TYPE_DIGIT_ERROR,
            )
        }

        return ValidationResult(successful = true)

    }

    override suspend fun validateItemName(itemName: String, itemId: Int?): ValidationResult {
        if (itemName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = MARKET_ITEM_NAME_EMPTY_ERROR,
            )
        }

        if (itemName.length < 3) {
            return ValidationResult(
                successful = false,
                errorMessage = MARKET_ITEM_NAME_LENGTH_ERROR,
            )
        }

        val result = itemName.any { it.isDigit() }

        if (result) {
            return ValidationResult(
                successful = false,
                errorMessage = MARKET_ITEM_NAME_DIGIT_ERROR,
            )
        }

        val serverResult = withContext(ioDispatcher) {
            marketItemDao.findItemByName(itemName, itemId) != null
        }

        if (serverResult) {
            return ValidationResult(
                successful = false,
                errorMessage = MARKET_ITEM_NAME_ALREADY_EXIST_ERROR,
            )
        }

        return ValidationResult(successful = true)
    }

    override fun validateItemMeasureUnit(itemMeasureUnit: String): ValidationResult {
        if (itemMeasureUnit.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = MARKET_ITEM_MEASURE_EMPTY_ERROR,
            )
        }

        return ValidationResult(successful = true)
    }

    override fun validateItemPrice(itemPrice: String?): ValidationResult {
        itemPrice?.let { price ->
            if (price.any { it.isLetter() }) {
                return ValidationResult(
                    successful = false,
                    errorMessage = MARKET_ITEM_PRICE_INVALID,
                )
            }

            if (price.toInt() <= 5) {
                return ValidationResult(
                    successful = false,
                    errorMessage = MARKET_ITEM_PRICE_LESS_THAN_FIVE_ERROR,
                )
            }

        }

        return ValidationResult(successful = true)
    }
}