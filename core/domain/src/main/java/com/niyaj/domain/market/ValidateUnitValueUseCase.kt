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

import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_VALUE_EMPTY_ERROR
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_VALUE_INVALID
import com.niyaj.common.utils.safeDouble
import javax.inject.Inject

class ValidateUnitValueUseCase @Inject constructor() {
    operator fun invoke(unitValue: String): ValidationResult {
        if (unitValue.isEmpty()) {
            return ValidationResult(false, UNIT_VALUE_EMPTY_ERROR)
        }

        try {
            if (unitValue.safeDouble() <= 0) {
                return ValidationResult(false, UNIT_VALUE_INVALID)
            }
        } catch (e: Exception) {
            return ValidationResult(false, UNIT_VALUE_INVALID)
        }

        return ValidationResult(true)
    }
}
