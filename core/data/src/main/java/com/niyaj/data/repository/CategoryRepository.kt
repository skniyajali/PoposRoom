package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    suspend fun getAllCategory(searchText: String): Flow<List<Category>>

    suspend fun getCategoryById(categoryId: Int): Resource<Category?>

    suspend fun upsertCategory(newCategory: Category): Resource<Boolean>

    suspend fun deleteCategories(categoryIds: List<Int>): Resource<Boolean>

    suspend fun importCategoriesToDatabase(categories: List<Category>): Resource<Boolean>
}