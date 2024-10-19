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

package com.niyaj.feature.product

import app.cash.turbine.test
import com.niyaj.model.searchProducts
import com.niyaj.testing.repository.TestProductRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.CategoryPreviewData
import com.niyaj.ui.parameterProvider.ProductPreviewData
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
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

class ProductViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = ProductPreviewData.productList
    private val repository = TestProductRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private lateinit var viewModel: ProductViewModel

    @Before
    fun setup() {
        viewModel = ProductViewModel(repository, analyticsHelper)
    }

    @Test
    fun productsState_initially_Loading() = runTest {
        assertEquals(UiState.Loading, viewModel.products.value)
    }

    @Test
    fun productState_isEmpty_whenDataIsEmpty() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.products.collect() }

        assertEquals(UiState.Empty, viewModel.products.value)

        job.cancel()
    }

    @Test
    fun productState_isSuccess_whenDataIsAvailable() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.products.collect() }
        repository.setProductList(itemList)

        viewModel.products.test {
            assertEquals(UiState.Success(itemList), awaitItem())
        }

        job.cancel()
    }

    @Test
    fun category_isEmpty_whenDataIsEmpty() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.categories.collect() }

        assertEquals(persistentListOf(), viewModel.categories.value)

        job.cancel()
    }

    @Test
    fun category_available_whenDataIsAvailable() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.categories.collect() }
        val categoryList = CategoryPreviewData.categoryList
        repository.setCategoryList(categoryList)

        assertEquals(categoryList.toImmutableList(), viewModel.categories.value)

        job.cancel()
    }

    @Test
    fun onSelectCategory_shouldUpdateSelectedCategory() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.selectedCategory.collect() }
        val category = CategoryPreviewData.categoryList.first()

        viewModel.selectCategory(category.categoryId)

        assertEquals(category.categoryId, viewModel.selectedCategory.value)

        job.cancel()
    }

    @Test
    fun onSelectCategory_shouldUpdateProducts() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.products.collect() }
        val category = CategoryPreviewData.categoryList.first()
        val productList = ProductPreviewData.productList.filter { it.categoryId == category.categoryId }

        repository.setProductList(itemList)
        viewModel.selectCategory(category.categoryId)

        assertEquals(UiState.Success(productList), viewModel.products.value)

        job.cancel()
    }

    @Test
    fun onSelectExistingCategory_shouldUpdateProductsAndDeselectSelectedCategory() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.products.collect() }
        val job2 = launch(UnconfinedTestDispatcher()) { viewModel.selectedCategory.collect() }

        val category = CategoryPreviewData.categoryList.first()
        val productList = ProductPreviewData.productList.filter { it.categoryId == category.categoryId }

        repository.setProductList(itemList)
        viewModel.selectCategory(category.categoryId)

        assertEquals(UiState.Success(productList), viewModel.products.value)

        viewModel.selectCategory(category.categoryId)
        assertEquals(0, viewModel.selectedCategory.value)
        assertEquals(UiState.Success(itemList), viewModel.products.value)

        job.cancel()
        job2.cancel()
    }

    @Test
    fun search_withInvalidData_returnEmptyResult() = runTest {
        repository.setProductList(itemList)
        viewModel.searchTextChanged("Invalid")

        advanceUntilIdle()

        viewModel.products.test {
            assertEquals(UiState.Empty, awaitItem())
        }
    }

    @Test
    fun search_withValidData_returnResult() = runTest {
        repository.setProductList(itemList)
        viewModel.searchTextChanged("Chicken Biryani")

        advanceUntilIdle()

        viewModel.products.test {
            assertEquals(
                UiState.Success(
                    itemList.searchProducts("Chicken Biryani"),
                ),
                awaitItem(),
            )
        }
    }

    @Test
    fun search_onClose_returnAllData() = runTest {
        repository.setProductList(itemList)
        viewModel.searchTextChanged("Extra")

        advanceUntilIdle()

        viewModel.products.test {
            assertEquals(UiState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.closeSearchBar()
        advanceUntilIdle()

        viewModel.products.test {
            assertEquals(UiState.Success(itemList), awaitItem())
        }
    }

    @Test
    fun deleteProducts_shouldRemoveFromList() = runTest {
        repository.setProductList(itemList)
        val productId = itemList.first().productId

        viewModel.selectItem(productId)
        viewModel.deleteItems()

        advanceUntilIdle()

        viewModel.products.test {
            assertEquals(UiState.Success(itemList.drop(1)), awaitItem())
        }

        assert(productId !in viewModel.selectedItems.toList())
    }

    @Test
    fun selectProducts_onSelectItem_shouldUpdateSelectedProducts() = runTest {
        repository.setProductList(itemList)

        val productId = itemList.first().productId
        viewModel.selectItem(productId)

        advanceUntilIdle()

        assertContains(viewModel.selectedItems.toList(), productId)
    }

    @Test
    fun selectAllItems_onSelectAllItems_shouldUpdateSelectedItems() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.products.collect() }

        repository.setProductList(itemList)
        viewModel.selectAllItems()

        advanceUntilIdle()

        assertEquals(itemList.map { it.productId }, viewModel.selectedItems.toList())

        job.cancel()
    }

    @Test
    fun deselectItems_onDeselectItems_shouldClearSelectedItems() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.products.collect() }
        repository.setProductList(itemList)

        viewModel.selectAllItems()
        assertEquals(viewModel.selectedItems.toList(), itemList.map { it.productId })

        advanceUntilIdle()

        viewModel.deselectItems()

        advanceUntilIdle()

        assertEquals(emptyList(), viewModel.selectedItems.toList())

        job.cancel()
    }
}
