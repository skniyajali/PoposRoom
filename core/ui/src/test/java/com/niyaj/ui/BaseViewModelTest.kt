/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.ui

import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.ui.event.BaseViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BaseViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val viewModel = BaseViewModel()

    @Test
    fun `selectItem adds item when not present`() = runTest {
        viewModel.selectItem(1)
        assertTrue(viewModel.selectedItems.contains(1))
    }

    @Test
    fun `selectItem removes item when already present`() = runTest {
        viewModel.selectItem(1)
        viewModel.selectItem(1)
        assertFalse(viewModel.selectedItems.contains(1))
    }

    @Test
    fun `selectAllItems selects all items when none are selected`() = runTest {
        viewModel.totalItems = listOf(1, 2, 3)
        viewModel.selectAllItems()
        assertEquals(viewModel.totalItems.size, viewModel.selectedItems.size)
    }

    @Test
    fun `selecting all items should deselect all when all are selected`() = runTest {
        viewModel.totalItems = listOf(1, 2, 3)
        viewModel.selectAllItems()
        viewModel.selectAllItems()
        assertTrue(viewModel.selectedItems.isEmpty())
    }

    @Test
    fun `deselecting items should clear all selected items`() = runTest {
        viewModel.selectItem(1)
        viewModel.selectItem(2)
        viewModel.deselectItems()
        assertTrue(viewModel.selectedItems.isEmpty())
    }

    @Test
    fun `opening search bar should set showSearchBar to true`() = runTest {
        viewModel.openSearchBar()
        assertTrue(viewModel.showSearchBar.first())
    }

    @Test
    fun `changing search text should update search text value`() = runTest {
        val newText = "search query"
        viewModel.searchTextChanged(newText)
        assertEquals(newText, viewModel.searchText.value)
    }

    @Test
    fun `clearing search text should set search text to empty string`() = runTest {
        viewModel.searchTextChanged("some text")
        viewModel.clearSearchText()
        assertTrue(viewModel.searchText.value.isEmpty())
    }

    @Test
    fun `closing search bar should set showSearchBar to false and clear search text`() = runTest {
        viewModel.openSearchBar()
        viewModel.searchTextChanged("some text")
        viewModel.closeSearchBar()
        assertFalse(viewModel.showSearchBar.value)
        assertTrue(viewModel.searchText.value.isEmpty())
    }
}
