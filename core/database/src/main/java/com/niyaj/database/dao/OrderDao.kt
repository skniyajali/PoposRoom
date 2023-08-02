package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.niyaj.database.model.AddressEntity
import com.niyaj.database.model.ChargesEntity
import com.niyaj.database.model.CustomerEntity
import com.niyaj.database.model.OrderWithCartDto
import com.niyaj.database.model.ProductEntity
import com.niyaj.model.OrderStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Transaction
    @Query(value = """
        SELECT * FROM cartorder WHERE orderStatus = :orderStatus 
        AND updatedAt BETWEEN :startDate AND :endDate ORDER BY createdAt DESC
    """)
    fun getAllOrders(
        startDate: Long,
        endDate: Long,
        orderStatus: OrderStatus = OrderStatus.PLACED
    ): Flow<List<OrderWithCartDto>>

    @Transaction
    @Query(value = """
        SELECT * FROM cartorder WHERE orderId = :orderId
    """)
    fun getOrderDetails(orderId: Int): Flow<OrderWithCartDto>


    // ----------------------------------------------------------------

    @Query(value = """
        SELECT productPrice FROM product WHERE productId = :productId
    """)
    suspend fun getProductPriceById(productId: Int): Int

    @Query(value = """
        SELECT shortName FROM address WHERE addressId = :addressId
    """)
    suspend fun getAddressNameById(addressId: Int): String?

    @Query(value = """
        SELECT customerPhone FROM customer WHERE customerId = :customerId
    """)
    suspend fun getCustomerPhoneById(customerId: Int): String?

    @Query(value = """
        SELECT * FROM product WHERE productId = :productId
    """)
    suspend fun getProductById(productId: Int): ProductEntity

    @Query(value = """
        SELECT * FROM address WHERE addressId = :addressId
    """)
    suspend fun getAddressById(addressId: Int): AddressEntity

    @Query(value = """
        SELECT * FROM customer WHERE customerId = :customerId
    """)
    suspend fun getCustomerById(customerId: Int): CustomerEntity

    @Query(value = """
        SELECT * FROM charges
    """)
    fun getAllCharges(): Flow<List<ChargesEntity>>

}