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

import com.niyaj.common.tags.AbsentScreenTags.ABSENT_DATE_EMPTY
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_DATE_EXIST
import com.niyaj.common.utils.getDateInMilliseconds
import com.niyaj.common.utils.getStartTime
import com.niyaj.domain.absent.ValidateAbsentDateUseCase
import com.niyaj.testing.repository.TestAbsentRepository
import com.niyaj.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertNull

class ValidateAbsentDateUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var validateAbsentDateUseCase: ValidateAbsentDateUseCase
    private lateinit var testAbsentRepository: TestAbsentRepository

    @Before
    fun setup() {
        testAbsentRepository = TestAbsentRepository()
        validateAbsentDateUseCase = ValidateAbsentDateUseCase(
            absentRepository = testAbsentRepository,
            ioDispatcher = UnconfinedTestDispatcher(),
        )
    }

    @Test
    fun `invoke should return false when absentDate is empty`() = runTest {
        val result = validateAbsentDateUseCase("")

        assertFalse(result.successful)
        assertEquals(ABSENT_DATE_EMPTY, result.errorMessage)
    }

    @Test
    fun `invoke should return true when employeeId is null`() = runTest {
        val result = validateAbsentDateUseCase(getStartTime)

        assertTrue(result.successful)
    }

    @Test
    fun `invoke should return false when employee is absent on given date`() = runTest {
        val data = testAbsentRepository.createTestData()

        val result = validateAbsentDateUseCase(data.absentDate, employeeId = data.employeeId)

        assertFalse(result.successful)
        assertEquals(ABSENT_DATE_EXIST, result.errorMessage)
    }

    @Test
    fun `invoke should return true when employee is not absent on given date`() = runTest {
        val result =
            validateAbsentDateUseCase(getDateInMilliseconds(hour = 10, day = 2), employeeId = 1)

        assertTrue(result.successful)
    }

    @Test
    fun `invoke should pass absentId to repository when provided`() = runTest {
        val data = testAbsentRepository.createTestData()

        val result = validateAbsentDateUseCase(
            data.absentDate,
            employeeId = data.employeeId,
            absentId = data.absentId,
        )

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }
}
