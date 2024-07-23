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
import androidx.room.Query
import com.niyaj.database.model.CategoryEntity
import com.niyaj.database.model.ProductWIthQuantityView
import com.niyaj.database.model.SelectedEntity
import com.niyaj.model.ProductWithQuantity
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeDao {

    @Query(
        value = """
        SELECT * FROM category WHERE isAvailable = :isAvailable ORDER BY categoryId ASC
    """,
    )
    fun getAllCategories(isAvailable: Boolean = true): Flow<List<CategoryEntity>>

    @Query(
        """
        SELECT product.categoryId, product.productId, productName, productPrice, product.tags,
        COALESCE(cart.quantity, 0) as quantity
        FROM product
        LEFT JOIN cart ON product.productId = cart.productId AND cart.orderId = :orderId
        """,
    )
    fun getProductWithQty(orderId: Int?): Flow<List<ProductWithQuantity>>

    @Query(
        value = """
        SELECT * FROM selected LIMIT 1
    """,
    )
    fun getSelectedOrder(): Flow<SelectedEntity?>

    @Query(
        value = """
        SELECT addressName FROM address INNER JOIN cartorder ON address.addressId is cartorder.addressId WHERE cartorder.orderId = :orderId
    """,
    )
    fun getSelectedOrderAddress(orderId: Int): String?

    @Query(
        value = """
        SELECT * FROM product_with_quantity WHERE CASE WHEN :categoryId != 0 THEN categoryId = :categoryId ELSE 1 END
    """,
    )
    fun getProductWithQtyView(categoryId: Int = 0): Flow<List<ProductWIthQuantityView>>
}
