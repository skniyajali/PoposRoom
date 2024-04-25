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
import com.niyaj.database.model.MarketItemEntity
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.MarketItem
import com.niyaj.model.MeasureUnit
import com.niyaj.model.searchMarketItems
import com.niyaj.model.searchMeasureUnit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
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
            marketItemDao.getAllMarketItems()
                .mapLatest { list ->
                    list.map { it.asExternalModel() }.searchMarketItems(searchText)
                }
        }
    }

    override suspend fun getAllMeasureUnits(searchText: String): Flow<List<MeasureUnit>> {
        return withContext(ioDispatcher) {
            marketItemDao.getAllMeasureUnits()
                .mapLatest { list ->
                    list.map { it.asExternalModel() }.searchMeasureUnit(searchText)
                }
        }
    }

    override suspend fun getAllMarketItemLists(
        searchText: String,
        removedItems: List<Int>,
    ): Flow<List<MarketItem>> {
        return withContext(ioDispatcher) {
            marketItemDao.getAllMarketItems()
                .mapLatest { list ->
                    list.filterNot {
                        removedItems.contains(it.itemId)
                    }.map {
                        it.asExternalModel()
                    }.searchMarketItems(searchText)
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
                Resource.Success(null)
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
                val validateItemUnit =
                    validateItemMeasureUnit(newMarketItem.itemMeasureUnit?.unitName ?: "")

                val hasError = listOf(
                    validateItemType,
                    validateName,
                    validatePrice,
                    validateItemUnit
                ).any { !it.successful }

                if (!hasError) {
                    val findUnit = newMarketItem.itemMeasureUnit?.let {
                        async {
                            marketItemDao.findMeasureUnitByIdOrName(it.unitId, it.unitName)
                        }.await()
                    }

                    val measureUnit = if (findUnit == null) {
                        val result = newMarketItem.itemMeasureUnit?.toEntity()?.let {
                            marketItemDao.upsertMeasureUnit(it)
                        }
                        result?.let {
                            marketItemDao.getMeasureUnitById(it.toInt())
                        }
                    } else findUnit

                    val newItem = MarketItemEntity(
                        itemId = newMarketItem.itemId,
                        itemType = newMarketItem.itemType,
                        itemName = newMarketItem.itemName,
                        itemPrice = newMarketItem.itemPrice,
                        itemDescription = newMarketItem.itemDescription,
                        itemMeasureUnit = measureUnit,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = if (newMarketItem.itemId == 0) null else System.currentTimeMillis()
                    )

                    val result = marketItemDao.insertOrIgnoreMarketItem(newItem)

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
                val validateItemUnit =
                    validateItemMeasureUnit(newMarketItem.itemMeasureUnit?.unitName ?: "")

                val hasError = listOf(
                    validateItemType,
                    validateName,
                    validatePrice,
                    validateItemUnit
                ).any { !it.successful }

                if (!hasError) {
                    val findUnit = newMarketItem.itemMeasureUnit?.let {
                        async {
                            marketItemDao.findMeasureUnitByIdOrName(it.unitId, it.unitName)
                        }.await()
                    }

                    val measureUnit = if (findUnit == null) {
                        val result = newMarketItem.itemMeasureUnit?.toEntity()?.let {
                            marketItemDao.upsertMeasureUnit(it)
                        }
                        result?.let {
                            marketItemDao.getMeasureUnitById(it.toInt())
                        }
                    } else findUnit

                    val newItem = MarketItemEntity(
                        itemId = newMarketItem.itemId,
                        itemType = newMarketItem.itemType,
                        itemName = newMarketItem.itemName,
                        itemPrice = newMarketItem.itemPrice,
                        itemDescription = newMarketItem.itemDescription,
                        itemMeasureUnit = measureUnit,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = if (newMarketItem.itemId == 0) null else System.currentTimeMillis()
                    )

                    val result = marketItemDao.updateMarketItem(newItem)

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
                val validateItemUnit =
                    validateItemMeasureUnit(newMarketItem.itemMeasureUnit?.unitName ?: "")

                val hasError = listOf(
                    validateItemType,
                    validateName,
                    validatePrice,
                    validateItemUnit
                ).any { !it.successful }

                if (!hasError) {
                    val findUnit = newMarketItem.itemMeasureUnit?.let {
                        marketItemDao.findMeasureUnitByIdOrName(it.unitId, it.unitName)
                    }

                    val measureUnit = if (findUnit == null) {
                        val result = newMarketItem.itemMeasureUnit?.toEntity()?.let {
                            marketItemDao.upsertMeasureUnit(it)
                        }
                        result?.let {
                            marketItemDao.getMeasureUnitById(it.toInt())
                        }
                    } else findUnit

                    if (measureUnit == null) {
                        Resource.Error("Unable to create or get Measure Unit")
                    } else {

                        val newItem = MarketItemEntity(
                            itemId = newMarketItem.itemId,
                            itemType = newMarketItem.itemType,
                            itemName = newMarketItem.itemName,
                            itemPrice = newMarketItem.itemPrice,
                            itemDescription = newMarketItem.itemDescription,
                            itemMeasureUnit = measureUnit,
                            createdAt = System.currentTimeMillis(),
                            updatedAt = if (newMarketItem.itemId == 0) null else System.currentTimeMillis()
                        )

                        val result = marketItemDao.upsertMarketItem(newItem)

                        Resource.Success(result > 0)
                    }
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
                val result = marketItemDao.deleteMarketItem(itemId)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteMarketItems(itemIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = marketItemDao.deleteMarketItems(itemIds)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun importMarketItemsToDatabase(marketItems: List<MarketItem>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                marketItems.forEach {
                    upsertMarketItem(it)
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
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
//            marketItemDao.findItemByName(itemName, itemId) != null
            false
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