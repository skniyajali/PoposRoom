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
import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.ProductTestTags.PRODUCT_CATEGORY_EMPTY_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_NAME_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_NAME_EMPTY_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_NAME_LENGTH_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_PRICE_EMPTY_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_PRICE_LENGTH_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_TAG_LENGTH_ERROR
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.ProductRepository
import com.niyaj.data.repository.validation.ProductValidationRepository
import com.niyaj.database.dao.ProductDao
import com.niyaj.database.model.CategoryWithProductCrossRef
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Category
import com.niyaj.model.Product
import com.niyaj.model.ProductIdWithPrice
import com.niyaj.model.ProductWiseOrder
import com.niyaj.model.filterProducts
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class ProductRepositoryImpl(
    private val productDao: ProductDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : ProductRepository, ProductValidationRepository {

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
                it.filterProducts(searchText)
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
                val validateCategory = validateCategoryId(newProduct.categoryId)
                val valProduct = validateProductName(newProduct.productName, newProduct.productId)
                val validateProductPrice = validateProductPrice(newProduct.productPrice)

                val hasError = listOf(
                    validateCategory,
                    validateProductPrice,
                    valProduct,
                ).any { !it.successful }

                if (!hasError) {
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
                } else {
                    Resource.Error("Unable to validate product")
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

    override fun validateCategoryId(categoryId: Int): ValidationResult {
        if (categoryId == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_CATEGORY_EMPTY_ERROR,
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override suspend fun validateProductName(
        productName: String,
        productId: Int?,
    ): ValidationResult {
        if (productName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_NAME_EMPTY_ERROR,
            )
        }

        if (productName.length < 4) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_NAME_LENGTH_ERROR,
            )
        }

        val serverResult = withContext(ioDispatcher) {
            productDao.findProductByName(productName, productId) != null
        }

        if (serverResult) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_NAME_ALREADY_EXIST_ERROR,
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override fun validateProductPrice(productPrice: Int, type: String?): ValidationResult {
        if (productPrice == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_PRICE_EMPTY_ERROR,
            )
        }

        if (type.isNullOrEmpty() && productPrice < 10) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_PRICE_LENGTH_ERROR,
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override fun validateProductTag(tagName: String): ValidationResult {
        if (tagName.isNotEmpty()) {
            if (tagName.length < 3) {
                return ValidationResult(
                    successful = false,
                    errorMessage = PRODUCT_TAG_LENGTH_ERROR,
                )
            }
        }

        return ValidationResult(true)
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
                val validateCategory = validateCategoryId(newProduct.categoryId)
                val valProduct = validateProductName(newProduct.productName, newProduct.productId)
                val validateProductPrice = validateProductPrice(newProduct.productPrice)

                val hasError = listOf(
                    validateCategory,
                    validateProductPrice,
                    valProduct,
                ).any { !it.successful }

                if (!hasError) {
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
                    } else {
                        return Resource.Error("Unable to find category")
                    }
                } else {
                    return Resource.Error("Unable to validate product")
                }
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
