/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.niyaj.database.model.CategoryEntity
import com.niyaj.database.model.CategoryWithProductCrossRef
import com.niyaj.database.model.ProductEntity
import com.niyaj.model.OrderStatus
import com.niyaj.model.ProductWiseOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query(
        value = """
        SELECT * FROM category
    """,
    )
    fun getAllCategory(): Flow<List<CategoryEntity>>

    @Query(
        value = """
        SELECT * FROM category WHERE categoryId = :categoryId
    """,
    )
    suspend fun getCategoryById(categoryId: Int): CategoryEntity?

    @Query(
        value = """
        SELECT * FROM product ORDER BY categoryId ASC, productPrice ASC
    """,
    )
    fun getAllProduct(): Flow<List<ProductEntity>>

    @Query(
        value = """
        SELECT * FROM product WHERE productId = :productId
    """,
    )
    suspend fun getProductById(productId: Int): ProductEntity?


    /**
     * Inserts or updates [ProductEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertProduct(product: ProductEntity): Long

    @Insert(entity = CategoryWithProductCrossRef::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCategoryWithProductCrossReference(categoryWithProduct: CategoryWithProductCrossRef)


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
    """,
    )
    fun findProductByName(productName: String, productId: Int?): ProductEntity?

    @Query(
        value = """
            SELECT productPrice FROM product WHERE productId = :productId
        """,
    )
    suspend fun getProductPriceById(productId: Int): Int

    @Transaction
    @Query(
        value = """
            SELECT orderId FROM cart WHERE productId = :productId ORDER BY updatedAt DESC
        """,
    )
    fun getOrderIdByProductIdFromCart(productId: Int): Flow<List<Int>>

    @Query(
        value = """
            SELECT c.orderId, co.orderType, c.quantity, 
            COALESCE(ad.addressName, null) as customerAddress,
            COALESCE(cu.customerPhone, null) as customerPhone,
            COALESCE(co.updatedAt, co.createdAt) as orderedDate
            FROM cart c
            JOIN cartorder co ON co.orderId = c.orderId
            LEFT JOIN address ad ON ad.addressId = co.addressId
            LEFT JOIN customer cu ON cu.customerId = co.customerId
            WHERE c.productId = :productId AND co.orderStatus = :orderStatus
            ORDER BY co.updatedAt DESC
        """
    )
    fun getProductWiseOrder(
        productId: Int,
        orderStatus: OrderStatus = OrderStatus.PLACED
    ): Flow<List<ProductWiseOrder>>

    @Query(
        value = """
            UPDATE product SET productPrice = :productPrice WHERE productId = :productId
        """,
    )
    suspend fun updateProductPrice(productId: Int, productPrice: Int): Int
}