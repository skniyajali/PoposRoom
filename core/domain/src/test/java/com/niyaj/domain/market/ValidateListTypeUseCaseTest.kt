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

import com.niyaj.common.tags.MarketTypeTags.LIST_NAME_LEAST
import com.niyaj.testing.util.MainDispatcherRule
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class ValidateListTypeUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()
    val useCase = ValidateListTypeUseCase()

    @Test
    fun `validate with empty list type return success`() {
        val result = useCase("")

        assert(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `validate with non-empty list type return success`() {
        val result = useCase("AB")

        assertFalse(result.successful)
        assertEquals(LIST_NAME_LEAST, result.errorMessage)
    }

    @Test
    fun `validate with valid list type return success`() {
        val result = useCase("Test List")

        assert(result.successful)
        assertNull(result.errorMessage)
    }
}
