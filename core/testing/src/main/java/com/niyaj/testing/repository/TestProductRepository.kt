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

package com.niyaj.testing.repository

import com.niyaj.common.result.Resource
import com.niyaj.common.utils.getStartDateLong
import com.niyaj.data.repository.ProductRepository
import com.niyaj.model.Category
import com.niyaj.model.Product
import com.niyaj.model.ProductIdWithPrice
import com.niyaj.model.ProductWiseOrder
import com.niyaj.model.searchProducts
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import org.jetbrains.annotations.TestOnly

class TestProductRepository : ProductRepository {

    /**
     * The backing product, category list for testing
     */
    private val productList = MutableStateFlow(mutableListOf<Product>())
    private val categoryList = MutableStateFlow(mutableListOf<Category>())
    private val productWiseOrder = MutableStateFlow(mutableListOf<ProductWiseOrder>())

    override fun getAllCategory(): Flow<ImmutableList<Category>> =
        categoryList.mapLatest { it.toImmutableList() }

    override suspend fun getCategoryById(categoryId: Int): Category? {
        return categoryList.value.find { it.categoryId == categoryId }
    }

    override suspend fun getAllProduct(
        searchText: String,
        selectedCategory: Int,
    ): Flow<List<Product>> {
        return productList.mapLatest { list ->
            list.filter {
                if (selectedCategory != 0) it.categoryId == selectedCategory else true
            }.searchProducts(searchText)
        }
    }

    override suspend fun getProductById(productId: Int): Resource<Product?> {
        val product = productList.value.find { it.productId == productId }
        return if (product != null) {
            Resource.Success(product)
        } else {
            Resource.Error("Product not found")
        }
    }

    override suspend fun upsertProduct(newProduct: Product): Resource<Boolean> {
        val index = productList.value.indexOfFirst { it.productId == newProduct.productId }
        return if (index != -1) {
            productList.value[index] = newProduct
            Resource.Success(true)
        } else {
            productList.value.add(newProduct)
            Resource.Success(true)
        }
    }

    override suspend fun deleteProducts(productIds: List<Int>): Resource<Boolean> {
        return Resource.Success(productList.value.removeAll { it.productId in productIds })
    }

    override suspend fun getProductPrice(productId: Int): Int {
        return productList.value.find { it.productId == productId }?.productPrice ?: 0
    }

    override suspend fun findProductByName(productName: String, productId: Int?): Boolean {
        return productList.value.any {
            if (productId != null) {
                it.productName == productName && it.productId != productId
            } else {
                it.productName == productName
            }
        }
    }

    override suspend fun getProductWiseOrderDetails(productId: Int): Flow<List<ProductWiseOrder>> {
        return productWiseOrder
    }

    override suspend fun importProductsToDatabase(products: List<Product>): Resource<Boolean> {
        products.forEach {
            upsertProduct(it)
        }
        return Resource.Success(true)
    }

    override suspend fun increaseProductsPrice(products: List<ProductIdWithPrice>): Resource<Boolean> {
        products.forEach { (productId, price) ->
            productList.value.indexOfFirst { it.productId == productId }
                .takeIf { it != -1 }
                ?.let { index ->
                    productList.value[index] = productList.value[index]
                        .copy(productPrice = productList.value[index].productPrice + price)
                }
        }

        return Resource.Success(true)
    }

    override suspend fun decreaseProductsPrice(products: List<ProductIdWithPrice>): Resource<Boolean> {
        products.forEach { (productId, price) ->
            productList.value.indexOfFirst { it.productId == productId }
                .takeIf { it != -1 }
                ?.let { index ->
                    productList.value[index] = productList.value[index]
                        .copy(productPrice = productList.value[index].productPrice - price)
                }
        }

        return Resource.Success(true)
    }

    @TestOnly
    fun setCategoryList(list: List<Category>) {
        categoryList.update { list.toMutableList() }
    }

    @TestOnly
    fun setProductList(list: List<Product>) {
        productList.update { list.toMutableList() }
    }

    @TestOnly
    fun setProductWiseOrderList(list: List<ProductWiseOrder>) {
        productWiseOrder.update { list.toMutableList() }
    }

    @TestOnly
    fun createTestProduct(): Product {
        val product = Product(
            productId = 1,
            productName = "Test Product",
            productPrice = 100,
            categoryId = 1,
            productDescription = "Test Description",
            createdAt = getStartDateLong,
        )

        productList.value.add(product)
        return product
    }
}
