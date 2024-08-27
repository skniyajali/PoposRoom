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

package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.repository.HomeRepository
import com.niyaj.database.dao.HomeDao
import com.niyaj.database.model.CategoryEntity
import com.niyaj.database.model.ProductWIthQuantityView
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Category
import com.niyaj.model.ProductWithQuantity
import com.niyaj.model.Selected
import com.niyaj.model.filterBySearch
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val homeDao: HomeDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : HomeRepository {

    override fun getAllCategory(): Flow<ImmutableList<Category>> {
        return homeDao.getAllCategories().mapLatest { list ->
            list.map(CategoryEntity::asExternalModel).toImmutableList()
        }
    }

    override fun getSelectedOrder(): Flow<Selected?> {
        return homeDao.getSelectedOrder().mapLatest { it?.asExternalModel() }
    }

    override suspend fun getSelectedOrderAddress(orderId: Int): String? {
        return withContext(ioDispatcher) {
            homeDao.getSelectedOrderAddress(orderId)
        }
    }

    override suspend fun getProductsWithQuantities(
        searchText: String,
        selectedCategory: Int,
    ): Flow<List<ProductWithQuantity>> {
        return withContext(ioDispatcher) {
            homeDao.getProductWithQtyView(selectedCategory)
                .mapLatest(List<ProductWIthQuantityView>::asExternalModel)
                .mapLatest { list ->
                    list.filterBySearch(searchText)
                        .sortByTagsAndCategory(priorityTags)
                }.distinctUntilChanged()
        }
    }
}

// TODO:: this should use dynamically
private val priorityTags = listOf("Popular", "Best", "New")

private fun List<ProductWithQuantity>.sortByTagsAndCategory(
    priorityTags: List<String>,
): List<ProductWithQuantity> {
    val priorityTagSet = priorityTags.toSet()
    val priorityTagIndices = priorityTags.withIndex().associate { it.value to it.index }

    val cachedData = map { item ->
        val priorityScore = item.tags.firstOrNull { it in priorityTagSet }
            ?.let { priorityTagIndices[it] ?: Int.MAX_VALUE }
            ?: Int.MAX_VALUE
        val hasTagsScore = if (item.tags.isEmpty()) 1 else 0
        Triple(item, priorityScore, hasTagsScore)
    }

    return cachedData.sortedWith(
        compareBy<Triple<ProductWithQuantity, Int, Int>>
            { it.second } // priorityScore
            .thenBy { it.third } // hasTagsScore
            .thenBy { it.first.categoryId }
            .thenBy { it.first.productPrice },
    ).map { it.first }
}
