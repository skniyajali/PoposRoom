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

package com.niyaj.domain.market

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NAME_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NAME_DIGIT_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NAME_EMPTY_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NAME_LENGTH_ERROR
import com.niyaj.data.repository.MarketItemRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ValidateMarketItemNameUseCase @Inject constructor(
    private val repository: MarketItemRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(itemName: String, itemId: Int?): ValidationResult {
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
            repository.findItemByName(itemName, itemId)
        }

        if (serverResult) {
            return ValidationResult(
                successful = false,
                errorMessage = MARKET_ITEM_NAME_ALREADY_EXIST_ERROR,
            )
        }

        return ValidationResult(successful = true)
    }
}
