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

package com.niyaj.feature.home

import app.cash.turbine.test
import com.niyaj.testing.repository.TestCartRepository
import com.niyaj.testing.repository.TestHomeRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.CardOrderPreviewData
import com.niyaj.ui.parameterProvider.CartPreviewParameterData
import com.niyaj.ui.parameterProvider.CategoryPreviewData
import com.niyaj.ui.parameterProvider.ProductPreviewData
import com.niyaj.ui.utils.UiEvent
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class HomeViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestHomeRepository()
    private val cartRepository = TestCartRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private lateinit var viewModel: HomeViewModel

    private val productList = ProductPreviewData.emptyProductQtyList.toImmutableList()
    private val categoryList = CategoryPreviewData.categoryList
    private val cartProducts = ProductPreviewData.cartProducts
    private val cartItem = CartPreviewParameterData.sampleDineInCart

    @Before
    fun setup() {
        viewModel = HomeViewModel(
            repository = repository,
            cartRepository = cartRepository,
            ioDispatcher = UnconfinedTestDispatcher(),
            analyticsHelper = analyticsHelper,
        )
    }

    @Test
    fun checkInitialSelectedOrderIsEmpty() = runTest {
        assertEquals(SelectedOrderState(), viewModel.selectedId.value)
    }

    @Test
    fun selectOrder_whenOrderExists_populateSelectedOrder() = runTest {
        val job = launch { viewModel.selectedId.collect() }

        val cartOrder = CardOrderPreviewData.sampleDineInOrder
        repository.updateCartOrders(listOf(cartOrder))
        repository.updateSelectedOrder(cartOrder.orderId)

        advanceUntilIdle()

        assertEquals(cartOrder.orderId, viewModel.selectedId.value.orderId)
        assertEquals(cartOrder.address.addressName, viewModel.selectedId.value.addressName)

        job.cancel()
    }

    @Test
    fun selectedCategory_initialIsZero() = runTest {
        assertEquals(0, viewModel.selectedCategory.value)
    }

    @Test
    fun selectedCategory_update_whenCategoryChanged() = runTest {
        viewModel.selectCategory(1)
        assertEquals(1, viewModel.selectedCategory.value)
    }

    @Test
    fun productsWithQuantity_initialIsLoading() = runTest {
        assertEquals(UiState.Loading, viewModel.productsWithQuantity.value)
    }

    @Test
    fun productWithQuantity_empty_whenNoProductsFound() = runTest {
        val job = launch { viewModel.productsWithQuantity.collect() }

        advanceUntilIdle()

        viewModel.productsWithQuantity.test {
            assertEquals(UiState.Empty, awaitItem())
        }

        job.cancel()
    }

    @Test
    fun productWithQuantity_populate_whenProductsAvailable() = runTest {
        repository.updateProductList(productList)

        viewModel.productsWithQuantity.test {
            assertEquals(awaitItem(), UiState.Success(productList))
        }
    }

    @Test
    fun categories_initialIsLoading() = runTest {
        assertEquals(UiState.Loading, viewModel.categories.value)
    }

    @Test
    fun categories_empty_whenNoCategoriesFound() = runTest {
        val job = launch { viewModel.categories.collect() }
        advanceUntilIdle()

        viewModel.categories.test {
            assertEquals(UiState.Empty, awaitItem())
        }

        job.cancel()
    }

    @Test
    fun categories_populate_whenCategoriesAvailable() = runTest {
        repository.updateCategoryList(categoryList)

        viewModel.categories.test {
            assertEquals(UiState.Success(categoryList), awaitItem())
        }
    }

    @Test
    fun addProductToCart_whenProductAdded_increaseProductQuantity() = runTest {
        cartRepository.updateCartItems(listOf(cartItem))
        repository.updateProductList(productList)
        cartRepository.updateCartProducts(cartProducts)

        viewModel.addProductToCart(cartItem.orderId, productList.first().productId)
        viewModel.addProductToCart(cartItem.orderId, productList.component2().productId)
        viewModel.addProductToCart(cartItem.orderId, productList.first().productId)

        val price = (cartProducts.first().productPrice * 2) + cartProducts.component2().productPrice

        viewModel.eventFlow.test {
            assertEquals(UiEvent.OnSuccess("Product added to cart"), awaitItem())
        }

        cartRepository.getCartItemByOrderId(cartItem.orderId).test {
            val item = awaitItem()
            assertEquals(2, item.cartProducts.size)
            assertEquals(price.toLong(), item.orderPrice)
            assertEquals(1, item.cartProducts.component2().productQuantity)
            assertEquals(2, item.cartProducts.component1().productQuantity)
        }
    }

    @Test
    fun removeProductFromCart_whenProductRemoved_decreaseProductQuantity() = runTest {
        cartRepository.updateCartItems(listOf(cartItem))
        repository.updateProductList(productList)
        cartRepository.updateCartProducts(cartProducts)

        viewModel.addProductToCart(cartItem.orderId, productList.first().productId)
        viewModel.addProductToCart(cartItem.orderId, productList.component2().productId)
        viewModel.addProductToCart(cartItem.orderId, productList.first().productId)

        val price = (cartProducts.first().productPrice * 2) + cartProducts.component2().productPrice

        viewModel.eventFlow.test {
            assertEquals(UiEvent.OnSuccess("Product added to cart"), awaitItem())
        }

        cartRepository.getCartItemByOrderId(cartItem.orderId).test {
            val item = awaitItem()
            assertEquals(2, item.cartProducts.size)
            assertEquals(price.toLong(), item.orderPrice)
            assertEquals(2, item.cartProducts.first().productQuantity)
            assertEquals(1, item.cartProducts.component2().productQuantity)
        }

        viewModel.removeProductFromCart(cartItem.orderId, productList.first().productId)
        viewModel.removeProductFromCart(cartItem.orderId, productList.component2().productId)

        viewModel.eventFlow.test {
            assertEquals(UiEvent.OnSuccess("Product removed from cart"), awaitItem())
        }

        val removedPrice = cartProducts.first().productPrice

        cartRepository.getCartItemByOrderId(cartItem.orderId).test {
            val item = awaitItem()
            assertEquals(1, item.cartProducts.size)
            assertEquals(removedPrice.toLong(), item.orderPrice)
            assertEquals(1, item.cartProducts.first().productQuantity)
        }
    }
}
