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

package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.database.model.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query(
        value = """
        SELECT * FROM category ORDER BY createdAt DESC
    """,
    )
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query(
        value = """
        SELECT * FROM category WHERE categoryId = :categoryId
    """,
    )
    fun getCategoryById(categoryId: Int): CategoryEntity?

    /**
     * Inserts [CategoryEntity] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreCategory(addOnItem: CategoryEntity): Long

    /**
     * Updates [CategoryEntity] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateCategory(addOnItem: CategoryEntity): Int

    /**
     * Inserts or updates [CategoryEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertCategory(addOnItem: CategoryEntity): Long

    @Query(
        value = """
        DELETE FROM category WHERE categoryId = :categoryId
    """,
    )
    suspend fun deleteCategory(categoryId: Int): Int

    /**
     * Deletes rows in the db matching the specified [categoryIds]
     */
    @Query(
        value = """
            DELETE FROM category
            WHERE categoryId in (:categoryIds)
        """,
    )
    suspend fun deleteCategories(categoryIds: List<Int>): Int

    @Query(
        value = """
        SELECT categoryId FROM category WHERE
            CASE WHEN :categoryId IS NULL OR :categoryId = 0
            THEN categoryName = :categoryName
            ELSE categoryId != :categoryId AND categoryName = :categoryName
            END LIMIT 1
    """,
    )
    fun findCategoryByName(categoryName: String, categoryId: Int?): Int?
}
