package com.niyaj.poposroom.features.product.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.poposroom.features.category.domain.model.Category
import com.niyaj.poposroom.features.product.domain.model.CategoryWithProduct
import com.niyaj.poposroom.features.product.domain.model.CategoryWithProductCrossRef
import com.niyaj.poposroom.features.product.domain.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Transaction
    @Query(value = """
        SELECT * FROM category
    """)
    fun getAllCategoryProduct(): Flow<List<CategoryWithProduct>>

    @Query(value = """
        SELECT * FROM category
    """)
    fun getAllCategory(): Flow<List<Category>>

    @Query(value = """
        SELECT * FROM category WHERE categoryId = :categoryId
    """
    )
    suspend fun getCategoryById(categoryId: Int): Category?

    @Query(value = """
        SELECT * FROM product ORDER BY createdAt DESC
    """)
    fun getAllProduct(): Flow<List<Product>>

    @Query(value = """
        SELECT * FROM product WHERE productId = :productId
    """)
    suspend fun getProductById(productId: Int): Product?

    /**
     * Inserts [Product] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreProduct(product: Product): Long

    /**
     * Updates [Product] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateProduct(product: Product): Int

    /**
     * Inserts or updates [Product] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertProduct(product: Product): Long

    @Insert(entity = CategoryWithProductCrossRef::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCategoryWithProductCrossReference(categoryWithProduct: CategoryWithProductCrossRef)

    @Query(value = """
        DELETE FROM product WHERE productId = :productId
    """)
    suspend fun deleteProduct(productId: Int): Int

    /**
     * Deletes rows in the db matching the specified [productIds]
     */
    @Query(
        value = """
            DELETE FROM product
            WHERE productId in (:productIds)
        """,
    )
    suspend fun deleteProducts(productIds: List<Int>): Int

    @Query(value = """
        SELECT * FROM product WHERE
            CASE WHEN :productId IS NULL OR :productId = 0
            THEN productName = :productName
            ELSE productId != :productId AND productName = :productName
            END LIMIT 1
    """
    )
    fun findProductByName(productName: String, productId: Int?): Product?

}