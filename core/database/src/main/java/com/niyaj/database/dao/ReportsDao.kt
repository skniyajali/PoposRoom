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
import androidx.room.Upsert
import com.niyaj.database.model.ReportsEntity
import com.niyaj.model.AddressWiseReport
import com.niyaj.model.CategoryWithProduct
import com.niyaj.model.CustomerWiseReport
import com.niyaj.model.ExpensesReport
import com.niyaj.model.OrderStatus
import com.niyaj.model.OrderType
import com.niyaj.model.ProductWiseReport
import com.niyaj.model.TotalExpenses
import com.niyaj.model.TotalOrders
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportsDao {

    @Query(
        value = """
            SELECT * FROM reports WHERE reportDate = :reportDate
        """,
    )
    fun getReportByReportDate(reportDate: String): Flow<ReportsEntity?>

    @Query(
        value = """ 
            SELECT reportId FROM reports WHERE reportDate = :reportDate
        """,
    )
    suspend fun findReportExists(reportDate: String): Int?

    @Query(
        value = """
            SELECT * FROM reports ORDER BY reportId DESC
        """,
    )
    fun getReports(): Flow<List<ReportsEntity>>

    @Upsert
    fun updateOrInsertReport(report: ReportsEntity): Long

    @Transaction
    @Query(
        value = """
            SELECT COUNT(*) as totalQuantity, SUM(expenseAmount) as totalExpenses FROM expense
            WHERE expenseDate BETWEEN :startDate AND :endDate
        """,
    )
    suspend fun getTotalExpenses(startDate: Long, endDate: Long): TotalExpenses

    @Transaction
    @Query(
        value = """
            SELECT COUNT(co.orderId) AS totalOrders, SUM(cp.totalPrice) AS totalAmount FROM cartorder co
            JOIN cart_price cp ON co.orderId = cp.orderId
            WHERE orderStatus = :orderStatus AND orderType = :orderType 
            AND (co.createdAt BETWEEN :startDate AND :endDate  
            OR updatedAt BETWEEN :startDate AND :endDate)
        """,
    )
    suspend fun getTotalOrders(
        startDate: Long,
        endDate: Long,
        orderType: OrderType,
        orderStatus: OrderStatus = OrderStatus.PLACED,
    ): TotalOrders

    @Query(
        value = """
            SELECT p.productId, p.productName, SUM(c.quantity) AS quantity 
            
            FROM product p
            JOIN cart c ON p.productId = c.productId
            JOIN cartorder co ON co.orderId = c.orderId
            
            WHERE CASE WHEN :orderType != null THEN co.orderType = :orderType else 1 END 
            AND co.orderStatus = :orderStatus
            AND (co.createdAt BETWEEN :startDate AND :endDate OR co.updatedAt BETWEEN :startDate AND :endDate)
            
            GROUP BY p.productId ORDER BY quantity DESC
        """,
    )
    fun getProductWiseOrders(
        startDate: Long,
        endDate: Long,
        orderType: String?,
        orderStatus: OrderStatus = OrderStatus.PLACED,
    ): Flow<List<ProductWiseReport>>

    @Query(
        value = """
            SELECT cat.categoryName, p.productId, p.productName, SUM(c.quantity) AS quantity
            
            FROM product p
            JOIN cart c ON p.productId = c.productId
            JOIN cartorder co ON co.orderId = c.orderId
            JOIN categorywithproductcrossref cp ON cp.productId = p.productId
            JOIN category cat ON cat.categoryId = cp.categoryId
            
            WHERE co.orderStatus = :orderStatus 
            AND CASE WHEN :orderType IS NOT null THEN co.orderType = :orderType else 1 END
            AND (co.createdAt BETWEEN :startDate AND :endDate OR co.updatedAt BETWEEN :startDate AND :endDate)
            
            GROUP BY cat.categoryName, p.productId
            ORDER BY cat.categoryName, quantity DESC
        """,
    )
    fun getCategoryWiseOrders(
        startDate: Long,
        endDate: Long,
        orderType: String? = null,
        orderStatus: OrderStatus = OrderStatus.PLACED,
    ): Flow<List<CategoryWithProduct>>

    @Transaction
    @Query(
        value = """
            SELECT ad.addressId, ad.addressName, ad.shortName, COUNT(DISTINCT co.orderId) As totalOrders, SUM(cp.totalPrice) As totalSales  
            FROM address ad
            JOIN cartorder co ON ad.addressId = co.addressId
            JOIN cart_price cp ON cp.orderId = co.orderId
            WHERE co.orderStatus = :orderStatus AND co.orderType = :orderType 
            AND (co.createdAt BETWEEN :startDate AND :endDate OR co.updatedAt BETWEEN :startDate AND :endDate)
            GROUP BY ad.addressId
            ORDER BY totalOrders DESC
        """,
    )
    fun getAddressWiseOrders(
        startDate: Long,
        endDate: Long,
        orderType: OrderType = OrderType.DineOut,
        orderStatus: OrderStatus = OrderStatus.PLACED,
    ): Flow<List<AddressWiseReport>>

    @Transaction
    @Query(
        value = """
            SELECT cu.customerId, cu.customerPhone, cu.customerEmail, cu.customerName, 
            COUNT(DISTINCT co.orderId) As totalOrders, SUM(cp.totalPrice) As totalSales  
            
            FROM customer cu
            JOIN cartorder co ON cu.customerId = co.customerId
            JOIN cart_price cp ON cp.orderId = co.orderId
            
            WHERE co.orderStatus = :orderStatus AND co.orderType = :orderType 
            AND (co.createdAt BETWEEN :startDate AND :endDate OR co.updatedAt BETWEEN :startDate AND :endDate)
            
            GROUP BY cu.customerId
            ORDER BY totalOrders DESC
        """,
    )
    fun getCustomerWiseOrder(
        startDate: Long,
        endDate: Long,
        orderType: OrderType = OrderType.DineOut,
        orderStatus: OrderStatus = OrderStatus.PLACED,
    ): Flow<List<CustomerWiseReport>>

    @Query(
        value = """
            SELECT expenseId, expenseAmount, expenseName FROM expense
            WHERE expenseDate BETWEEN :startDate AND :endDate ORDER BY expenseAmount DESC
        """,
    )
    fun getExpensesReport(
        startDate: Long,
        endDate: Long,
    ): Flow<List<ExpensesReport>>
}
