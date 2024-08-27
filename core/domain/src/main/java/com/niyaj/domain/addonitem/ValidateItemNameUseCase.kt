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

package com.niyaj.domain.addonitem

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.AddOnTestTags
import com.niyaj.data.repository.AddOnItemRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Suppress("ReturnCount")
class ValidateItemNameUseCase @Inject constructor(
    private val addOnItemRepository: AddOnItemRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(name: String, addOnItemId: Int?): ValidationResult {
        if (name.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = AddOnTestTags.ADDON_NAME_EMPTY_ERROR,
            )
        }

        if (name.length < 5) {
            return ValidationResult(
                successful = false,
                errorMessage = AddOnTestTags.ADDON_NAME_LENGTH_ERROR,
            )
        }

        if (!name.startsWith(AddOnTestTags.ADDON_WHITELIST_ITEM)) {
            val result = name.any { it.isDigit() }

            if (result) {
                return ValidationResult(
                    successful = false,
                    errorMessage = AddOnTestTags.ADDON_NAME_DIGIT_ERROR,
                )
            }

            val serverResult = withContext(ioDispatcher) {
                addOnItemRepository.findAddOnItemByName(name, addOnItemId)
            }

            if (serverResult) {
                return ValidationResult(
                    successful = false,
                    errorMessage = AddOnTestTags.ADDON_NAME_ALREADY_EXIST_ERROR,
                )
            }
        }

        return ValidationResult(successful = true)
    }
}
