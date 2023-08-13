package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.result.ValidationResult
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.ProductRepository
import com.niyaj.data.repository.validation.ProductValidationRepository
import com.niyaj.data.utils.ProductTestTags.PRODUCT_CATEGORY_EMPTY_ERROR
import com.niyaj.data.utils.ProductTestTags.PRODUCT_NAME_ALREADY_EXIST_ERROR
import com.niyaj.data.utils.ProductTestTags.PRODUCT_NAME_EMPTY_ERROR
import com.niyaj.data.utils.ProductTestTags.PRODUCT_NAME_LENGTH_ERROR
import com.niyaj.data.utils.ProductTestTags.PRODUCT_PRICE_EMPTY_ERROR
import com.niyaj.data.utils.ProductTestTags.PRODUCT_PRICE_LENGTH_ERROR
import com.niyaj.database.dao.ProductDao
import com.niyaj.database.model.CategoryWithProductCrossRef
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Category
import com.niyaj.model.CategoryWithProducts
import com.niyaj.model.Product
import com.niyaj.model.ProductWiseOrder
import com.niyaj.model.filterCategory
import com.niyaj.model.filterProducts
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class ProductRepositoryImpl(
    private val productDao: ProductDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : ProductRepository, ProductValidationRepository {

    override fun getAllCategory(): Flow<List<Category>> {
        return productDao.getAllCategory().mapLatest { list ->
            list.map {
                it.asExternalModel()
            }
        }
    }

    override suspend fun getCategoryById(categoryId: Int): Category? {
        return withContext(ioDispatcher) {
            productDao.getCategoryById(categoryId)?.asExternalModel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAllCategoryProducts(searchText: String): Flow<List<CategoryWithProducts>> {
        return withContext(ioDispatcher) {
            productDao.getAllCategoryProduct().mapLatest { list ->
                list.map { it.asExternalModel() }
                    .filter {
                        it.category.filterCategory(searchText)
                    }
            }
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
                } else list
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

    override suspend fun addOrIgnoreProduct(newProduct: Product): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateCategory = validateCategoryId(newProduct.categoryId)
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
                            productDao.insertOrIgnoreProduct(newProduct.toEntity())
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
                val validateCategory = validateCategoryId(newProduct.categoryId)
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
                            productDao.updateProduct(newProduct.toEntity())
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
                val validateCategory = validateCategoryId(newProduct.categoryId)
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
                            productDao.upsertProduct(newProduct.toEntity())
                        }

                        if (result > 0) {
                            withContext(ioDispatcher) {
                                productDao.upsertCategoryWithProductCrossReference(
                                    CategoryWithProductCrossRef(
                                        newProduct.categoryId,
                                        result.toInt()
                                    )
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
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete product")
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
                errorMessage = PRODUCT_CATEGORY_EMPTY_ERROR
            )
        }

        return ValidationResult(
            successful = true
        )
    }

    override suspend fun validateProductName(
        productName: String,
        productId: Int?,
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

    override suspend fun getProductPrice(productId: Int): Int {
        return withContext(ioDispatcher) {
            productDao.getProductPriceById(productId)
        }
    }

    override suspend fun getProductWiseOrderDetails(productId: Int): Flow<List<ProductWiseOrder>> {
        return withContext(ioDispatcher) {
            productDao.getOrderIdsAndQuantity(productId).flatMapLatest { list ->
                productDao.getProductWiseOrders(list)
            }.mapLatest { items ->
                items.map {
                    ProductWiseOrder(
                        orderId = it.cartOrder.orderId,
                        orderedDate = it.cartOrder.updatedAt ?: it.cartOrder.createdAt,
                        orderType = it.cartOrder.orderType,
                        quantity = it.productQuantity.quantity,
                        customerPhone = it.customer?.customerPhone,
                        customerAddress = it.address?.addressName
                    )
                }
            }
        }
    }
}