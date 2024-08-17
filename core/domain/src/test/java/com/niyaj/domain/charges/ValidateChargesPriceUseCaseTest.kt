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

import com.niyaj.common.tags.ChargesTestTags
import com.niyaj.testing.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test

class ValidateChargesPriceUseCaseTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var validateChargesPriceUseCase: ValidateChargesPriceUseCase

    @Before
    fun setup() {
        validateChargesPriceUseCase = ValidateChargesPriceUseCase()
    }

    @Test
    fun `given charges price less than 10, when validating, then return error`() {
        val result = validateChargesPriceUseCase(5)

        assertFalse(result.successful)
        assertEquals(ChargesTestTags.CHARGES_PRICE_LESS_THAN_TEN_ERROR, result.errorMessage)
    }

    @Test
    fun `given charges price equal to 10, when validating, then return success`() {
        val result = validateChargesPriceUseCase(10)

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `given charges price greater than 10, when validating, then return success`() {
        val result = validateChargesPriceUseCase(15)

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `given maximum possible charges price, when validating, then return success`() {
        val result = validateChargesPriceUseCase(Int.MAX_VALUE)

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }
}
