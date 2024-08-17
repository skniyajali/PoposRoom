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

import com.niyaj.common.tags.PrinterInfoTestTags.WIDTH_IS_REQUIRED
import com.niyaj.testing.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import kotlin.test.Test

class ValidatePrinterWidthUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()
    val useCase = ValidatePrinterWidthUseCase()

    @Test
    fun `validate with zero width returns error`() {
        val result = useCase(0f)
        assertFalse(result.successful)
        assertEquals(WIDTH_IS_REQUIRED, result.errorMessage)
    }

    @Test
    fun `validate with invalid width returns error`() {
        val result = useCase(-0.2f)

        assertFalse(result.successful)
        assertEquals(WIDTH_IS_REQUIRED, result.errorMessage)
    }

    @Test
    fun `validate with valid width returns success`() {
        val result = useCase(10f)

        assertEquals(true, result.successful)
        assertEquals(null, result.errorMessage)
    }
}
