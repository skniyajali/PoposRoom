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

import androidx.compose.ui.util.fastFirst
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.niyaj.common.result.Resource
import com.niyaj.common.tags.ProductTestTags.PRODUCT_CATEGORY_EMPTY_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_NAME_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_NAME_EMPTY_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_NAME_LENGTH_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_PRICE_EMPTY_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_PRICE_LENGTH_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_TAG_LENGTH_ERROR
import com.niyaj.common.utils.capitalizeWords
import com.niyaj.domain.product.ValidateProductCategoryUseCase
import com.niyaj.domain.product.ValidateProductNameUseCase
import com.niyaj.domain.product.ValidateProductPriceUseCase
import com.niyaj.domain.product.ValidateProductTagUseCase
import com.niyaj.feature.product.createOrUpdate.AddEditProductEvent
import com.niyaj.feature.product.createOrUpdate.AddEditProductViewModel
import com.niyaj.model.Category
import com.niyaj.testing.repository.TestProductRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.parameterProvider.CategoryPreviewData
import com.niyaj.ui.parameterProvider.ProductPreviewData
import com.niyaj.ui.utils.UiEvent
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AddEditProductViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = ProductPreviewData.productList
    private val repository = TestProductRepository()
    private val analyticsHelper = TestAnalyticsHelper()

    private val validateProductCategory = ValidateProductCategoryUseCase()
    private val validateProductName = ValidateProductNameUseCase(
        repository = repository,
        ioDispatcher = UnconfinedTestDispatcher(),
    )
    private val validateProductTag = ValidateProductTagUseCase()
    private val validateProductPrice = ValidateProductPriceUseCase()
    private lateinit var viewModel: AddEditProductViewModel

    @BeforeTest
    fun setup() {
        viewModel = AddEditProductViewModel(
            productRepository = repository,
            validateProductCategory = validateProductCategory,
            validateProductName = validateProductName,
            validateProductTag = validateProductTag,
            validateProductPrice = validateProductPrice,
            analyticsHelper = analyticsHelper,
            savedStateHandle = SavedStateHandle(),
        )
    }

    @Test
    fun `categories should empty when no data present`() = runTest {
        viewModel.categories.test {
            assertEquals(emptyList(), awaitItem())
        }
    }

    @Test
    fun `categories should not empty when data present`() = runTest {
        val categories = CategoryPreviewData.categoryList
        repository.setCategoryList(categories)

        viewModel.categories.test {
            assertEquals(categories, awaitItem())
        }
    }

    @Test
    fun `initialize with valid productId should populated product details`() = runTest {
        val product = repository.createTestProduct()
        val categories = CategoryPreviewData.categoryList.filter {
            it.categoryId == product.categoryId
        }

        repository.setCategoryList(categories)
        viewModel.setProductId(product.productId)

        val savedStateHandle = SavedStateHandle()
        savedStateHandle["productId"] = product.productId

        viewModel = AddEditProductViewModel(
            productRepository = repository,
            validateProductCategory = validateProductCategory,
            validateProductName = validateProductName,
            validateProductTag = validateProductTag,
            validateProductPrice = validateProductPrice,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )

        assertEquals(product.productName, viewModel.state.productName)
        assertEquals(product.productPrice.toString(), viewModel.state.productPrice)
        assertEquals(product.productDescription, viewModel.state.productDesc)
        assertEquals(product.productAvailability, viewModel.state.productAvailability)

        assert(viewModel.selectedTags.containsAll(product.tags))
        viewModel.selectedCategory.test {
            assertEquals(categories.first(), awaitItem())
        }
    }

    @Test
    fun `On ProductNameChanged should update productName`() {
        viewModel.onEvent(AddEditProductEvent.ProductNameChanged("Test Product"))

        assertEquals("Test Product", viewModel.state.productName)
    }

    @Test
    fun `On ProductPriceChanged should update productCategory`() {
        viewModel.onEvent(AddEditProductEvent.ProductPriceChanged("10"))

        assertEquals("10", viewModel.state.productPrice)
    }

    @Test
    fun `On ProductDescChanged should update productCategory`() {
        viewModel.onEvent(AddEditProductEvent.ProductDescChanged("Test Description"))

        assertEquals("Test Description", viewModel.state.productDesc)
    }

    @Test
    fun `On ProductAvailabilityChanged should update productCategory`() {
        val productAvailability = viewModel.state.productAvailability
        viewModel.onEvent(AddEditProductEvent.ProductAvailabilityChanged)

        assertEquals(!productAvailability, viewModel.state.productAvailability)
    }

    @Test
    fun `On TagNameChanged should update tagName`() {
        viewModel.onEvent(AddEditProductEvent.TagNameChanged("Test Tag"))

        assertEquals("Test Tag", viewModel.state.tagName)
    }

    @Test
    fun `On CategoryChanged should update categoryName`() = runTest {
        val category = Category(
            categoryId = 1,
            categoryName = "Test Category",
        )
        viewModel.onEvent(AddEditProductEvent.CategoryChanged(category))

        viewModel.selectedCategory.test {
            assertEquals(category, awaitItem())
        }
    }

    @Test
    fun `on SelectTag should update selectedTags`() {
        val tag = "Test Tag"
        viewModel.onEvent(AddEditProductEvent.OnSelectTag(tag))

        assert(viewModel.selectedTags.contains(tag))
    }

    @Test
    fun `on SelectExisting tag should remove tag from selectedTags`() {
        val tag = "Test Tag"
        viewModel.onEvent(AddEditProductEvent.OnSelectTag(tag))

        assert(viewModel.selectedTags.contains(tag))

        viewModel.onEvent(AddEditProductEvent.OnSelectTag(tag))

        assert(!viewModel.selectedTags.contains(tag))
    }

    @Test
    fun `categoryError updated when category is empty`() = runTest {
        val category = Category()
        viewModel.onEvent(AddEditProductEvent.CategoryChanged(category))

        viewModel.categoryError.test {
            assertEquals(PRODUCT_CATEGORY_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun `categoryError updated when category is valid`() = runTest {
        val category = Category(
            categoryId = 1,
            categoryName = "Test Category",
        )
        viewModel.onEvent(AddEditProductEvent.CategoryChanged(category))

        viewModel.categoryError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun `nameError updated when product name is empty`() = runTest {
        viewModel.onEvent(AddEditProductEvent.ProductNameChanged(""))

        viewModel.nameError.test {
            assertEquals(PRODUCT_NAME_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun `nameError updated when product name is less than 4 characters`() = runTest {
        viewModel.onEvent(AddEditProductEvent.ProductNameChanged("Tes"))

        viewModel.nameError.test {
            assertEquals(PRODUCT_NAME_LENGTH_ERROR, awaitItem())
        }
    }

    @Test
    fun `nameError updated when product name is already exists`() = runTest {
        val product = repository.createTestProduct()
        viewModel.onEvent(AddEditProductEvent.ProductNameChanged(product.productName))

        viewModel.nameError.test {
            assertEquals(PRODUCT_NAME_ALREADY_EXIST_ERROR, awaitItem())
        }
    }

    @Test
    fun `nameError updated when product name is already exists with productId`() = runTest {
        val product = repository.createTestProduct()
        viewModel.setProductId(product.productId)
        viewModel.onEvent(AddEditProductEvent.ProductNameChanged(product.productName))

        viewModel.nameError.test {
            assertEquals(null, awaitItem())
        }
    }

    @Test
    fun `nameError updated when product name is valid`() = runTest {
        viewModel.onEvent(AddEditProductEvent.ProductNameChanged("Test Product"))

        viewModel.nameError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun `priceError updated when product price is empty or zero`() = runTest {
        viewModel.onEvent(AddEditProductEvent.ProductPriceChanged("0"))

        viewModel.priceError.test {
            assertEquals(PRODUCT_PRICE_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun `priceError updated when product price is less than 10`() = runTest {
        viewModel.onEvent(AddEditProductEvent.ProductPriceChanged("8"))

        viewModel.priceError.test {
            assertEquals(PRODUCT_PRICE_LENGTH_ERROR, awaitItem())
        }
    }

    @Test
    fun `priceError updated when product price is valid`() = runTest {
        viewModel.onEvent(AddEditProductEvent.ProductPriceChanged("12"))

        viewModel.priceError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun `tagError updated when product price is not empty and less than 3 characters`() = runTest {
        viewModel.onEvent(AddEditProductEvent.TagNameChanged("Te"))

        viewModel.tagError.test {
            assertEquals(PRODUCT_TAG_LENGTH_ERROR, awaitItem())
        }
    }

    @Test
    fun `tagError updated when product price is valid`() = runTest {
        viewModel.onEvent(AddEditProductEvent.TagNameChanged("Test"))

        viewModel.tagError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun `on create new product should created new product`() = runTest {
        val product = itemList.first()
        val category = CategoryPreviewData.categoryList.fastFirst {
            it.categoryId == product.categoryId
        }

        viewModel.onEvent(AddEditProductEvent.CategoryChanged(category))
        viewModel.onEvent(AddEditProductEvent.ProductNameChanged(product.productName))
        viewModel.onEvent(AddEditProductEvent.ProductPriceChanged(product.productPrice.toString()))
        viewModel.onEvent(AddEditProductEvent.ProductDescChanged(product.productDescription))
        if (!product.productAvailability) {
            viewModel.onEvent(AddEditProductEvent.ProductAvailabilityChanged)
        }
        product.tags.forEach {
            viewModel.onEvent(AddEditProductEvent.OnSelectTag(it))
        }

        viewModel.onEvent(AddEditProductEvent.AddOrUpdateProduct)

        viewModel.eventFlow.test {
            assertEquals(
                UiEvent.OnSuccess("Product created successfully"),
                awaitItem(),
            )
        }

        val createdProduct = repository.getProductById(0)
        assertNotNull(createdProduct.data)
        assert(createdProduct is Resource.Success)

        assertEquals(product.productName, createdProduct.data?.productName)
        assertEquals(product.productPrice, createdProduct.data?.productPrice)
        assertEquals(
            product.productDescription.capitalizeWords,
            createdProduct.data?.productDescription,
        )
        assertEquals(product.productAvailability, createdProduct.data?.productAvailability)

        assert(createdProduct.data?.tags?.containsAll(product.tags) == true)
        assertEquals(category.categoryId, createdProduct.data?.categoryId)
    }

    @Test
    fun `on update existing product should update the product`() = runTest {
        val product = itemList.first()
        val category = CategoryPreviewData.categoryList.fastFirst {
            it.categoryId == product.categoryId
        }
        repository.setCategoryList(listOf(category))

        viewModel.onEvent(AddEditProductEvent.ProductNameChanged(product.productName))
        viewModel.onEvent(AddEditProductEvent.ProductPriceChanged(product.productPrice.toString()))
        viewModel.onEvent(AddEditProductEvent.ProductDescChanged(product.productDescription))
        if (!product.productAvailability) {
            viewModel.onEvent(AddEditProductEvent.ProductAvailabilityChanged)
        }
        viewModel.onEvent(AddEditProductEvent.CategoryChanged(category))
        product.tags.forEach {
            viewModel.onEvent(AddEditProductEvent.OnSelectTag(it))
        }

        viewModel.setProductId(product.productId)
        viewModel.onEvent(AddEditProductEvent.AddOrUpdateProduct)

        val updatedPro = product.copy(
            productId = 1,
            productName = "Updated Product",
            productPrice = 20,
            productDescription = "Updated Description",
            productAvailability = false,
            tags = listOf("Updated Tag"),
        )

        viewModel.onEvent(AddEditProductEvent.ProductNameChanged(updatedPro.productName))
        viewModel.onEvent(AddEditProductEvent.ProductPriceChanged(updatedPro.productPrice.toString()))
        viewModel.onEvent(AddEditProductEvent.ProductDescChanged(updatedPro.productDescription))
        if (!updatedPro.productAvailability) {
            viewModel.onEvent(AddEditProductEvent.ProductAvailabilityChanged)
        }
        viewModel.onEvent(AddEditProductEvent.CategoryChanged(category))
        updatedPro.tags.forEach {
            viewModel.onEvent(AddEditProductEvent.OnSelectTag(it))
        }

        viewModel.setProductId(product.productId)
        viewModel.onEvent(AddEditProductEvent.AddOrUpdateProduct)

        viewModel.eventFlow.test {
            assertEquals(
                UiEvent.OnSuccess("Product updated successfully"),
                awaitItem(),
            )
        }

        val updatedProduct = repository.getProductById(updatedPro.productId)
        assertNotNull(updatedProduct)

        assertEquals(updatedPro.productName, updatedProduct.data?.productName)
        assertEquals(updatedPro.productPrice, updatedProduct.data?.productPrice)
        assertEquals(
            updatedPro.productDescription.capitalizeWords,
            updatedProduct.data?.productDescription,
        )
        assertEquals(updatedPro.productAvailability, updatedProduct.data?.productAvailability)
        assert(updatedProduct.data?.tags?.containsAll(updatedPro.tags) == true)
        assertEquals(category.categoryId, updatedProduct.data?.categoryId)
    }
}
