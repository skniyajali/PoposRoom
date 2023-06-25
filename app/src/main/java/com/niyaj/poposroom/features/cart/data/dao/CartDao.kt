package com.niyaj.poposroom.features.cart.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.niyaj.poposroom.features.addon_item.domain.model.AddOnItem
import com.niyaj.poposroom.features.addon_item.domain.model.AddOnPriceWithApplicable
import com.niyaj.poposroom.features.address.domain.model.Address
import com.niyaj.poposroom.features.cart.domain.model.CartEntity
import com.niyaj.poposroom.features.cart.domain.model.OrderWithCart
import com.niyaj.poposroom.features.cart.domain.model.ProductPriceWithQuantity
import com.niyaj.poposroom.features.cart_order.domain.utils.OrderStatus
import com.niyaj.poposroom.features.cart_order.domain.utils.OrderType
import com.niyaj.poposroom.features.charges.domain.model.ChargesPriceWithApplicable
import com.niyaj.poposroom.features.customer.domain.model.Customer
import com.niyaj.poposroom.features.product.domain.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Transaction
    @Query(value = """
        SELECT * FROM cartorder WHERE orderStatus = :orderStatus ORDER BY createdAt DESC
    """)
    fun getAllCartOrders(orderStatus: OrderStatus = OrderStatus.PROCESSING): Flow<List<OrderWithCart>>


    @Transaction
    @Query(value = """
        SELECT * FROM cartorder WHERE orderType = :orderType 
        AND orderStatus = :orderStatus ORDER BY createdAt DESC
    """)
    fun getAllOrders(
        orderType: OrderType,
        orderStatus: OrderStatus = OrderStatus.PROCESSING
    ): Flow<List<OrderWithCart>>


    @Transaction
    @Query(value = """
        SELECT * FROM cartorder WHERE orderId = :orderId
    """)
    fun getCartProductsByOrderId(
        orderId: Int
    ): Flow<OrderWithCart>


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
    fun getAllAddOnItems(): Flow<List<AddOnItem>>
}