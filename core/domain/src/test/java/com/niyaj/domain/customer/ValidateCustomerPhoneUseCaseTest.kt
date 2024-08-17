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
import com.niyaj.testing.repository.TestCustomerRepository
import com.niyaj.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test

class ValidateCustomerPhoneUseCaseTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()
    private val repository = TestCustomerRepository()

    private lateinit var validateCustomerPhoneUseCase: ValidateCustomerPhoneUseCase

    @Before
    fun setup() {
        validateCustomerPhoneUseCase = ValidateCustomerPhoneUseCase(
            repository,
            UnconfinedTestDispatcher(),
        )
    }

    @Test
    fun `given empty phone number, when validating, then return error`() = runTest {
        val result = validateCustomerPhoneUseCase("", null)

        assertFalse(result.successful)
        assertEquals(CustomerTestTags.CUSTOMER_PHONE_EMPTY_ERROR, result.errorMessage)
    }

    @Test
    fun `given phone number with less than 10 digits, when validating, then return error`() =
        runTest {
            val result = validateCustomerPhoneUseCase("123456789", null)

            assertFalse(result.successful)
            assertEquals(CustomerTestTags.CUSTOMER_PHONE_LENGTH_ERROR, result.errorMessage)
        }

    @Test
    fun `given phone number with more than 10 digits, when validating, then return error`() =
        runTest {
            val result = validateCustomerPhoneUseCase("12345678901", null)

            assertFalse(result.successful)
            assertEquals(CustomerTestTags.CUSTOMER_PHONE_LENGTH_ERROR, result.errorMessage)
        }

    @Test
    fun `given phone number containing letters, when validating, then return error`() = runTest {
        val result = validateCustomerPhoneUseCase("123456789a", null)

        assertFalse(result.successful)
        assertEquals(CustomerTestTags.CUSTOMER_PHONE_LETTER_ERROR, result.errorMessage)
    }

    @Test
    fun `given existing phone number, when validating, then return error`() = runTest {
        val data = repository.createTestItem()
        val result = validateCustomerPhoneUseCase(data.customerPhone, null)

        assertFalse(result.successful)
        assertEquals(CustomerTestTags.CUSTOMER_PHONE_ALREADY_EXIST_ERROR, result.errorMessage)
    }

    @Test
    fun `given valid existing phone number with matching ID, then return success`() = runTest {
        val data = repository.createTestItem()
        val result = validateCustomerPhoneUseCase(data.customerPhone, data.customerId)

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `given valid new phone number, when validating, then return success`() = runTest {
        val result = validateCustomerPhoneUseCase("1234567890", 1)

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }
}
