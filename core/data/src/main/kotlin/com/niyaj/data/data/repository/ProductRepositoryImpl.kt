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
import com.niyaj.common.result.Resource
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.ProductRepository
import com.niyaj.database.dao.ProductDao
import com.niyaj.database.model.CategoryWithProductCrossRef
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Category
import com.niyaj.model.Product
import com.niyaj.model.ProductIdWithPrice
import com.niyaj.model.ProductWiseOrder
import com.niyaj.model.searchProducts
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : ProductRepository {

    override fun getAllCategory(): Flow<ImmutableList<Category>> {
        return productDao.getAllCategory().mapLatest { list ->
            list.map {
                it.asExternalModel()
            }.toImmutableList()
        }
    }

    override suspend fun getCategoryById(categoryId: Int): Category? {
        return withContext(ioDispatcher) {
            productDao.getCategoryById(categoryId)?.asExternalModel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAllProduct(
        searchText: String,
        selectedCategory: Int,
    ): Flow<List<Product>> {
        return withContext(ioDispatcher) {
            productDao.getAllProduct().mapLatest { entityList ->
                entityList.map { it.asExternalModel() }
            }.mapLatest { list ->
                if (selectedCategory != 0) {
                    list.filter { it.categoryId == selectedCategory }
                } else {
                    list
                }
            }.mapLatest {
                it.searchProducts(searchText)
            }
        }
    }

    override suspend fun getProductById(productId: Int): Resource<Product?> {
        return withContext(ioDispatcher) {
            try {
                Resource.Success(productDao.getProductById(productId)?.asExternalModel())
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Product not found")
            }
        }
    }

    override suspend fun upsertProduct(newProduct: Product): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val category = withContext(ioDispatcher) {
                    productDao.getCategoryById(newProduct.categoryId)
                }

                if (category != null) {
                    val result = withContext(ioDispatcher) {
                        productDao.upsertProduct(newProduct.toEntity())
                    }

                    if (result > 0) {
                        withContext(ioDispatcher) {
                            productDao.upsertCategoryWithProductCrossReference(
                                CategoryWithProductCrossRef(
                                    newProduct.categoryId,
                                    result.toInt(),
                                ),
                            )
                        }
                    }

                    Resource.Success(result > 0)
                } else {
                    Resource.Error("Unable to find category")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create or update new product")
        }
    }

    override suspend fun deleteProducts(productIds: List<Int>): Resource<Boolean> {
        return try {
            val result = withContext(ioDispatcher) {
                productDao.deleteProducts(productIds)
            }

            Resource.Success(result > 0)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete products")
        }
    }

    override suspend fun findProductByName(productName: String, productId: Int?): Boolean {
        return withContext(ioDispatcher) {
            productDao.findProductByName(productName, productId) != null
        }
    }

    override suspend fun getProductPrice(productId: Int): Int {
        return withContext(ioDispatcher) {
            productDao.getProductPriceById(productId)
        }
    }

    override suspend fun getProductWiseOrderDetails(productId: Int): Flow<List<ProductWiseOrder>> {
        return withContext(ioDispatcher) {
            productDao.getProductWiseOrder(productId)
        }
    }

    override suspend fun importProductsToDatabase(products: List<Product>): Resource<Boolean> {
        try {
            products.forEach { newProduct ->
                upsertProduct(newProduct)
            }

            return Resource.Success(true)
        } catch (e: Exception) {
            return Resource.Error(e.message ?: "Unable to import products to database")
        }
    }

    override suspend fun increaseProductsPrice(products: List<ProductIdWithPrice>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                products.forEach {
                    withContext(ioDispatcher) {
                        productDao.updateProductPrice(it.productId, it.productPrice)
                    }
                }

                Resource.Success(true)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to increase products price")
        }
    }

    override suspend fun decreaseProductsPrice(products: List<ProductIdWithPrice>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                products.forEach {
                    withContext(ioDispatcher) {
                        productDao.updateProductPrice(it.productId, it.productPrice)
                    }
                }

                Resource.Success(true)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to decrease product price")
        }
    }
}
