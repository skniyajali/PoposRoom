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

import com.niyaj.common.tags.ProductTestTags.PRODUCT_TAG_LENGTH_ERROR
import com.niyaj.testing.util.MainDispatcherRule
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ValidateProductTagUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()
    val useCase = ValidateProductTagUseCase()

    @Test
    fun `when tag name is not empty and less than 3 digit then return error`() {
        val result = useCase("AB")
        assertFalse(result.successful)
        assertEquals(PRODUCT_TAG_LENGTH_ERROR, result.errorMessage)
    }

    @Test
    fun `when tag name is valid then return success`() {
        val result = useCase("Test")
        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }
}
