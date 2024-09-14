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

import androidx.annotation.VisibleForTesting
import com.niyaj.data.repository.HomeRepository
import com.niyaj.model.CartOrder
import com.niyaj.model.Category
import com.niyaj.model.ProductWithQuantity
import com.niyaj.model.Selected
import com.niyaj.model.filterByCategory
import com.niyaj.model.filterBySearch
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest

class TestHomeRepository : HomeRepository {

    /**
     * The backing product, category list for testing
     */
    private val productList = MutableStateFlow(mutableListOf<ProductWithQuantity>())
    private val categoryList = MutableStateFlow(mutableListOf<Category>())

    private val cartOrders = MutableStateFlow(mutableListOf<CartOrder>())
    private val selectedItem = MutableStateFlow<Selected?>(null)

    override fun getAllCategory(): Flow<ImmutableList<Category>> =
        categoryList.mapLatest { it.toImmutableList() }

    override fun getSelectedOrder(): Flow<Selected?> = selectedItem

    override suspend fun getSelectedOrderAddress(orderId: Int): String? {
        return cartOrders.value.find { it.orderId == orderId }?.address?.addressName
    }

    override suspend fun getProductsWithQuantities(
        searchText: String,
        selectedCategory: Int,
    ): Flow<List<ProductWithQuantity>> {
        return productList.mapLatest { list ->
            list.filterByCategory(selectedCategory).filterBySearch(searchText)
        }
    }

    @VisibleForTesting
    fun updateSelectedOrder(orderId: Int) {
        selectedItem.value = Selected(orderId = orderId)
    }

    @VisibleForTesting
    fun updateProductList(products: List<ProductWithQuantity>) {
        productList.value = products.toMutableList()
    }

    @VisibleForTesting
    fun updateCategoryList(categories: List<Category>) {
        categoryList.value = categories.toMutableList()
    }

    @VisibleForTesting
    fun updateCartOrders(orders: List<CartOrder>) {
        cartOrders.value = orders.toMutableList()
    }
}
