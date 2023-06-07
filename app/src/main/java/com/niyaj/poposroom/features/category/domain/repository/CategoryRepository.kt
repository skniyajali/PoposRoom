package com.niyaj.poposroom.features.category.domain.repository

import com.niyaj.poposroom.features.category.domain.model.Category
import com.niyaj.poposroom.features.common.utils.Resource
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    suspend fun getAllCategory(searchText: String): Flow<List<Category>>

    suspend fun getCategoryById(categoryId: Int): Resource<Category?>

    suspend fun addOrIgnoreCategory(newCategory: Category): Resource<Boolean>

    suspend fun updateCategory(newCategory: Category): Resource<Boolean>

    suspend fun upsertCategory(newCategory: Category): Resource<Boolean>

    suspend fun deleteCategory(categoryId: Int): Resource<Boolean>

    suspend fun deleteCategories(categoryIds: List<Int>): Resource<Boolean>
}