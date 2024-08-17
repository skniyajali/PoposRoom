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

import com.niyaj.common.tags.PrinterInfoTestTags.NBR_LINES_IS_REQUIRED
import com.niyaj.testing.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import kotlin.test.Test

class ValidateNbrLinesUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()
    val useCase = ValidateNbrLinesUseCase()

    @Test
    fun `validate with zero nbrLines returns error`() {
        val result = useCase(0)
        assertFalse(result.successful)
        assertEquals(NBR_LINES_IS_REQUIRED, result.errorMessage)
    }

    @Test
    fun `validate with invalid nbrLines returns error`() {
        val result = useCase(-2)

        assertFalse(result.successful)
        assertEquals(NBR_LINES_IS_REQUIRED, result.errorMessage)
    }

    @Test
    fun `validate with valid nbrLines returns success`() {
        val result = useCase(10)

        assertEquals(true, result.successful)
        assertEquals(null, result.errorMessage)
    }
}
