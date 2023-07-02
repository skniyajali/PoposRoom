package com.niyaj.poposroom.features.order.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.niyaj.poposroom.features.address.domain.model.Address
import com.niyaj.poposroom.features.cart.domain.model.OrderWithCart
import com.niyaj.poposroom.features.cart_order.domain.utils.OrderStatus
import com.niyaj.poposroom.features.charges.domain.model.Charges
import com.niyaj.poposroom.features.customer.domain.model.Customer
import com.niyaj.poposroom.features.product.domain.model.Product
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
    ): Flow<List<OrderWithCart>>

    @Transaction
    @Query(value = """
        SELECT * FROM cartorder WHERE orderId = :orderId
    """)
    fun getOrderDetails(orderId: Int): Flow<OrderWithCart>


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
    suspend fun getProductById(productId: Int): Product

    @Query(value = """
        SELECT * FROM address WHERE addressId = :addressId
    """)
    suspend fun getAddressById(addressId: Int): Address

    @Query(value = """
        SELECT * FROM customer WHERE customerId = :customerId
    """)
    suspend fun getCustomerById(customerId: Int): Customer

    @Query(value = """
        SELECT * FROM charges
    """)
    fun getAllCharges(): Flow<List<Charges>>

}