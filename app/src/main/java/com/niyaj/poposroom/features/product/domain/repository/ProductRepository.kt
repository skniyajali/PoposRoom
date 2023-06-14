package com.niyaj.poposroom.features.product.domain.repository

import com.niyaj.poposroom.features.category.domain.model.Category
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.product.domain.model.CategoryWithProduct
import com.niyaj.poposroom.features.product.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun getAllCategory(): Flow<List<Category>>

    suspend fun getCategoryById(categoryId: Int) : Category?

    suspend fun getAllCategoryProducts(searchText: String): Flow<List<CategoryWithProduct>>

    suspend fun getAllProduct(searchText: String): Flow<List<Product>>

    suspend fun getProductById(productId: Int): Resource<Product?>

    suspend fun addOrIgnoreProduct(newProduct: Product): Resource<Boolean>

    suspend fun updateProduct(newProduct: Product): Resource<Boolean>

    suspend fun upsertProduct(newProduct: Product): Resource<Boolean>

    suspend fun deleteProduct(productId: Int): Resource<Boolean>

    suspend fun deleteProducts(productIds: List<Int>): Resource<Boolean>

}