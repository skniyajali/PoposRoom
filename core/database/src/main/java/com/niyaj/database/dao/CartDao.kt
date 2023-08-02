package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.niyaj.database.model.AddOnItemEntity
import com.niyaj.database.model.CartEntity
import com.niyaj.database.model.OrderWithCartDto
import com.niyaj.database.model.ProductEntity
import com.niyaj.model.AddOnPriceWithApplicable
import com.niyaj.model.ChargesPriceWithApplicable
import com.niyaj.model.OrderStatus
import com.niyaj.model.OrderType
import com.niyaj.model.ProductPriceWithQuantity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Transaction
    @Query(value = """
        SELECT * FROM cartorder WHERE orderStatus = :orderStatus ORDER BY createdAt DESC
    """)
    fun getAllCartOrders(orderStatus: OrderStatus = OrderStatus.PROCESSING): Flow<List<OrderWithCartDto>>


    @Transaction
    @Query(value = """
        SELECT * FROM cartorder WHERE orderType = :orderType 
        AND orderStatus = :orderStatus ORDER BY createdAt DESC
    """)
    fun getAllOrders(
        orderType: OrderType,
        orderStatus: OrderStatus = OrderStatus.PROCESSING
    ): Flow<List<OrderWithCartDto>>


    @Transaction
    @Query(value = """
        SELECT * FROM cartorder WHERE orderId = :orderId
    """)
    fun getCartProductsByOrderId(
        orderId: Int
    ): Flow<OrderWithCartDto>


    @Query(value = """
        SELECT quantity FROM cart WHERE orderId = :orderId AND productId = :productId
    """
    )
    fun getProductQuantity(orderId: Int, productId: Int): Flow<Int>

    @Query("""
        SELECT cart.quantity AS quantity, product.productPrice AS productPrice 
        FROM cart INNER JOIN product ON cart.productId = product.productId 
        WHERE cart.orderId = :orderId AND cart.productId = :productId
    """)
    fun getProductPriceAndQuantity(orderId: Int, productId: Int): Flow<ProductPriceWithQuantity>

    @Query(value = """
        SELECT * FROM cart WHERE orderId = :orderId AND productId = :productId LIMIT 1
    """
    )
    suspend fun getCartOrderById(orderId: Int, productId: Int): CartEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreProductToCart(cartEntity: CartEntity): Long

    @Query(value = """
        UPDATE cart SET quantity = :quantity WHERE orderId = :orderId AND productId = :productId
    """
    )
    suspend fun updateQuantity(orderId: Int, productId: Int, quantity: Int): Int

    @Upsert
    suspend fun addOrRemoveCartProduct(cartEntity: CartEntity): Long

    @Query(value = """
        DELETE FROM cart WHERE orderId = :orderId AND productId = :productId
    """
    )
    suspend fun deleteProductFromCart(orderId: Int, productId: Int): Int

    @Query(value = """
        SELECT * FROM product WHERE productId = :productId LIMIT 1
    """)
    suspend fun getProductById(productId: Int): ProductEntity

    @Query(value = """
        SELECT shortName FROM address WHERE addressId = :addressId
    """)
    suspend fun getAddressById(addressId: Int): String?

    @Query(value = """
        SELECT customerPhone FROM customer WHERE customerId = :customerId
    """)
    suspend fun getCustomerById(customerId: Int): String?

    @Query(value = """
        SELECT itemPrice, isApplicable FROM addonitem WHERE itemId IN (:items)
    """)
    suspend fun getAddOnPrice(items: List<Int>): List<AddOnPriceWithApplicable>

    @Query(value = """
        SELECT chargesPrice, isApplicable FROM charges WHERE chargesId IN (:charges)
    """)
    suspend fun getChargesPrice(charges: List<Int>): List<ChargesPriceWithApplicable>

    @Query(value = """
        SELECT chargesPrice, isApplicable FROM charges
    """)
    fun getAllCharges(): Flow<List<ChargesPriceWithApplicable>>

    @Query(value = """
        SELECT * FROM addonitem
    """)
    fun getAllAddOnItems(): Flow<List<AddOnItemEntity>>
}