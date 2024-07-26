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

package com.niyaj.domain.printer

import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.PrinterInfoTestTags.PRODUCT_NAME_LENGTH_IS_ERROR
import com.niyaj.common.tags.PrinterInfoTestTags.PRODUCT_NAME_LENGTH_IS_REQUIRED
import javax.inject.Inject

class ValidateProductNameLengthUseCase @Inject constructor() {
    operator fun invoke(length: Int): ValidationResult {
        if (length <= 0) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_NAME_LENGTH_IS_REQUIRED,
            )
        }

        if (length <= 10) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_NAME_LENGTH_IS_ERROR,
            )
        }

        return ValidationResult(true)
    }
}
