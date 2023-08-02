package com.niyaj.data.repository

import com.niyaj.model.Category
import com.niyaj.model.ProductWithFlowQuantity
import com.niyaj.model.Selected
import kotlinx.coroutines.flow.Flow

interface MainFeedRepository {

    fun getAllCategory(): Flow<List<Category>>

    suspend fun getAllProduct(searchText: String, selectedCategory: Int = 0): Flow<List<ProductWithFlowQuantity>>

    fun getSelectedOrder(): Flow<Selected?>
}