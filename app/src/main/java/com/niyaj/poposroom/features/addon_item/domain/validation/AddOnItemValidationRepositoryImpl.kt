package com.niyaj.poposroom.features.addon_item.domain.validation

import com.niyaj.poposroom.features.addon_item.dao.AddOnItemDao
import com.niyaj.poposroom.features.addon_item.domain.utils.AddOnConstants.ADDON_NAME_ALREADY_EXIST_ERROR
import com.niyaj.poposroom.features.addon_item.domain.utils.AddOnConstants.ADDON_NAME_DIGIT_ERROR
import com.niyaj.poposroom.features.addon_item.domain.utils.AddOnConstants.ADDON_NAME_EMPTY_ERROR
import com.niyaj.poposroom.features.addon_item.domain.utils.AddOnConstants.ADDON_NAME_LENGTH_ERROR
import com.niyaj.poposroom.features.addon_item.domain.utils.AddOnConstants.ADDON_PRICE_EMPTY_ERROR
import com.niyaj.poposroom.features.addon_item.domain.utils.AddOnConstants.ADDON_PRICE_LESS_THAN_FIVE_ERROR
import com.niyaj.poposroom.features.addon_item.domain.utils.AddOnConstants.ADDON_WHITELIST_ITEM
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers.IO
import com.niyaj.poposroom.features.common.utils.ValidationResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddOnItemValidationRepositoryImpl @Inject constructor(
    private val addOnItemDao: AddOnItemDao,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
) : AddOnItemValidationRepository {
    override suspend fun validateItemName(name: String, addOnItemId: Int?): ValidationResult {
        if(name.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = ADDON_NAME_EMPTY_ERROR,
            )
        }

        if (name.length < 5) {
            return ValidationResult(
                successful = false,
                errorMessage = ADDON_NAME_LENGTH_ERROR,
            )
        }

        if (!name.startsWith(ADDON_WHITELIST_ITEM)) {

            val result = name.any { it.isDigit() }

            if (result) {
                return ValidationResult(
                    successful = false,
                    errorMessage = ADDON_NAME_DIGIT_ERROR,
                )
            }

            val serverResult = withContext(ioDispatcher) {
                addOnItemDao.findAddOnItemByName(addOnItemId, name) != null
            }

            if (serverResult) {
                return ValidationResult(
                    successful = false,
                    errorMessage = ADDON_NAME_ALREADY_EXIST_ERROR,
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
                errorMessage = ADDON_PRICE_EMPTY_ERROR,
            )
        }

        if (price < 5) {
            return ValidationResult(
                successful = false,
                errorMessage = ADDON_PRICE_LESS_THAN_FIVE_ERROR,
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}