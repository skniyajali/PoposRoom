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

package com.niyaj.category

import app.cash.turbine.test
import com.niyaj.model.searchCategory
import com.niyaj.testing.repository.TestCategoryRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.CategoryPreviewData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class CategoryViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = CategoryPreviewData.categoryList
    private val repository = TestCategoryRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private lateinit var viewModel: CategoryViewModel

    @Before
    fun setup() {
        viewModel = CategoryViewModel(repository, analyticsHelper)
    }

    @Test
    fun categoryState_initially_Loading() = runTest {
        assertEquals(
            UiState.Loading,
            viewModel.categories.value,
        )
    }

    @Test
    fun categoryState_isEmpty_whenDataIsEmpty() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.categories.collect() }

        assertEquals(
            UiState.Empty,
            viewModel.categories.value,
        )

        job.cancel()
    }

    @Test
    fun categoryState_isSuccess_whenDataIsAvailable() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.categories.collect() }
        repository.updateCategoryData(itemList)

        assertEquals(
            UiState.Success(itemList),
            viewModel.categories.value,
        )

        job.cancel()
    }

    @Test
    fun search_withInvalidData_returnEmptyResult() = runTest {
        repository.updateCategoryData(itemList)
        viewModel.searchTextChanged("Invalid")

        advanceUntilIdle()

        viewModel.categories.test {
            assertEquals(UiState.Empty, awaitItem())
        }
    }

    @Test
    fun search_withValidData_returnResult() = runTest {
        repository.updateCategoryData(itemList)
        viewModel.searchTextChanged("Books")

        advanceUntilIdle()

        viewModel.categories.test {
            assertEquals(UiState.Success(itemList.searchCategory("Books")), awaitItem())
        }
    }

    @Test
    fun search_onClose_returnAllData() = runTest {
        repository.updateCategoryData(itemList)
        viewModel.searchTextChanged("Address")

        advanceUntilIdle()

        viewModel.categories.test {
            assertEquals(UiState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.closeSearchBar()
        advanceUntilIdle()

        viewModel.categories.test {
            assertEquals(UiState.Success(itemList), awaitItem())
        }
    }

    @Test
    fun deleteCategory_shouldRemoveFromList() = runTest {
        repository.updateCategoryData(itemList)
        val categoryId = itemList.first().categoryId

        viewModel.selectItem(categoryId)
        viewModel.deleteItems()

        advanceUntilIdle()

        viewModel.categories.test {
            assertEquals(UiState.Success(itemList.drop(1)), awaitItem())
        }

        assert(categoryId !in viewModel.selectedItems.toList())
    }

    @Test
    fun selectCategory_onSelectItem_shouldUpdateSelectedCategory() = runTest {
        repository.updateCategoryData(itemList)

        val categoryId = itemList.first().categoryId
        viewModel.selectItem(categoryId)

        advanceUntilIdle()

        assertContains(viewModel.selectedItems.toList(), categoryId)
    }

    @Test
    fun selectAllItems_onSelectAllItems_shouldUpdateSelectedItems() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.categories.collect() }

        repository.updateCategoryData(itemList)
        viewModel.selectAllItems()

        advanceUntilIdle()

        assertEquals(itemList.map { it.categoryId }, viewModel.selectedItems.toList())

        job.cancel()
    }

    @Test
    fun deselectItems_onDeselectItems_shouldClearSelectedItems() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.categories.collect() }
        repository.updateCategoryData(itemList)

        viewModel.selectAllItems()
        assertEquals(viewModel.selectedItems.toList(), itemList.map { it.categoryId })

        advanceUntilIdle()

        viewModel.deselectItems()

        advanceUntilIdle()

        assertEquals(emptyList(), viewModel.selectedItems.toList())

        job.cancel()
    }
}
