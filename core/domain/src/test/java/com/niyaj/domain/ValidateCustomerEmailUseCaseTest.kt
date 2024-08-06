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

package com.niyaj.domain

import com.niyaj.common.tags.CustomerTestTags
import com.niyaj.domain.customer.ValidateCustomerEmailUseCase
import com.niyaj.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import kotlin.test.Test

class ValidateCustomerEmailUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val validateCustomerEmailUseCase = ValidateCustomerEmailUseCase()

    @Test
    fun `given null email, when validating, then return success`() = runTest {
        val result = validateCustomerEmailUseCase(null)

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `given empty email, when validating, then return success`() = runTest {
        val result = validateCustomerEmailUseCase("")

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `given invalid email without at symbol, when validating, then return error`() = runTest {
        val result = validateCustomerEmailUseCase("invalidemail.com")

        assertFalse(result.successful)
        assertEquals(CustomerTestTags.CUSTOMER_EMAIL_VALID_ERROR, result.errorMessage)
    }

    @Test
    fun `given invalid email without domain, when validating, then return error`() = runTest {
        val result = validateCustomerEmailUseCase("invalid@")

        assertFalse(result.successful)
        assertEquals(CustomerTestTags.CUSTOMER_EMAIL_VALID_ERROR, result.errorMessage)
    }

    @Test
    fun `given invalid email with special characters, when validating, then return error`() = runTest {
        val result = validateCustomerEmailUseCase("invalid!@email.com")

        assertFalse(result.successful)
        assertEquals(CustomerTestTags.CUSTOMER_EMAIL_VALID_ERROR, result.errorMessage)
    }

    @Test
    fun `given valid email, when validating, then return success`() = runTest {
        val result = validateCustomerEmailUseCase("valid@email.com")

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `given valid email with subdomain, when validating, then return success`() {
        val result = validateCustomerEmailUseCase("valid@subdomain.email.com")

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `given valid email with plus sign, when validating, then return success`() {
        val result = validateCustomerEmailUseCase("valid+test@email.com")

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }
}
