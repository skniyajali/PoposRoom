package com.niyaj.poposroom.features.category.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.poposroom.features.category.domain.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query(value = """
        SELECT * FROM category ORDER BY createdAt DESC
    """)
    fun getAllCategories(): Flow<List<Category>>

    @Query(value = """
        SELECT * FROM category WHERE categoryId = :categoryId
    """)
    fun getCategoryById(categoryId: Int): Category?

    /**
     * Inserts [Category] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreCategory(addOnItem: Category): Long

    /**
     * Updates [Category] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateCategory(addOnItem: Category): Int

    /**
     * Inserts or updates [Category] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertCategory(addOnItem: Category): Long

    @Query(value = """
        DELETE FROM category WHERE categoryId = :categoryId
    """)
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

    @Query(value = """
        SELECT * FROM category WHERE
            CASE WHEN :categoryId IS NULL OR :categoryId = 0
            THEN categoryName = :categoryName
            ELSE categoryId != :categoryId AND categoryName = :categoryName
            END LIMIT 1
    """)
    fun findCategoryByName(categoryName: String, categoryId: Int?): Category?
}