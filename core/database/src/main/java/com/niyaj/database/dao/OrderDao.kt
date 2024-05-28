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
import androidx.room.Query
import androidx.room.Transaction
import com.niyaj.database.model.ChargesEntity
import com.niyaj.database.model.OrderDetailsDto
import com.niyaj.database.model.ProductEntity
import com.niyaj.model.Order
import com.niyaj.model.OrderStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query(
        """
            SELECT co.orderId, co.orderType,
            COALESCE(cu.customerPhone, null) as customerPhone,
            COALESCE(ad.shortName, null) as customerAddress,
            COALESCE(co.updatedAt, co.createdAt) as orderDate,
            cp.totalPrice as orderPrice
            FROM cartorder co
            JOIN cart_price cp ON cp.orderId = co.orderId
            LEFT JOIN customer cu ON cu.customerId = co.customerId
            LEFT JOIN address ad ON ad.addressId = co.addressId
            WHERE (co.updatedAt BETWEEN :startDate AND :endDate) AND co.orderStatus = :orderStatus
            ORDER BY co.updatedAt DESC
        """
    )
    fun getAllOrder(
        startDate: Long,
        endDate: Long,
        orderStatus: OrderStatus = OrderStatus.PLACED,
    ): Flow<List<Order>>

    @Transaction
    @Query(
        value = """
        SELECT * FROM cartorder WHERE orderId = :orderId
    """,
    )
    fun getFullOrderDetails(orderId: Int): Flow<OrderDetailsDto>


    // ----------------------------------------------------------------

    @Query(
        value = """
        SELECT * FROM product WHERE productId = :productId
    """,
    )
    suspend fun getProductById(productId: Int): ProductEntity

    @Query(
        value = """
        SELECT * FROM charges
    """,
    )
    fun getAllCharges(): Flow<List<ChargesEntity>>

}