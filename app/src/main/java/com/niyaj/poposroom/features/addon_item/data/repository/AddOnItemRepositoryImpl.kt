package com.niyaj.poposroom.features.addon_item.data.repository

import com.niyaj.poposroom.features.addon_item.data.dao.AddOnItemDao
import com.niyaj.poposroom.features.addon_item.domain.model.AddOnItem
import com.niyaj.poposroom.features.addon_item.domain.model.searchAddOnItem
import com.niyaj.poposroom.features.addon_item.domain.repository.AddOnItemRepository
import com.niyaj.poposroom.features.addon_item.domain.repository.AddOnItemValidationRepository
import com.niyaj.poposroom.features.addon_item.domain.utils.AddOnConstants
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.common.utils.ValidationResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
class AddOnItemRepositoryImpl(
    private val addOnItemDao: AddOnItemDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher
) : AddOnItemRepository, AddOnItemValidationRepository {
    override suspend fun getAllAddOnItem(searchText: String): Flow<List<AddOnItem>> {
        return withContext(ioDispatcher) {
            addOnItemDao.getAllAddOnItems().mapLatest { it.searchAddOnItem(searchText) }
        }
    }

    override suspend fun getAddOnItemById(itemId: Int): Resource<AddOnItem?> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(addOnItemDao.getAddOnItemById(itemId))
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun addOrIgnoreAddOnItem(newAddOnItem: AddOnItem): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateName = validateItemName(newAddOnItem.itemName, newAddOnItem.itemId)
                val validatePrice = validateItemPrice(newAddOnItem.itemPrice)

                val hasError = listOf(validateName, validatePrice).any { !it.successful }

                if (!hasError) {
                    val result = addOnItemDao.insertOrIgnoreAddOnItem(newAddOnItem)

                    Resource.Success(result > 0)
                }else {
                    Resource.Error("Unable to create addon item")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun updateAddOnItem(newAddOnItem: AddOnItem): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateName = validateItemName(newAddOnItem.itemName, newAddOnItem.itemId)
                val validatePrice = validateItemPrice(newAddOnItem.itemPrice)

                val hasError = listOf(validateName, validatePrice).any { !it.successful }

                if (!hasError) {
                    val result = addOnItemDao.updateAddOnItem(newAddOnItem)

                    Resource.Success(result > 0)
                }else {
                    Resource.Error("Unable to update addon item")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun upsertAddOnItem(newAddOnItem: AddOnItem): Resource<Boolean> {
        return try {
            val validateName = validateItemName(newAddOnItem.itemName, newAddOnItem.itemId)
            val validatePrice = validateItemPrice(newAddOnItem.itemPrice)

            val hasError = listOf(validateName, validatePrice).any { !it.successful }

            if (!hasError) {
                val result = withContext(ioDispatcher) {
                    addOnItemDao.upsertAddOnItem(newAddOnItem)
                }

                Resource.Success(result > 0)
            }else {
                Resource.Error("Unable to create or update addon item")
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteAddOnItem(itemId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = addOnItemDao.deleteAddOnItem(itemId)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteAddOnItems(itemIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = addOnItemDao.deleteAddOnItems(itemIds)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun validateItemName(name: String, addOnItemId: Int?): ValidationResult {
        if(name.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = AddOnConstants.ADDON_NAME_EMPTY_ERROR,
            )
        }

        if (name.length < 5) {
            return ValidationResult(
                successful = false,
                errorMessage = AddOnConstants.ADDON_NAME_LENGTH_ERROR,
            )
        }

        if (!name.startsWith(AddOnConstants.ADDON_WHITELIST_ITEM)) {

            val result = name.any { it.isDigit() }

            if (result) {
                return ValidationResult(
                    successful = false,
                    errorMessage = AddOnConstants.ADDON_NAME_DIGIT_ERROR,
                )
            }

            val serverResult = withContext(ioDispatcher) {
                addOnItemDao.findAddOnItemByName(name, addOnItemId) != null
            }

            if (serverResult) {
                return ValidationResult(
                    successful = false,
                    errorMessage = AddOnConstants.ADDON_NAME_ALREADY_EXIST_ERROR,
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validateItemPrice(price: Int): ValidationResult {
        if(price == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = AddOnConstants.ADDON_PRICE_EMPTY_ERROR,
            )
        }

        if (price < 5) {
            return ValidationResult(
                successful = false,
                errorMessage = AddOnConstants.ADDON_PRICE_LESS_THAN_FIVE_ERROR,
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}