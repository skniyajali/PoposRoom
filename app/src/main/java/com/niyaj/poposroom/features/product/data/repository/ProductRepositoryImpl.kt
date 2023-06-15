package com.niyaj.poposroom.features.product.data.repository

import com.niyaj.poposroom.features.category.domain.model.Category
import com.niyaj.poposroom.features.category.domain.model.filterCategory
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.common.utils.ValidationResult
import com.niyaj.poposroom.features.product.data.dao.ProductDao
import com.niyaj.poposroom.features.product.domain.model.CategoryWithProduct
import com.niyaj.poposroom.features.product.domain.model.CategoryWithProductCrossRef
import com.niyaj.poposroom.features.product.domain.model.Product
import com.niyaj.poposroom.features.product.domain.model.filterProducts
import com.niyaj.poposroom.features.product.domain.repository.ProductRepository
import com.niyaj.poposroom.features.product.domain.repository.ProductValidationRepository
import com.niyaj.poposroom.features.product.domain.utils.ProductTestTags.PRODUCT_CATEGORY_EMPTY_ERROR
import com.niyaj.poposroom.features.product.domain.utils.ProductTestTags.PRODUCT_NAME_ALREADY_EXIST_ERROR
import com.niyaj.poposroom.features.product.domain.utils.ProductTestTags.PRODUCT_NAME_EMPTY_ERROR
import com.niyaj.poposroom.features.product.domain.utils.ProductTestTags.PRODUCT_NAME_LENGTH_ERROR
import com.niyaj.poposroom.features.product.domain.utils.ProductTestTags.PRODUCT_PRICE_EMPTY_ERROR
import com.niyaj.poposroom.features.product.domain.utils.ProductTestTags.PRODUCT_PRICE_LENGTH_ERROR
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class ProductRepositoryImpl(
    private val productDao: ProductDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher
) : ProductRepository, ProductValidationRepository {

    override fun getAllCategory(): Flow<List<Category>> {
        return productDao.getAllCategory()
    }

    override suspend fun getCategoryById(categoryId: Int): Category? {
        return withContext(ioDispatcher) {
            productDao.getCategoryById(categoryId)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAllCategoryProducts(searchText: String): Flow<List<CategoryWithProduct>> {
        return withContext(ioDispatcher) {
            productDao.getAllCategoryProduct().mapLatest { list ->
                list.filter {
                    it.category.filterCategory(searchText)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAllProduct(searchText: String, selectedCategory: Int): Flow<List<Product>> {
        return withContext(ioDispatcher) {
            productDao.getAllProduct().mapLatest { list ->
                if (selectedCategory != 0) {
                    list.filter { it.categoryId == selectedCategory }
                } else list
            }.mapLatest {
                it.filterProducts(searchText)
            }
        }
    }

    override suspend fun getProductById(productId: Int): Resource<Product?> {
        return withContext(ioDispatcher) {
            try {
                Resource.Success(productDao.getProductById(productId))
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Product not found")
            }
        }
    }

    override suspend fun addOrIgnoreProduct(newProduct: Product): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateCategory = validateCategoryName(newProduct.categoryId)
                val valProduct = validateProductName(newProduct.productName)
                val validateProductPrice = validateProductPrice(newProduct.productPrice)

                val hasError = listOf(
                    validateCategory,
                    validateProductPrice,
                    valProduct
                ).any { !it.successful }

                if (!hasError) {

                    val category = withContext(ioDispatcher) {
                        productDao.getCategoryById(newProduct.categoryId)
                    }

                    if (category != null) {
                        val result = withContext(ioDispatcher) {
                            productDao.insertOrIgnoreProduct(newProduct)
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
            Resource.Error(e.message ?: "Unable to create new product")
        }
    }

    override suspend fun updateProduct(newProduct: Product): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateCategory = validateCategoryName(newProduct.categoryId)
                val valProduct = validateProductName(newProduct.productName, newProduct.productId)
                val validateProductPrice = validateProductPrice(newProduct.productPrice)

                val hasError = listOf(
                    validateCategory,
                    validateProductPrice,
                    valProduct
                ).any { !it.successful }

                if (!hasError) {

                    val category = withContext(ioDispatcher) {
                        productDao.getCategoryById(newProduct.categoryId)
                    }

                    if (category != null) {
                        val result = withContext(ioDispatcher) {
                            productDao.updateProduct(newProduct)
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
            Resource.Error(e.message ?: "Unable to create new product")
        }
    }

    override suspend fun upsertProduct(newProduct: Product): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateCategory = validateCategoryName(newProduct.categoryId)
                val valProduct = validateProductName(newProduct.productName, newProduct.productId)
                val validateProductPrice = validateProductPrice(newProduct.productPrice)

                val hasError = listOf(
                    validateCategory,
                    validateProductPrice,
                    valProduct
                ).any { !it.successful }


                if (!hasError) {
                    val category = withContext(ioDispatcher) {
                        productDao.getCategoryById(newProduct.categoryId)
                    }

                    if (category != null) {
                        val result = withContext(ioDispatcher) {
                            productDao.upsertProduct(newProduct)
                        }

                        if (result > 0) {
                            withContext(ioDispatcher) {
                                productDao.upsertCategoryWithProductCrossReference(
                                    CategoryWithProductCrossRef(newProduct.categoryId, result.toInt())
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

    override suspend fun deleteProduct(productId: Int): Resource<Boolean> {
        return try {
            val result = withContext(ioDispatcher) {
                productDao.deleteProduct(productId)
            }

            Resource.Success(result > 0)
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete product")
        }
    }

    override suspend fun deleteProducts(productIds: List<Int>): Resource<Boolean> {
        return try {
            val result = withContext(ioDispatcher) {
                productDao.deleteProducts(productIds)
            }

            Resource.Success(result > 0)
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete products")
        }
    }

    override fun validateCategoryName(categoryId: Int): ValidationResult {
        if (categoryId == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_CATEGORY_EMPTY_ERROR
            )
        }

        return ValidationResult(
            successful = true
        )
    }

    override suspend fun validateProductName(
        productName: String,
        productId: Int?
    ): ValidationResult {
        if (productName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_NAME_EMPTY_ERROR
            )
        }

        if (productName.length < 4) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_NAME_LENGTH_ERROR
            )
        }

        val serverResult = withContext(ioDispatcher) {
            productDao.findProductByName(productName, productId) != null
        }

        if (serverResult) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_NAME_ALREADY_EXIST_ERROR
            )
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validateProductPrice(productPrice: Int, type: String?): ValidationResult {
        if (productPrice == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_PRICE_EMPTY_ERROR
            )
        }

        if (type.isNullOrEmpty() && productPrice < 10) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_PRICE_LENGTH_ERROR
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}