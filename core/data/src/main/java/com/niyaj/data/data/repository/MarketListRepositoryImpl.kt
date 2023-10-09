package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.MarketListTestTags.MARKET_LIST_MEASURE_EMPTY_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_LIST_NAME_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_LIST_NAME_DIGIT_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_LIST_NAME_EMPTY_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_LIST_NAME_LENGTH_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_LIST_PRICE_INVALID
import com.niyaj.common.tags.MarketListTestTags.MARKET_LIST_PRICE_LESS_THAN_FIVE_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_LIST_TYPE_DIGIT_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_LIST_TYPE_EMPTY_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_LIST_TYPE_LENGTH_ERROR
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.MarketListRepository
import com.niyaj.data.repository.validation.MarketListValidationRepository
import com.niyaj.database.dao.MarketListDao
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.MarketList
import com.niyaj.model.searchMarketList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class MarketListRepositoryImpl(
    private val marketListDao: MarketListDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : MarketListRepository, MarketListValidationRepository {

    override suspend fun getAllMarketList(searchText: String): Flow<List<MarketList>> {
        return withContext(ioDispatcher) {
            marketListDao.getAllMarketLists()
                .mapLatest { list ->
                    list.map { it.asExternalModel() }.searchMarketList(searchText)
                }
        }
    }

    override suspend fun getAllItemType(searchText: String): Flow<List<String>> {
        return withContext(ioDispatcher) {
            marketListDao.getAllItemTypes().mapLatest { list ->
                list.filter {
                    it.contains(searchText, true)
                }
            }
        }
    }

    override suspend fun getMarketListById(itemId: Int): Resource<MarketList?> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(marketListDao.getMarketListById(itemId)?.asExternalModel())
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun addOrIgnoreMarketList(newMarketList: MarketList): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateItemType = validateItemType(newMarketList.itemType)
                val validateName = validateItemName(newMarketList.itemName, newMarketList.itemId)
                val validatePrice = validateItemPrice(newMarketList.itemPrice)
                val validateItemUnit = validateItemMeasureUnit(newMarketList.itemMeasureUnit)

                val hasError = listOf(
                    validateItemType,
                    validateName,
                    validatePrice,
                    validateItemUnit
                ).any { !it.successful }

                if (!hasError) {
                    val result = marketListDao.insertOrIgnoreMarketList(newMarketList.toEntity())

                    Resource.Success(result > 0)
                } else {
                    Resource.Error("Unable to create addon item")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun updateMarketList(newMarketList: MarketList): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateItemType = validateItemType(newMarketList.itemType)
                val validateName = validateItemName(newMarketList.itemName, newMarketList.itemId)
                val validatePrice = validateItemPrice(newMarketList.itemPrice)
                val validateItemUnit = validateItemMeasureUnit(newMarketList.itemMeasureUnit)

                val hasError = listOf(
                    validateItemType,
                    validateName,
                    validatePrice,
                    validateItemUnit
                ).any { !it.successful }

                if (!hasError) {
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
                val validateItemType = validateItemType(newMarketList.itemType)
                val validateName = validateItemName(newMarketList.itemName, newMarketList.itemId)
                val validatePrice = validateItemPrice(newMarketList.itemPrice)
                val validateItemUnit = validateItemMeasureUnit(newMarketList.itemMeasureUnit)

                val hasError = listOf(
                    validateItemType,
                    validateName,
                    validatePrice,
                    validateItemUnit
                ).any { !it.successful }

                if (!hasError) {
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

    override suspend fun deleteMarketList(itemId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = marketListDao.deleteMarketList(itemId)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteMarketLists(itemIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = marketListDao.deleteMarketLists(itemIds)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun importMarketListsToDatabase(marketLists: List<MarketList>): Resource<Boolean> {
        TODO("Not yet implemented")
    }

    override fun validateItemType(itemType: String): ValidationResult {
        if (itemType.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = MARKET_LIST_TYPE_EMPTY_ERROR,
            )
        }

        if (itemType.length < 4) {
            return ValidationResult(
                successful = false,
                errorMessage = MARKET_LIST_TYPE_LENGTH_ERROR,
            )
        }

        val result = itemType.any { it.isDigit() }

        if (result) {
            return ValidationResult(
                successful = false,
                errorMessage = MARKET_LIST_TYPE_DIGIT_ERROR,
            )
        }

        return ValidationResult(successful = true)

    }

    override suspend fun validateItemName(itemName: String, itemId: Int?): ValidationResult {
        if (itemName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = MARKET_LIST_NAME_EMPTY_ERROR,
            )
        }

        if (itemName.length < 3) {
            return ValidationResult(
                successful = false,
                errorMessage = MARKET_LIST_NAME_LENGTH_ERROR,
            )
        }

        val result = itemName.any { it.isDigit() }

        if (result) {
            return ValidationResult(
                successful = false,
                errorMessage = MARKET_LIST_NAME_DIGIT_ERROR,
            )
        }

        val serverResult = withContext(ioDispatcher) {
            marketListDao.findItemByName(itemName, itemId) != null
        }

        if (serverResult) {
            return ValidationResult(
                successful = false,
                errorMessage = MARKET_LIST_NAME_ALREADY_EXIST_ERROR,
            )
        }

        return ValidationResult(successful = true)
    }

    override fun validateItemMeasureUnit(itemMeasureUnit: String): ValidationResult {
        if (itemMeasureUnit.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = MARKET_LIST_MEASURE_EMPTY_ERROR,
            )
        }

        return ValidationResult(successful = true)
    }

    override fun validateItemPrice(itemPrice: String?): ValidationResult {
        itemPrice?.let { price ->
            if (price.any { it.isLetter() }) {
                return ValidationResult(
                    successful = false,
                    errorMessage = MARKET_LIST_PRICE_INVALID,
                )
            }

            if (price.toInt() <= 5) {
                return ValidationResult(
                    successful = false,
                    errorMessage = MARKET_LIST_PRICE_LESS_THAN_FIVE_ERROR,
                )
            }

        }

        return ValidationResult(successful = true)
    }
}