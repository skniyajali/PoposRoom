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
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.CategoryRepository
import com.niyaj.database.dao.CategoryDao
import com.niyaj.database.model.CategoryEntity
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Category
import com.niyaj.model.searchCategory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : CategoryRepository {

    override suspend fun getAllCategory(searchText: String): Flow<List<Category>> {
        return withContext(ioDispatcher) {
            categoryDao.getAllCategories().mapLatest { list ->
                list.map(CategoryEntity::asExternalModel).searchCategory(searchText)
            }
        }
    }

    override suspend fun getCategoryById(categoryId: Int): Resource<Category?> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(categoryDao.getCategoryById(categoryId)?.asExternalModel())
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun upsertCategory(newCategory: Category): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = categoryDao.upsertCategory(newCategory.toEntity())

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable")
        }
    }

    override suspend fun deleteCategories(categoryIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                withContext(ioDispatcher) {
                    val result = categoryDao.deleteCategories(categoryIds)

                    Resource.Success(result > 0)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete categories")
        }
    }

    override suspend fun findCategoryByName(categoryName: String, categoryId: Int?): Boolean {
        return withContext(ioDispatcher) {
            categoryDao.findCategoryByName(categoryName, categoryId) != null
        }
    }

    override suspend fun importCategoriesToDatabase(categories: List<Category>): Resource<Boolean> {
        try {
            categories.forEach { category ->
                withContext(ioDispatcher) {
                    categoryDao.upsertCategory(category.toEntity())
                }
            }

            return Resource.Success(true)
        } catch (e: Exception) {
            return Resource.Error(e.message ?: "Unable")
        }
    }
}
