package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.niyaj.database.model.AddOnItemEntity
import com.niyaj.database.model.AddressEntity
import com.niyaj.database.model.CartItemDto
import com.niyaj.database.model.ChargesEntity
import com.niyaj.database.model.CustomerEntity
import com.niyaj.database.model.OrderDto
import com.niyaj.database.model.ProductEntity
import com.niyaj.model.OrderStatus
import com.niyaj.model.OrderType
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Transaction
    @Query(
        value = """
        SELECT * FROM cartorder WHERE orderStatus = :orderStatus 
        AND updatedAt BETWEEN :startDate AND :endDate ORDER BY createdAt DESC
    """
    )
    fun getAllOrders(
        startDate: Long,
        endDate: Long,
        orderStatus: OrderStatus = OrderStatus.PLACED
    ): Flow<List<OrderDto>>


    @Transaction
    @Query(
        value = """
        SELECT * FROM cartorder WHERE orderStatus = :orderStatus AND orderType = :orderType
        AND updatedAt BETWEEN :startDate AND :endDate ORDER BY createdAt DESC
    """
    )
    fun getAllOrders(
        startDate: Long,
        endDate: Long,
        orderType: OrderType,
        orderStatus: OrderStatus = OrderStatus.PLACED
    ): Flow<List<OrderDto>>

    @Transaction
    @Query(
        value = """
        SELECT * FROM cartorder WHERE orderId = :orderId
    """
    )
    fun getOrderDetails(orderId: Int): Flow<CartItemDto>


    // ----------------------------------------------------------------

    @Query(
        value = """
        SELECT * FROM addonitem WHERE itemId = :itemId
    """
    )
    fun getAddOnItemById(itemId: Int): AddOnItemEntity

    @Query(
        value = """
        SELECT * FROM charges WHERE chargesId = :chargesId
    """
    )
    fun getChargesById(chargesId: Int): ChargesEntity

    @Query(
        value = """
        SELECT shortName FROM address WHERE addressId = :addressId
    """
    )
    suspend fun getAddressNameById(addressId: Int): String?

    @Query(
        value = """
        SELECT customerPhone FROM customer WHERE customerId = :customerId
    """
    )
    suspend fun getCustomerPhoneById(customerId: Int): String?

    @Query(
        value = """
        SELECT * FROM product WHERE productId = :productId
    """
    )
    suspend fun getProductById(productId: Int): ProductEntity

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
        SELECT * FROM charges
    """
    )
    fun getAllCharges(): Flow<List<ChargesEntity>>

}