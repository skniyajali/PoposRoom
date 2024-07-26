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

package com.niyaj.domain.absent

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_DATE_EMPTY
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_DATE_EXIST
import com.niyaj.data.repository.AbsentRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ValidateAbsentDateUseCase @Inject constructor(
    private val absentRepository: AbsentRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        absentDate: String,
        employeeId: Int? = null,
        absentId: Int? = null,
    ): ValidationResult {
        if (absentDate.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = ABSENT_DATE_EMPTY,
            )
        }

        if (employeeId != null) {
            val serverResult = withContext(ioDispatcher) {
                absentRepository.findEmployeeByDate(absentDate, employeeId, absentId)
            }

            if (serverResult) {
                return ValidationResult(
                    successful = false,
                    errorMessage = ABSENT_DATE_EXIST,
                )
            }
        }

        return ValidationResult(true)
    }
}
