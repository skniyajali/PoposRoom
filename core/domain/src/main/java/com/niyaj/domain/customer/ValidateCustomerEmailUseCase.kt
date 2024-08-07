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

package com.niyaj.domain.customer

import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.CustomerTestTags
import javax.inject.Inject

class ValidateCustomerEmailUseCase @Inject constructor() {
    operator fun invoke(customerEmail: String?): ValidationResult {
        if (!customerEmail.isNullOrEmpty()) {
            val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
            if (!customerEmail.matches(emailRegex.toRegex())) {
                return ValidationResult(
                    successful = false,
                    errorMessage = CustomerTestTags.CUSTOMER_EMAIL_VALID_ERROR,
                )
            }
        }

        return ValidationResult(successful = true)
    }
}
