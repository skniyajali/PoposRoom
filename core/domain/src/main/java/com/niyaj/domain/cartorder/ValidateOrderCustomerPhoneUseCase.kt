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

package com.niyaj.domain.cartorder

import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_PHONE_EMPTY_ERROR
import com.niyaj.common.tags.CartOrderTestTags.CUSTOMER_PHONE_LENGTH_ERROR
import com.niyaj.common.tags.CartOrderTestTags.CUSTOMER_PHONE_LETTER_ERROR
import javax.inject.Inject

class ValidateOrderCustomerPhoneUseCase @Inject constructor() {
    operator fun invoke(customerPhone: String): ValidationResult {
        if (customerPhone.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = CART_ORDER_PHONE_EMPTY_ERROR,
            )
        }

        if (customerPhone.length < 10 || customerPhone.length > 10) {
            return ValidationResult(
                successful = false,
                errorMessage = CUSTOMER_PHONE_LENGTH_ERROR,
            )
        }

        val containsLetters = customerPhone.any { it.isLetter() }

        if (containsLetters) {
            return ValidationResult(
                successful = false,
                errorMessage = CUSTOMER_PHONE_LETTER_ERROR,
            )
        }

        return ValidationResult(successful = true)
    }
}
