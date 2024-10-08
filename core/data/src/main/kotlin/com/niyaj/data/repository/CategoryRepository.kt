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

package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    suspend fun getAllCategory(searchText: String): Flow<List<Category>>

    suspend fun getCategoryById(categoryId: Int): Resource<Category?>

    suspend fun upsertCategory(newCategory: Category): Resource<Boolean>

    suspend fun deleteCategories(categoryIds: List<Int>): Resource<Boolean>

    suspend fun findCategoryByName(categoryName: String, categoryId: Int?): Boolean

    suspend fun importCategoriesToDatabase(categories: List<Category>): Resource<Boolean>
}
