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

package com.niyaj.testing.repository

import com.niyaj.common.result.Resource
import com.niyaj.data.repository.CategoryRepository
import com.niyaj.model.Category
import com.niyaj.model.searchCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import org.jetbrains.annotations.TestOnly

class TestCategoryRepository : CategoryRepository {
    /**
     * The backing category list for testing
     */
    private val items = MutableStateFlow(mutableListOf<Category>())

    override suspend fun getAllCategory(searchText: String): Flow<List<Category>> {
        return items.mapLatest { it.searchCategory(searchText) }
    }

    override suspend fun getCategoryById(categoryId: Int): Resource<Category?> {
        return Resource.Success(items.value.find { it.categoryId == categoryId })
    }

    override suspend fun upsertCategory(newCategory: Category): Resource<Boolean> {
        val result = items.value.find { it.categoryId == newCategory.categoryId }

        return Resource.Success(
            if (result == null) {
                items.value.add(newCategory)
            } else {
                items.value.remove(result)
                items.value.add(newCategory)
            },
        )
    }

    override suspend fun deleteCategories(categoryIds: List<Int>): Resource<Boolean> {
        return Resource.Success(items.value.removeAll { it.categoryId in categoryIds })
    }

    override suspend fun findCategoryByName(categoryName: String, categoryId: Int?): Boolean {
        return items.value.any {
            if (categoryId != null) {
                it.categoryName == categoryName && it.categoryId != categoryId
            } else {
                it.categoryName == categoryName
            }
        }
    }

    override suspend fun importCategoriesToDatabase(categories: List<Category>): Resource<Boolean> {
        categories.forEach { upsertCategory(it) }

        return Resource.Success(true)
    }

    @TestOnly
    fun updateCategoryData(categoryList: List<Category>) {
        items.update { categoryList.toMutableList() }
    }

    @TestOnly
    fun createTestCategory(): Category {
        val category = Category(
            categoryId = 1,
            categoryName = "Test Category",
            isAvailable = true,
        )

        items.value.add(category)
        return category
    }
}
