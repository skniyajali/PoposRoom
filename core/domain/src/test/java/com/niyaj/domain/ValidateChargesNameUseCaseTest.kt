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

import com.niyaj.common.tags.ChargesTestTags
import com.niyaj.domain.charges.ValidateChargesNameUseCase
import com.niyaj.testing.repository.TestChargesRepository
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

class ValidateChargesNameUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestChargesRepository()
    private lateinit var useCase: ValidateChargesNameUseCase

    @Before
    fun setup() {
        useCase = ValidateChargesNameUseCase(repository, UnconfinedTestDispatcher())
    }

    @Test
    fun `empty charges name, when validating, then return error`() = runTest {
        val result = useCase("")

        assertFalse(result.successful)
        assertEquals(ChargesTestTags.CHARGES_NAME_EMPTY_ERROR, result.errorMessage)
    }

    @Test
    fun `charges name shorter than 5 characters, when validating, then return error`() = runTest {
        val result = useCase("Abed")

        assertFalse(result.successful)
        assertEquals(ChargesTestTags.CHARGES_NAME_LENGTH_ERROR, result.errorMessage)
    }

    @Test
    fun `charges name containing digits, when validating, then return error`() = runTest {
        val result = useCase("Charge1")

        assertFalse(result.successful)
        assertEquals(ChargesTestTags.CHARGES_NAME_DIGIT_ERROR, result.errorMessage)
    }

    @Test
    fun `existing charges name, when validating, then return error`() = runTest {
        val data = repository.createTestCharges()

        val result = useCase(data.chargesName)

        assertFalse(result.successful)
        assertEquals(ChargesTestTags.CHARGES_NAME_ALREADY_EXIST_ERROR, result.errorMessage)
    }

    @Test
    fun `valid charges name, when validating, then return success`() = runTest {
        val result = useCase("Valid New Charge")

        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `valid existing charges name with matching id, when validating, then return success`() =
        runTest {
            val data = repository.createTestCharges()

            val result = useCase(data.chargesName, chargesId = data.chargesId)

            assertTrue(result.successful)
            assertNull(result.errorMessage)
        }
}
