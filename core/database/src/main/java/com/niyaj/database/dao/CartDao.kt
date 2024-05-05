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
import com.niyaj.database.model.AddOnItemEntity
import com.niyaj.database.model.CartAddOnItemsEntity
import com.niyaj.database.model.CartEntity
import com.niyaj.database.model.CartItemDto
import com.niyaj.database.model.ProductEntity
import com.niyaj.model.AddOnPriceWithApplicable
import com.niyaj.model.ChargesPriceWithApplicable
import com.niyaj.model.OrderStatus
import com.niyaj.model.OrderType
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface CartDao {

    @Transaction
    @Query(
        value = """
        SELECT * FROM cartorder WHERE orderType = :orderType 
        AND orderStatus = :orderStatus ORDER BY createdAt DESC
    """,
    )
    fun getAllOrders(
        orderType: OrderType,
        orderStatus: OrderStatus = OrderStatus.PROCESSING,
    ): Flow<List<CartItemDto>>

    @Query(
        value = """
        SELECT quantity FROM cart WHERE orderId = :orderId AND productId = :productId
    """,
    )
    fun getProductQuantity(orderId: Int, productId: Int): Flow<Int>

    @Query(
        value = """
        SELECT * FROM cart WHERE orderId = :orderId AND productId = :productId LIMIT 1
    """,
    )
    suspend fun getCartOrderById(orderId: Int, productId: Int): CartEntity?

    @Query(
        value = """
        UPDATE cart SET quantity = :quantity, updatedAt = :updatedAt WHERE orderId = :orderId AND productId = :productId
    """,
    )
    suspend fun updateQuantity(
        orderId: Int,
        productId: Int,
        quantity: Int,
        updatedAt: Date = Date(),
    ): Int

    @Upsert
    suspend fun addOrRemoveCartProduct(cartEntity: CartEntity): Long

    @Query(
        value = """
        DELETE FROM cart WHERE orderId = :orderId AND productId = :productId
    """,
    )
    suspend fun deleteProductFromCart(orderId: Int, productId: Int): Int


    @Insert(entity = CartAddOnItemsEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartAddOnItem(items: CartAddOnItemsEntity): Long

    @Query(
        value = """
        DELETE FROM cart_addon_items WHERE orderId = :orderId AND itemId = :itemId
    """,
    )
    suspend fun deleteCartAddOnItem(orderId: Int, itemId: Int): Int

    @Query(
        value = """
        SELECT * FROM product WHERE productId = :productId LIMIT 1
    """,
    )
    suspend fun getProductById(productId: Int): ProductEntity

    @Query(
        value = """
        SELECT shortName FROM address WHERE addressId = :addressId
    """,
    )
    suspend fun getAddressById(addressId: Int): String?

    @Query(
        value = """
        SELECT customerPhone FROM customer WHERE customerId = :customerId
    """,
    )
    suspend fun getCustomerById(customerId: Int): String?

    @Query(
        value = """
        SELECT itemPrice, isApplicable FROM addonitem WHERE itemId = :itemId
    """,
    )
    suspend fun getAddOnPrice(itemId: Int): AddOnPriceWithApplicable

    @Query(
        value = """
        SELECT chargesPrice, isApplicable FROM charges WHERE chargesId = :chargesId
    """,
    )
    suspend fun getChargesPrice(chargesId: Int): ChargesPriceWithApplicable

    @Query(
        value = """
        SELECT * FROM addonitem
    """,
    )
    fun getAllAddOnItems(): Flow<List<AddOnItemEntity>>


    @Query(
        value = """
        SELECT * FROM product WHERE productId IN (:productIds)
       """,
    )
    suspend fun getProductsById(productIds: List<Int>): List<ProductEntity>

}