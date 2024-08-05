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
import com.niyaj.category.settings.CategorySettingsEvent
import com.niyaj.category.settings.CategorySettingsViewModel
import com.niyaj.model.searchCategory
import com.niyaj.testing.repository.TestCategoryRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.parameterProvider.CategoryPreviewData
import com.niyaj.ui.utils.UiEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class CategorySettingsViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = CategoryPreviewData.categoryList
    private val repository = TestCategoryRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private lateinit var viewModel: CategorySettingsViewModel

    @Before
    fun setup() {
        viewModel = CategorySettingsViewModel(repository, analyticsHelper)
    }

    @Test
    fun `when search text changes, categories are updated`() = runTest {
        repository.updateCategoryData(itemList)

        viewModel.searchTextChanged("Books")
        advanceUntilIdle()

        viewModel.categories.test {
            assertEquals(itemList.searchCategory("Books"), awaitItem())
        }
    }

    @Test
    fun `when GetExportedItems event is triggered with no selection, all items are exported`() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.categories.collect() }

            repository.updateCategoryData(itemList)

            viewModel.onEvent(CategorySettingsEvent.GetExportedCategory)
            testScheduler.advanceUntilIdle()

            viewModel.exportedCategories.test {
                assertEquals(itemList, awaitItem())
            }

            job.cancel()
        }

    @Test
    fun `when GetExportedItems event is triggered with selection, only selected items are exported`() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.categories.collect() }

            repository.updateCategoryData(itemList)

            viewModel.selectItem(1)
            viewModel.selectItem(3)

            viewModel.onEvent(CategorySettingsEvent.GetExportedCategory)
            advanceUntilIdle()

            assertEquals(
                itemList.filter { it.categoryId == 1 || it.categoryId == 3 },
                viewModel.exportedCategories.value,
            )

            job.cancel()
        }

    @Test
    fun `when OnImportCategoriesFromFile event is triggered, importedItems are updated`() =
        runTest {
            viewModel.onEvent(CategorySettingsEvent.OnImportCategoriesFromFile(itemList))
            testScheduler.advanceUntilIdle()

            assertEquals(itemList, viewModel.importedCategories.value)
        }

    @Test
    fun `when ImportCategoriesToDatabase event is triggered with no selection, all imported items are added`() =
        runTest {
            val job = launch { viewModel.categories.collect() }

            viewModel.onEvent(CategorySettingsEvent.OnImportCategoriesFromFile(itemList))
            assertEquals(itemList, viewModel.importedCategories.value)
            viewModel.onEvent(CategorySettingsEvent.ImportCategoriesToDatabase)
            advanceUntilIdle()

            viewModel.eventFlow.test {
                val event = awaitItem()
                assertTrue(event is UiEvent.OnSuccess)
                assertEquals(
                    "${itemList.size} categories has been imported successfully",
                    (event as UiEvent.OnSuccess).successMessage,
                )
            }

            advanceUntilIdle()

            viewModel.categories.test {
                assertEquals(
                    itemList,
                    awaitItem(),
                )
            }

            job.cancel()
        }

    @Test
    fun `when ImportCategoriesToDatabase event is triggered with selection, only selected items are added`() =
        runTest {
            val job = launch { viewModel.categories.collect() }

            viewModel.onEvent(CategorySettingsEvent.OnImportCategoriesFromFile(itemList))

            assertEquals(itemList, viewModel.importedCategories.value)

            viewModel.selectItem(2)
            viewModel.selectItem(4)

            viewModel.onEvent(CategorySettingsEvent.ImportCategoriesToDatabase)
            advanceUntilIdle()

            viewModel.eventFlow.test {
                val event = awaitItem()
                assertTrue(event is UiEvent.OnSuccess)
                assertEquals(
                    "2 categories has been imported successfully",
                    (event as UiEvent.OnSuccess).successMessage,
                )
            }

            advanceUntilIdle()

            viewModel.categories.test {
                val event = awaitItem()
                assertEquals(itemList.filter { it.categoryId == 2 || it.categoryId == 4 }, event)
            }

            job.cancel()
        }
}
