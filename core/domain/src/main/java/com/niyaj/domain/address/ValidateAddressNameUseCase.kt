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

package com.niyaj.domain.address

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.AddressTestTags
import com.niyaj.data.repository.AddressRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ValidateAddressNameUseCase @Inject constructor(
    private val repository: AddressRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(addressName: String, addressId: Int? = null): ValidationResult {
        if (addressName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = AddressTestTags.ADDRESS_NAME_EMPTY_ERROR,
            )
        }

        if (addressName.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = AddressTestTags.ADDRESS_NAME_LENGTH_ERROR,
            )
        }

        val serverResult = withContext(ioDispatcher) {
            repository.findAddressNameByNameAndId(addressName, addressId)
        }

        if (serverResult) {
            return ValidationResult(
                successful = false,
                errorMessage = AddressTestTags.ADDRESS_NAME_ALREADY_EXIST_ERROR,
            )
        }

        return ValidationResult(successful = true)
    }
}
