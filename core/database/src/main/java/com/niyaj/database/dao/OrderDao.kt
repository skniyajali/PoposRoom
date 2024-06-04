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
import androidx.room.Transaction
import com.niyaj.database.model.ChargesEntity
import com.niyaj.database.model.OrderDetailsDto
import com.niyaj.database.model.ProductEntity
import com.niyaj.model.DeliveryReport
import com.niyaj.model.Order
import com.niyaj.model.OrderStatus
import com.niyaj.model.OrderType
import com.niyaj.model.TotalDeliveryPartnerOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query(
        """
            SELECT co.orderId, co.orderType,
            COALESCE(cu.customerPhone, null) as customerPhone,
            COALESCE(ad.shortName, null) as customerAddress,
            COALESCE(em.employeeName, null) as deliveryPartnerName,
            COALESCE(em.employeeId, 0) as deliveryPartnerId,
            COALESCE(co.updatedAt, co.createdAt) as orderDate,
            cp.totalPrice as orderPrice
            FROM cartorder co
            JOIN cart_price cp ON cp.orderId = co.orderId
            LEFT JOIN customer cu ON cu.customerId = co.customerId
            LEFT JOIN address ad ON ad.addressId = co.addressId
            LEFT JOIN employee em ON em.employeeId = co.deliveryPartnerId
            WHERE (co.updatedAt BETWEEN :startDate AND :endDate)
            AND co.orderStatus = :orderStatus AND co.orderType = :orderType
            ORDER BY co.updatedAt DESC
        """,
    )
    fun getAllOrder(
        startDate: Long,
        endDate: Long,
        orderType: OrderType,
        orderStatus: OrderStatus = OrderStatus.PLACED,
    ): Flow<List<Order>>

    @Query(
        """
            SELECT COUNT(co.orderId) as totalOrders, SUM(cp.totalPrice) as totalAmount,
            COALESCE(em.employeeId, 0) as partnerId,
            COALESCE(em.employeeName, null) as partnerName
            FROM cartorder co
            JOIN cart_price cp ON cp.orderId = co.orderId
            LEFT JOIN customer cu ON cu.customerId = co.customerId
            LEFT JOIN address ad ON ad.addressId = co.addressId
            LEFT JOIN employee em ON em.employeeId = co.deliveryPartnerId
            WHERE (co.updatedAt BETWEEN :startDate AND :endDate)
            AND co.orderStatus = :orderStatus AND co.orderType = :orderType
            GROUP BY co.deliveryPartnerId
            ORDER BY co.updatedAt DESC 
        """,
    )
    fun getDeliveryPartnerOrders(
        startDate: Long,
        endDate: Long,
        orderType: OrderType = OrderType.DineOut,
        orderStatus: OrderStatus = OrderStatus.PLACED,
    ): Flow<List<TotalDeliveryPartnerOrder>>

    @Query(
        """
            SELECT co.orderId,
            COALESCE(cu.customerPhone, null) as customerPhone,
            COALESCE(ad.addressName, null) as customerAddress,
            COALESCE(em.employeeName, null) as partnerName,
            COALESCE(em.employeeId, 0) as partnerId,
            COALESCE(co.updatedAt, co.createdAt) as orderDate,
            cp.totalPrice as orderPrice
            FROM cartorder co
            JOIN cart_price cp ON cp.orderId = co.orderId
            LEFT JOIN customer cu ON cu.customerId = co.customerId
            LEFT JOIN address ad ON ad.addressId = co.addressId
            LEFT JOIN employee em ON em.employeeId = co.deliveryPartnerId
            WHERE (co.updatedAt BETWEEN :startDate AND :endDate)
            AND co.orderStatus = :orderStatus AND co.orderType = :orderType AND
            CASE WHEN :partnerId IS NOT NULL THEN co.deliveryPartnerId = :partnerId ELSE 1 END
            ORDER BY co.updatedAt DESC
        """,
    )
    fun getPartnerDeliveryReport(
        startDate: Long,
        endDate: Long,
        partnerId: Int? = null,
        orderType: OrderType = OrderType.DineOut,
        orderStatus: OrderStatus = OrderStatus.PLACED,
    ): Flow<List<DeliveryReport>>

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
