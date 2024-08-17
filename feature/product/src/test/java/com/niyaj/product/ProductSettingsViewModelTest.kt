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

package com.niyaj.product

import app.cash.turbine.test
import com.niyaj.model.searchProducts
import com.niyaj.product.settings.ProductSettingsEvent
import com.niyaj.product.settings.ProductSettingsViewModel
import com.niyaj.testing.repository.TestProductRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.parameterProvider.ProductPreviewData
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

class ProductSettingsViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = ProductPreviewData.productList
    private val repository = TestProductRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private lateinit var viewModel: ProductSettingsViewModel

    @Before
    fun setup() {
        viewModel = ProductSettingsViewModel(repository, analyticsHelper)
    }

    @Test
    fun `when search text changes, charges are updated`() = runTest {
        repository.setProductList(itemList)

        viewModel.searchTextChanged("Chicken Biryani")
        advanceUntilIdle()

        viewModel.products.test {
            assertEquals(itemList.searchProducts("Chicken Biryani"), awaitItem())
        }
    }

    @Test
    fun `when GetExportedProduct event is triggered with no selection, all items are exported`() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.products.collect() }

            repository.setProductList(itemList)

            viewModel.onEvent(ProductSettingsEvent.GetExportedProduct)
            testScheduler.advanceUntilIdle()

            viewModel.exportedProducts.test {
                assertEquals(itemList, awaitItem())
            }

            job.cancel()
        }

    @Test
    fun `when GetExportedItems event is triggered with selection, only selected items are exported`() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.products.collect() }

            repository.setProductList(itemList)

            viewModel.selectItem(1)
            viewModel.selectItem(3)

            viewModel.onEvent(ProductSettingsEvent.GetExportedProduct)
            advanceUntilIdle()

            assertEquals(
                itemList.filter { it.productId == 1 || it.productId == 3 },
                viewModel.exportedProducts.value,
            )

            job.cancel()
        }

    @Test
    fun `when OnImportProductsFromFile event is triggered, importedItems are updated`() =
        runTest {
            viewModel.onEvent(ProductSettingsEvent.OnImportProductsFromFile(itemList))
            testScheduler.advanceUntilIdle()

            assertEquals(itemList, viewModel.importedProducts.value)
        }

    @Test
    fun `when ImportProductsToDatabase event is triggered with no selection, all imported items are added`() =
        runTest {
            val job = launch { viewModel.products.collect() }

            viewModel.onEvent(ProductSettingsEvent.OnImportProductsFromFile(itemList))
            assertEquals(itemList, viewModel.importedProducts.value)
            viewModel.onEvent(ProductSettingsEvent.ImportProductsToDatabase)
            advanceUntilIdle()

            viewModel.eventFlow.test {
                val event = awaitItem()
                assertTrue(event is UiEvent.OnSuccess)
                assertEquals(
                    "${itemList.size} Products has been imported.",
                    (event as UiEvent.OnSuccess).successMessage,
                )
            }

            advanceUntilIdle()

            viewModel.products.test {
                assertEquals(itemList, awaitItem())
            }

            job.cancel()
        }

    @Test
    fun `when ImportProductsToDatabase event is triggered with selection, only selected items are added`() =
        runTest {
            val job = launch { viewModel.products.collect() }

            viewModel.onEvent(ProductSettingsEvent.OnImportProductsFromFile(itemList))

            assertEquals(itemList, viewModel.importedProducts.value)

            viewModel.selectItem(2)
            viewModel.selectItem(4)

            viewModel.onEvent(ProductSettingsEvent.ImportProductsToDatabase)
            advanceUntilIdle()

            viewModel.eventFlow.test {
                val event = awaitItem()
                assertTrue(event is UiEvent.OnSuccess)
                assertEquals(
                    "2 Products has been imported.",
                    (event as UiEvent.OnSuccess).successMessage,
                )
            }

            advanceUntilIdle()

            viewModel.products.test {
                val event = awaitItem()
                assertEquals(itemList.filter { it.productId == 2 || it.productId == 4 }, event)
            }

            job.cancel()
        }
}
