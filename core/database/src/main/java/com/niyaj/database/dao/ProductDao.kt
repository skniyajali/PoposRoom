package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.database.model.CategoryEntity
import com.niyaj.database.model.CategoryWithProductCrossRef
import com.niyaj.database.model.CategoryWithProductsDto
import com.niyaj.database.model.ProductEntity
import com.niyaj.database.model.ProductWiseOrderDetailsDto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Transaction
    @Query(
        value = """
        SELECT * FROM category
    """
    )
    fun getAllCategoryProduct(): Flow<List<CategoryWithProductsDto>>

    @Query(
        value = """
        SELECT * FROM category
    """
    )
    fun getAllCategory(): Flow<List<CategoryEntity>>

    @Query(
        value = """
        SELECT * FROM category WHERE categoryId = :categoryId
    """
    )
    suspend fun getCategoryById(categoryId: Int): CategoryEntity?

    @Query(
        value = """
        SELECT * FROM product ORDER BY categoryId ASC, productPrice ASC
    """
    )
    fun getAllProduct(): Flow<List<ProductEntity>>

    @Query(
        value = """
        SELECT * FROM product WHERE productId = :productId
    """
    )
    suspend fun getProductById(productId: Int): ProductEntity?

    /**
     * Inserts [ProductEntity] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreProduct(product: ProductEntity): Long

    /**
     * Updates [ProductEntity] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateProduct(product: ProductEntity): Int

    /**
     * Inserts or updates [ProductEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertProduct(product: ProductEntity): Long

    @Insert(entity = CategoryWithProductCrossRef::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCategoryWithProductCrossReference(categoryWithProduct: CategoryWithProductCrossRef)

    @Query(
        value = """
        DELETE FROM product WHERE productId = :productId
    """
    )
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

    @Query(
        value = """
        SELECT * FROM product WHERE
            CASE WHEN :productId IS NULL OR :productId = 0
            THEN productName = :productName
            ELSE productId != :productId AND productName = :productName
            END LIMIT 1
    """
    )
    fun findProductByName(productName: String, productId: Int?): ProductEntity?

    @Query(
        value = """
            SELECT productPrice FROM product WHERE productId = :productId
        """
    )
    suspend fun getProductPriceById(productId: Int): Int

    @Transaction
    @Query(
        value = """
            SELECT orderId FROM cart WHERE productId = :productId ORDER BY updatedAt DESC
        """
    )
    fun getOrderIdsAndQuantity(productId: Int): Flow<List<Int>>


    @Transaction
    @Query(
        value = """
            SELECT * FROM cartorder WHERE orderId IN (:orderIds) ORDER BY updatedAt DESC
        """
    )
    fun getProductWiseOrders(orderIds: List<Int>): Flow<List<ProductWiseOrderDetailsDto>>


    @Query(
        value = """
            UPDATE product SET productPrice = :productPrice WHERE productId = :productId
        """
    )
    suspend fun updateProductPrice(productId: Int, productPrice: Int): Int
}