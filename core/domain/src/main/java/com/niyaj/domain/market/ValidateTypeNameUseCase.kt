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
import com.niyaj.common.tags.MarketTypeTags.TYPE_NAME_EXISTS
import com.niyaj.common.tags.MarketTypeTags.TYPE_NAME_IS_REQUIRED
import com.niyaj.common.tags.MarketTypeTags.TYPE_NAME_LEAST
import com.niyaj.data.repository.MarketTypeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ValidateTypeNameUseCase @Inject constructor(
    private val repository: MarketTypeRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(typeName: String, typeId: Int? = null): ValidationResult {
        if (typeName.isEmpty()) return ValidationResult(false, TYPE_NAME_IS_REQUIRED)

        if (typeName.length < 3) return ValidationResult(false, TYPE_NAME_LEAST)

        val serverResult = withContext(ioDispatcher) {
            repository.findMarketTypeByName(typeName, typeId)
        }

        if (serverResult) return ValidationResult(false, TYPE_NAME_EXISTS)

        return ValidationResult(true, null)
    }
}
