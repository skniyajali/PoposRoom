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

package com.niyaj.domain.payment

import com.niyaj.common.tags.PaymentScreenTags.GIVEN_AMOUNT_EMPTY
import com.niyaj.common.tags.PaymentScreenTags.GIVEN_AMOUNT_LENGTH_ERROR
import com.niyaj.common.tags.PaymentScreenTags.GIVEN_AMOUNT_LETTER_ERROR
import com.niyaj.testing.util.MainDispatcherRule
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ValidateGivenAmountUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()
    val useCase = ValidateGivenAmountUseCase()

    @Test
    fun `given empty salary when invoke then return error`() {
        val result = useCase("")
        assertFalse(result.successful)
        assertEquals(GIVEN_AMOUNT_EMPTY, result.errorMessage)
    }

    @Test
    fun `given salary length less than 2 digit return error`() {
        val result = useCase("1")
        assertFalse(result.successful)
        assertEquals(GIVEN_AMOUNT_LENGTH_ERROR, result.errorMessage)
    }

    @Test
    fun `given salary contain letter return error`() {
        val result = useCase("1a")
        assertFalse(result.successful)
        assertEquals(GIVEN_AMOUNT_LETTER_ERROR, result.errorMessage)
    }

    @Test
    fun `given valid salary return success`() {
        val result = useCase("100")
        assert(result.successful)
    }
}
