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

import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NAME_EMPTY_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NAME_LENGTH_ERROR
import com.niyaj.testing.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import kotlin.test.Test

class ValidateExpenseNameUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()
    val useCase = ValidateExpenseNameUseCase()

    @Test
    fun `validate expense name with empty name`() {
        val result = useCase("")
        assert(result.successful.not())
        assertEquals(EXPENSE_NAME_EMPTY_ERROR, result.errorMessage)
    }

    @Test
    fun `validate expense name with name less than 3 characters`() {
        val result = useCase("Te")
        assert(result.successful.not())
        assertEquals(EXPENSE_NAME_LENGTH_ERROR, result.errorMessage)
    }

    @Test
    fun `validate expense name with valid name`() {
        val result = useCase("Test")
        assert(result.successful)
        assertNull(result.errorMessage)
    }
}
