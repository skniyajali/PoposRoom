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
import com.niyaj.database.model.CartItemDto
import com.niyaj.database.model.OrderDetailsDto
import com.niyaj.database.model.ProductEntity
import com.niyaj.database.model.ProfileEntity
import com.niyaj.model.ChargesNameAndPrice
import com.niyaj.model.DeliveryReport
import com.niyaj.model.OrderStatus
import com.niyaj.model.OrderType
import kotlinx.coroutines.flow.Flow

@Dao
interface PrintDao {

    @Query(
        """
            SELECT co.orderId,
            COALESCE(cu.customerPhone, null) as customerPhone,
            COALESCE(ad.shortName, null) as customerAddress,
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
    suspend fun getDeliveryReport(
        startDate: Long,
        endDate: Long,
        partnerId: Int? = null,
        orderType: OrderType = OrderType.DineOut,
        orderStatus: OrderStatus = OrderStatus.PLACED,
    ): List<DeliveryReport>

    @Transaction
    @Query(
        value = """
        SELECT * FROM cartorder WHERE orderId = :orderId
    """,
    )
    suspend fun getOrderDetails(orderId: Int): CartItemDto

    @Transaction
    @Query(
        value = """
        SELECT * FROM cartorder WHERE orderId = :orderId
    """,
    )
    fun getFullOrderDetails(orderId: Int): OrderDetailsDto

    @Query(
        value = """
        SELECT chargesName, chargesPrice FROM charges WHERE isApplicable = :isApplicable ORDER BY createdAt DESC
    """,
    )
    suspend fun getAllCharges(isApplicable: Boolean = true): List<ChargesNameAndPrice>

    @Query(
        value = """
        SELECT * FROM profile WHERE restaurantId = :restaurantId
    """,
    )
    fun getProfileInfo(restaurantId: Int): Flow<ProfileEntity?>

    @Query(
        value = """
        SELECT * FROM product WHERE productId = :productId
    """,
    )
    suspend fun getProductById(productId: Int): ProductEntity
}
