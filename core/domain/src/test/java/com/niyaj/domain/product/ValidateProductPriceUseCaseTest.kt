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

package com.niyaj.domain.product

import com.niyaj.common.tags.ProductTestTags.PRODUCT_PRICE_EMPTY_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_PRICE_LENGTH_ERROR
import com.niyaj.testing.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull

class ValidateProductPriceUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()
    val useCase = ValidateProductPriceUseCase()

    @Test
    fun `when product price is 0 then return error`() {
        val result = useCase(0)
        assert(result.successful.not())
        assertEquals(PRODUCT_PRICE_EMPTY_ERROR, result.errorMessage)
    }

    @Test
    fun `when product price is less than 10 return error`() {
        val result = useCase(8)
        assertFalse(result.successful)
        assertEquals(PRODUCT_PRICE_LENGTH_ERROR, result.errorMessage)
    }

    @Test
    fun `when product price is greater than 10 return success`() {
        val result = useCase(11)
        assert(result.successful)
        assertNull(result.errorMessage)
    }
}
