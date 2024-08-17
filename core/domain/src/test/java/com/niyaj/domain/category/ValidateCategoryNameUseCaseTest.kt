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

package com.niyaj.domain.category

import com.niyaj.common.tags.CategoryConstants
import com.niyaj.testing.repository.TestCategoryRepository
import com.niyaj.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test

class ValidateCategoryNameUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestCategoryRepository()

    private lateinit var validateCategoryNameUseCase: ValidateCategoryNameUseCase

    @Before
    fun setup() {
        validateCategoryNameUseCase = ValidateCategoryNameUseCase(
            repository = repository,
            ioDispatcher = UnconfinedTestDispatcher(),
        )
    }

    @Test
    fun `empty category name, when validating, then return error`() = runTest {
        val result = validateCategoryNameUseCase("")

        assertFalse(result.successful)
        assertEquals(CategoryConstants.CATEGORY_NAME_EMPTY_ERROR, result.errorMessage)
    }

    @Test
    fun `category name shorter than 3 characters, when validating, then return error`() = runTest {
        val result = validateCategoryNameUseCase("ab")

        assertFalse(result.successful)
        assertEquals(CategoryConstants.CATEGORY_NAME_LENGTH_ERROR, result.errorMessage)
    }

    @Test
    fun `existing category name, when validating, then return error`() = runTest {
        val data = repository.createTestCategory()
        val result = validateCategoryNameUseCase(data.categoryName)

        assertFalse(result.successful)
        assertEquals(CategoryConstants.CATEGORY_NAME_ALREADY_EXIST_ERROR, result.errorMessage)
    }

    @Test
    fun `valid new category name, when validating, then return success`() = runTest {
        val result = validateCategoryNameUseCase("New Category")

        assertTrue(result.successful)
        assertEquals(null, result.errorMessage)
    }

    @Test
    fun `valid existing category name with matching id, when validating, then return success`() =
        runTest {
            val data = repository.createTestCategory()

            val result =
                validateCategoryNameUseCase(data.categoryName, categoryId = data.categoryId)

            assertTrue(result.successful)
            assertEquals(null, result.errorMessage)
        }
}
