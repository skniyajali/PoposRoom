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

package com.niyaj.domain.expense

import com.niyaj.common.tags.ExpenseTestTags.EXPENSES_PRICE_IS_NOT_VALID
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_PRICE_EMPTY_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_PRICE_LESS_THAN_TEN_ERROR
import com.niyaj.testing.util.MainDispatcherRule
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ValidateExpenseAmountUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()
    val useCase = ValidateExpenseAmountUseCase()

    @Test
    fun `validate expense amount with empty amount`() {
        val result = useCase("")
        assert(result.successful.not())
        assertEquals(EXPENSE_PRICE_EMPTY_ERROR, result.errorMessage)
    }

    @Test
    fun `validate expense amount with amount less than 10`() {
        val result = useCase("9")
        assert(result.successful.not())
        assertEquals(EXPENSE_PRICE_LESS_THAN_TEN_ERROR, result.errorMessage)
    }

    @Test
    fun `validate expense amount with invalid amount`() {
        val result = useCase("abc")
        assert(result.successful.not())
        assertEquals(EXPENSES_PRICE_IS_NOT_VALID, result.errorMessage)
    }

    @Test
    fun `validate expense amount with amount greater than 10`() {
        val result = useCase("11")
        assert(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `validate expense amount with valid amount`() {
        val result = useCase("10")
        assert(result.successful)
        assertNull(result.errorMessage)
    }
}
