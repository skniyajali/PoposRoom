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
import com.niyaj.domain.employee.ValidateEmployeeSalaryUseCase
import com.niyaj.testing.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test

class ValidateEmployeeSalaryUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var validateEmployeeSalaryUseCase: ValidateEmployeeSalaryUseCase

    @Before
    fun setup() {
        validateEmployeeSalaryUseCase = ValidateEmployeeSalaryUseCase()
    }

    @Test
    fun `invoke returns error when salary is empty`() {
        val result = validateEmployeeSalaryUseCase("")

        assertFalse(result.successful)
        assertEquals(EmployeeTestTags.EMPLOYEE_SALARY_EMPTY_ERROR, result.errorMessage)
    }

    @Test
    fun `invoke returns error when salary length is not 5 characters`() {
        val result = validateEmployeeSalaryUseCase("1234")

        assertFalse(result.successful)
        assertEquals(EmployeeTestTags.EMPLOYEE_SALARY_LENGTH_ERROR, result.errorMessage)
    }

    @Test
    fun `invoke returns error when salary contains letters`() {
        val result = validateEmployeeSalaryUseCase("12a45")

        assertFalse(result.successful)
        assertEquals(EmployeeTestTags.EMPLOYEE_SALARY_LETTER_ERROR, result.errorMessage)
    }

    @Test
    fun `invoke returns success for valid salary`() {
        val result = validateEmployeeSalaryUseCase("12345")

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `invoke returns error for salary with more than 5 digits`() {
        val result = validateEmployeeSalaryUseCase("123456")

        assertFalse(result.successful)
        assertEquals(EmployeeTestTags.EMPLOYEE_SALARY_LENGTH_ERROR, result.errorMessage)
    }

    @Test
    fun `invoke returns error for salary with special characters`() {
        val result = validateEmployeeSalaryUseCase("12$45")

        assertFalse(result.successful)
        assertEquals(EmployeeTestTags.EMPLOYEE_SALARY_LETTER_ERROR, result.errorMessage)
    }
}
