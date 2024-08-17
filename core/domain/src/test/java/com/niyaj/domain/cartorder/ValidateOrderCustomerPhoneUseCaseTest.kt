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

import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_PHONE_EMPTY_ERROR
import com.niyaj.common.tags.CartOrderTestTags.CUSTOMER_PHONE_LENGTH_ERROR
import com.niyaj.common.tags.CartOrderTestTags.CUSTOMER_PHONE_LETTER_ERROR
import com.niyaj.testing.util.MainDispatcherRule
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ValidateOrderCustomerPhoneUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()
    val useCase = ValidateOrderCustomerPhoneUseCase()

    @Test
    fun `validate with empty phone return error`() {
        val result = useCase("")
        assert(result.successful.not())
        assertEquals(CART_ORDER_PHONE_EMPTY_ERROR, result.errorMessage)
    }

    @Test
    fun `validate phone no with less than 10 digit return error`() {
        val result = useCase("123")
        assert(result.successful.not())
        assertEquals(CUSTOMER_PHONE_LENGTH_ERROR, result.errorMessage)
    }

    @Test
    fun `validate phone no with greater than 10 digit return error`() {
        val result = useCase("1239078563421")
        assert(result.successful.not())
        assertEquals(CUSTOMER_PHONE_LENGTH_ERROR, result.errorMessage)
    }

    @Test
    fun `validate phone no with with letter return error`() {
        val result = useCase("123456789P")
        assert(result.successful.not())
        assertEquals(CUSTOMER_PHONE_LETTER_ERROR, result.errorMessage)
    }

    @Test
    fun `validate with valid phone return success`() {
        val result = useCase("1234567890")
        assert(result.successful)
        assertNull(result.errorMessage)
    }
}
