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

import com.niyaj.common.tags.CartOrderTestTags.ADDRESS_NAME_LENGTH_ERROR
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_NAME_EMPTY_ERROR
import com.niyaj.testing.util.MainDispatcherRule
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class ValidateOrderAddressNameUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()
    val useCase = ValidateOrderAddressNameUseCase()

    @Test
    fun `given empty address name when invoke then return error`() {
        val result = useCase("")
        assert(result.successful.not())
        assertEquals(CART_ORDER_NAME_EMPTY_ERROR, result.errorMessage)
    }

    @Test
    fun `given address name length less than 6 digit return error`() {
        val result = useCase("Test")

        assertFalse(result.successful)
        assertEquals(ADDRESS_NAME_LENGTH_ERROR, result.errorMessage)
    }

    @Test
    fun `given valid address name return success`() {
        val result = useCase("Test Address")
        assert(result.successful)
        assertNull(result.errorMessage)
    }
}
