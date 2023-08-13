package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.Category
import com.niyaj.model.CategoryWithProducts
import com.niyaj.model.Product
import com.niyaj.model.ProductWiseOrder
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun getAllCategory(): Flow<List<Category>>

    suspend fun getCategoryById(categoryId: Int) : Category?

    suspend fun getAllCategoryProducts(searchText: String): Flow<List<CategoryWithProducts>>

    suspend fun getAllProduct(searchText: String, selectedCategory: Int = 0): Flow<List<Product>>

    suspend fun getProductById(productId: Int): Resource<Product?>

    suspend fun addOrIgnoreProduct(newProduct: Product): Resource<Boolean>

    suspend fun updateProduct(newProduct: Product): Resource<Boolean>

    suspend fun upsertProduct(newProduct: Product): Resource<Boolean>

    suspend fun deleteProduct(productId: Int): Resource<Boolean>

    suspend fun deleteProducts(productIds: List<Int>): Resource<Boolean>

    suspend fun getProductPrice(productId: Int): Int

    suspend fun getProductWiseOrderDetails(productId: Int): Flow<List<ProductWiseOrder>>

}