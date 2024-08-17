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

package com.niyaj.domain.customer

import com.niyaj.common.tags.CustomerTestTags
import com.niyaj.testing.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import kotlin.test.Test

class ValidateCustomerNameUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    val validateCustomerNameUseCase = ValidateCustomerNameUseCase()

    @Test
    fun `given null customer name, when validating, then return success`() {
        val result = validateCustomerNameUseCase(null)

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `given empty customer name, when validating, then return success`() {
        val result = validateCustomerNameUseCase("")

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `given customer name with 1 character, when validating, then return error`() {
        val result = validateCustomerNameUseCase("A")

        assertFalse(result.successful)
        assertEquals(CustomerTestTags.CUSTOMER_NAME_LENGTH_ERROR, result.errorMessage)
    }

    @Test
    fun `given customer name with more than 3 characters, when validating, then return success`() {
        val result = validateCustomerNameUseCase("John Doe")

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `given customer name with leading and trailing spaces, when validating, then return success`() {
        val result = validateCustomerNameUseCase("  John Doe  ")

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }
}
