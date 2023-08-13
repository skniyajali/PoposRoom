package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import androidx.room.Upsert
import com.niyaj.database.model.AddressEntity
import com.niyaj.database.model.CategoryEntity
import com.niyaj.database.model.CustomerEntity
import com.niyaj.database.model.OrderDto
import com.niyaj.database.model.ProductEntity
import com.niyaj.database.model.ProductWiseOrderDto
import com.niyaj.database.model.ReportsEntity
import com.niyaj.model.OrderStatus
import com.niyaj.model.OrderType
import com.niyaj.model.TotalExpenses
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportsDao {

    @Query(
        value = """
            SELECT * FROM reports WHERE reportDate = :reportDate
        """
    )
    fun getReportByReportDate(reportDate: String): Flow<ReportsEntity?>

    @Query(
        value = """ 
            SELECT reportId FROM reports WHERE reportDate = :reportDate
        """
    )
    suspend fun findReportExists(reportDate: String): Int?

    @Query(
        value = """
            SELECT * FROM reports WHERE createdAt <= :startDate ORDER BY reportId DESC
        """
    )
    fun getReports(startDate: String): Flow<List<ReportsEntity>>

    @Upsert
    fun updateOrInsertReport(report: ReportsEntity): Long

    @Transaction
    @Query(
        value = """
            SELECT COUNT(*) as totalQuantity, SUM(expenseAmount) as totalExpenses FROM expense
            WHERE createdAt BETWEEN :startDate AND :endDate
        """
    )
    suspend fun getTotalExpenses(startDate: Long, endDate: Long): TotalExpenses


    @Transaction
    @Query(
        value = """
            SELECT * FROM cartorder WHERE createdAt BETWEEN :startDate 
            AND :endDate AND orderStatus = :orderStatus AND orderType = :orderType
        """
    )
    suspend fun getTotalDineInOrders(
        startDate: Long,
        endDate: Long,
        orderStatus: OrderStatus = OrderStatus.PLACED,
        orderType: OrderType = OrderType.DineIn,
    ): List<OrderDto>


    @Transaction
    @Query(
        value = """
            SELECT * FROM cartorder WHERE createdAt BETWEEN :startDate 
            AND :endDate AND orderStatus = :orderStatus AND orderType = :orderType
        """
    )
    suspend fun getTotalDineOutOrders(
        startDate: Long,
        endDate: Long,
        orderStatus: OrderStatus = OrderStatus.PLACED,
        orderType: OrderType = OrderType.DineOut,
    ): List<OrderDto>


    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        value = """
            SELECT * FROM cartorder WHERE createdAt BETWEEN :startDate 
            AND :endDate AND orderStatus = :orderStatus
        """
    )
    fun getProductWiseOrder(
        startDate: Long,
        endDate: Long,
        orderStatus: OrderStatus = OrderStatus.PLACED,
    ): Flow<List<ProductWiseOrderDto>>

    @Transaction
    @Query(
        value = """
            SELECT addressId FROM cartorder WHERE createdAt BETWEEN :startDate 
            AND :endDate AND orderStatus = :orderStatus AND orderType = :orderType
        """
    )
    fun getAddressWiseOrder(
        startDate: Long,
        endDate: Long,
        orderType: OrderType = OrderType.DineOut,
        orderStatus: OrderStatus = OrderStatus.PLACED,
    ): Flow<List<Int>>


    @Transaction
    @Query(
        value = """
            SELECT customerId FROM cartorder WHERE createdAt BETWEEN :startDate 
            AND :endDate AND orderStatus = :orderStatus AND orderType = :orderType
        """
    )
    fun getCustomerWiseOrder(
        startDate: Long,
        endDate: Long,
        orderType: OrderType = OrderType.DineOut,
        orderStatus: OrderStatus = OrderStatus.PLACED,
    ): Flow<List<Int>>


    @Query(
        value = """
            SELECT * FROM address WHERE addressId = :addressId
        """
    )
    suspend fun getAddressById(addressId: Int): AddressEntity

    @Query(
        value = """
            SELECT * FROM customer WHERE customerId = :customerId
        """
    )
    suspend fun getCustomerById(customerId: Int): CustomerEntity


    @Query(
        value = """
            SELECT categoryId FROM product WHERE productId = :productId
        """
    )
    suspend fun getProductCategoryById(productId: Int): Int


    @Query(
        value = """
            SELECT productName FROM product WHERE productId = :productId
        """
    )
    suspend fun getProductNameById(productId: Int): String

    @Query(
        value = """
            SELECT * FROM category WHERE categoryId = :categoryId
        """
    )
    suspend fun getCategoryById(categoryId: Int): CategoryEntity
}