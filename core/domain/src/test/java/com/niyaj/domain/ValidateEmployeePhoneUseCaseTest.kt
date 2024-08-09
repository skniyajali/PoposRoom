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

import com.niyaj.common.tags.EmployeeTestTags
import com.niyaj.domain.employee.ValidateEmployeePhoneUseCase
import com.niyaj.testing.repository.TestEmployeeRepository
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

class ValidateEmployeePhoneUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestEmployeeRepository()
    private lateinit var validateEmployeePhoneUseCase: ValidateEmployeePhoneUseCase

    @Before
    fun setup() {
        validateEmployeePhoneUseCase = ValidateEmployeePhoneUseCase(
            repository,
            UnconfinedTestDispatcher(),
        )
    }

    @Test
    fun `invoke returns error when phone is empty`() = runTest {
        val result = validateEmployeePhoneUseCase("")

        assertFalse(result.successful)
        assertEquals(EmployeeTestTags.EMPLOYEE_PHONE_EMPTY_ERROR, result.errorMessage)
    }

    @Test
    fun `invoke returns error when phone length is not 10 digits`() = runTest {
        val result = validateEmployeePhoneUseCase("123456789")

        assertFalse(result.successful)
        assertEquals(EmployeeTestTags.EMPLOYEE_PHONE_LENGTH_ERROR, result.errorMessage)
    }

    @Test
    fun `invoke returns error when phone contains non-digit characters`() = runTest {
        val result = validateEmployeePhoneUseCase("123456789a")

        assertFalse(result.successful)
        assertEquals(EmployeeTestTags.EMPLOYEE_PHONE_LETTER_ERROR, result.errorMessage)
    }

    @Test
    fun `invoke returns error when phone already exists`() = runTest {
        val data = repository.createTestItem()

        val result = validateEmployeePhoneUseCase(data.employeePhone)

        assertFalse(result.successful)
        assertEquals(EmployeeTestTags.EMPLOYEE_PHONE_ALREADY_EXIST_ERROR, result.errorMessage)
    }

    @Test
    fun `invoke returns success for valid phone number`() = runTest {
        val data = repository.createTestItem()

        val result = validateEmployeePhoneUseCase(data.employeePhone, data.employeeId)

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }
}
