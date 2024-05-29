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

class HomeRepositoryImpl(
    private val homeDao: HomeDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : HomeRepository {

    override fun getAllCategory(): Flow<ImmutableList<Category>> {
        return homeDao.getAllCategories().mapLatest { list ->
            list.map {
                it.asExternalModel()
            }.toImmutableList()
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
            homeDao.getProductWithQtyView(selectedCategory).mapLatest { list ->
                list.asExternalModel()
                    .filterBySearch(searchText)
            }.distinctUntilChanged()

//            homeDao.getSelectedOrder().flatMapLatest { order ->
//                homeDao.getProductWithQty(order?.orderId).mapLatest {
//                    it.filterByCategory(selectedCategory)
//                        .filterBySearch(searchText)
//                }.distinctUntilChanged()
//            }
        }
    }
}
