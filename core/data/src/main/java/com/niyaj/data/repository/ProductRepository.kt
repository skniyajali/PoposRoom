package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.Category
import com.niyaj.model.Product
import com.niyaj.model.ProductIdWithPrice
import com.niyaj.model.ProductWiseOrder
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun getAllCategory(): Flow<ImmutableList<Category>>

    suspend fun getCategoryById(categoryId: Int): Category?

    suspend fun getAllProduct(searchText: String, selectedCategory: Int = 0): Flow<List<Product>>

    suspend fun getProductById(productId: Int): Resource<Product?>

    suspend fun upsertProduct(newProduct: Product): Resource<Boolean>

    suspend fun deleteProducts(productIds: List<Int>): Resource<Boolean>

    suspend fun getProductPrice(productId: Int): Int

    suspend fun getProductWiseOrderDetails(productId: Int): Flow<List<ProductWiseOrder>>

    suspend fun importProductsToDatabase(products: List<Product>): Resource<Boolean>

    suspend fun increaseProductsPrice(products: List<ProductIdWithPrice>): Resource<Boolean>

    suspend fun decreaseProductsPrice(products: List<ProductIdWithPrice>): Resource<Boolean>

}