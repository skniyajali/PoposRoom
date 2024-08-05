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

import com.niyaj.common.tags.AddOnTestTags
import com.niyaj.common.tags.AddOnTestTags.ADDON_NAME_EMPTY_ERROR
import com.niyaj.common.tags.AddOnTestTags.ADDON_WHITELIST_ITEM
import com.niyaj.domain.addonitem.ValidateItemNameUseCase
import com.niyaj.testing.repository.TestAddOnItemRepository
import com.niyaj.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import kotlin.test.Test

class ValidateItemNameUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestAddOnItemRepository()

    private val validateItemNameUseCase = ValidateItemNameUseCase(
        addOnItemRepository = repository,
        ioDispatcher = UnconfinedTestDispatcher(),
    )

    @Test
    fun `item name is empty returns error`() = runTest {
        val result = validateItemNameUseCase("", null)
        assertFalse(result.successful)
        assertEquals(ADDON_NAME_EMPTY_ERROR, result.errorMessage)
    }

    @Test
    fun `item name shorter than 5 characters returns error`() = runTest {
        val result = validateItemNameUseCase("Test", null)
        assertFalse(result.successful)
        assertEquals(AddOnTestTags.ADDON_NAME_LENGTH_ERROR, result.errorMessage)
    }

    @Test
    fun `name with digits returns error when not starting with whitelist item`() = runTest {
        val result = validateItemNameUseCase("Test123", null)
        assertFalse(result.successful)
        assertEquals(AddOnTestTags.ADDON_NAME_DIGIT_ERROR, result.errorMessage)
    }

    @Test
    fun `name already exists returns error`() = runTest {
        val item = repository.createTestItem()
        val result = validateItemNameUseCase(item.itemName, null)
        assertFalse(result.successful)
        assertEquals(AddOnTestTags.ADDON_NAME_ALREADY_EXIST_ERROR, result.errorMessage)
    }

    @Test
    fun `valid name returns successful result`() = runTest {
        val item = repository.createTestItem()
        val result = validateItemNameUseCase(item.itemName, item.itemId)
        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `name starting with whitelist item is valid even with digits`() = runTest {
        val result = validateItemNameUseCase("${ADDON_WHITELIST_ITEM}123", null)
        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }
}
