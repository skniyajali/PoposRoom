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

package com.niyaj.domain.charges

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.ChargesTestTags
import com.niyaj.data.repository.ChargesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Suppress("ReturnCount")
class ValidateChargesNameUseCase @Inject constructor(
    private val chargesRepository: ChargesRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(chargesName: String, chargesId: Int? = null): ValidationResult {
        if (chargesName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = ChargesTestTags.CHARGES_NAME_EMPTY_ERROR,
            )
        }

        if (chargesName.length < 5) {
            return ValidationResult(
                successful = false,
                errorMessage = ChargesTestTags.CHARGES_NAME_LENGTH_ERROR,
            )
        }

        if (chargesName.any { it.isDigit() }) {
            return ValidationResult(
                successful = false,
                errorMessage = ChargesTestTags.CHARGES_NAME_DIGIT_ERROR,
            )
        }

        val serverResult = withContext(ioDispatcher) {
            chargesRepository.findChargesByNameAndId(chargesName, chargesId)
        }

        if (serverResult) {
            return ValidationResult(
                successful = false,
                errorMessage = ChargesTestTags.CHARGES_NAME_ALREADY_EXIST_ERROR,
            )
        }

        return ValidationResult(successful = true)
    }
}
