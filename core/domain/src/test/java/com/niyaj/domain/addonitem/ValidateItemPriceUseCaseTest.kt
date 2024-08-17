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

package com.niyaj.domain.addonitem

import com.niyaj.common.tags.AddOnTestTags
import com.niyaj.testing.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import kotlin.test.Test

class ValidateItemPriceUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val priceUseCase = ValidateItemPriceUseCase()

    @Test
    fun `price of zero returns error`() {
        val result = priceUseCase(0)
        assertFalse(result.successful)
        assertEquals(AddOnTestTags.ADDON_PRICE_EMPTY_ERROR, result.errorMessage)
    }

    @Test
    fun `price less than 5 returns error`() {
        val result = priceUseCase(4)
        assertFalse(result.successful)
        assertEquals(AddOnTestTags.ADDON_PRICE_LESS_THAN_FIVE_ERROR, result.errorMessage)
    }

    @Test
    fun `price of 5 is valid`() {
        val result = priceUseCase(5)
        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `price greater than 5 is valid`() {
        val result = priceUseCase(10)
        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }
}
