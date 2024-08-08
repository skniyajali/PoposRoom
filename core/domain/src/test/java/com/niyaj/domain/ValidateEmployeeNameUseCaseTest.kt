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
import com.niyaj.domain.employee.ValidateEmployeeNameUseCase
import com.niyaj.testing.repository.TestEmployeeRepository
import com.niyaj.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test

class ValidateEmployeeNameUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestEmployeeRepository()
    private lateinit var validateEmployeeNameUseCase: ValidateEmployeeNameUseCase

    @Before
    fun setup() {
        validateEmployeeNameUseCase = ValidateEmployeeNameUseCase(
            repository,
            UnconfinedTestDispatcher(),
        )
    }

    @Test
    fun `invoke returns error when name is empty`() = runTest {
        val result = validateEmployeeNameUseCase("", null)

        assertFalse(result.successful)
        assertEquals(EmployeeTestTags.EMPLOYEE_NAME_EMPTY_ERROR, result.errorMessage)
    }

    @Test
    fun `invoke returns error when name is less than 4 characters`() = runTest {
        val result = validateEmployeeNameUseCase("Bob", null)

        assertFalse(result.successful)
        assertEquals(EmployeeTestTags.EMPLOYEE_NAME_LENGTH_ERROR, result.errorMessage)
    }

    @Test
    fun `invoke returns error when name contains digits`() = runTest {
        val result = validateEmployeeNameUseCase("John123", null)

        assertFalse(result.successful)
        assertEquals(EmployeeTestTags.EMPLOYEE_NAME_DIGIT_ERROR, result.errorMessage)
    }

    @Test
    fun `invoke returns error when name already exists`() = runTest {
        val data = repository.createTestItem()

        val result = validateEmployeeNameUseCase(data.employeeName, null)

        assertFalse(result.successful)
        assertEquals(EmployeeTestTags.EMPLOYEE_NAME_ALREADY_EXIST_ERROR, result.errorMessage)
    }

    @Test
    fun `invoke returns success for valid name`() = runTest {
        val data = repository.createTestItem()

        val result = validateEmployeeNameUseCase(data.employeeName, data.employeeId)

        assertTrue(result.successful)
        assertEquals(null, result.errorMessage)
    }
}
