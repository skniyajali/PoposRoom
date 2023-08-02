package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.niyaj.database.model.AddOnItemEntity
import com.niyaj.database.model.CartAddOnItemsEntity
import com.niyaj.database.model.CartChargesEntity
import com.niyaj.database.model.CartOrderEntity
import com.niyaj.database.model.ChargesEntity
import com.niyaj.database.model.OrderWithCartDto
import com.niyaj.model.AddOnPriceWithApplicable
import com.niyaj.model.ChargesPriceWithApplicable
import com.niyaj.model.OrderStatus
import com.niyaj.model.ProductPriceWithQuantity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface CartOrderDao {

    @Query(value = """
        SELECT orderId FROM cartorder ORDER BY orderId DESC LIMIT 1
    """
    )
    suspend fun getLastCreatedOrderId(): Int?

    @Query(value = """
        SELECT orderId FROM cartorder WHERE orderStatus = :orderStatus ORDER BY orderId DESC LIMIT 1
    """
    )
    suspend fun getLastProcessingId(orderStatus: OrderStatus = OrderStatus.PROCESSING): Int?

    @Query(value = """
        SELECT orderStatus FROM cartorder WHERE orderId = :cartOrderId
    """
    )
    suspend fun getOrderStatus(cartOrderId: Int): OrderStatus

    // --------------------------------

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
    fun getAllChargesPrice(): List<ChargesPriceWithApplicable>

    @Transaction
    @Query(value = """
        SELECT itemId FROM cart_addon_items WHERE orderId = :orderId ORDER BY createdAt DESC
    """)
    fun getCartAddOnItemsId(orderId: Int): Flow<List<Int>>

    @Transaction
    @Query(value = """
        SELECT chargesId FROM cart_charges WHERE orderId = :orderId ORDER BY createdAt DESC
    """)
    fun getCartChargesId(orderId: Int): Flow<List<Int>>

    @Transaction
    @Query(value = """
        SELECT itemId FROM cart_addon_items WHERE orderId = :orderId ORDER BY createdAt DESC
    """)
    fun getCartAddOnItems(orderId: Int): List<Int>

    @Transaction
    @Query(value = """
        SELECT chargesId FROM cart_charges WHERE orderId = :orderId ORDER BY createdAt DESC
    """)
    fun getCartCharges(orderId: Int): List<Int>

    @Transaction
    @Query(value = """
        SELECT * FROM addonitem WHERE itemId = :itemId
    """)
    fun getAddOnItemById(itemId: Int): AddOnItemEntity

    @Transaction
    @Query(value = """
        SELECT * FROM charges WHERE chargesId = :chargesId
    """)
    fun getChargesById(chargesId: Int): ChargesEntity

    @Insert(entity = CartAddOnItemsEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartAddOnItem(items: CartAddOnItemsEntity): Long

    @Query(value = """
        DELETE FROM cart_addon_items WHERE orderId = :orderId AND itemId = :itemId
    """)
    suspend fun deleteCartAddOnItem(orderId: Int, itemId: Int): Int

    @Query(value = """
        SELECT * FROM cart_addon_items WHERE orderId = :orderId AND itemId = :itemId
    """)
    suspend fun getCartAddOnItemById(orderId: Int, itemId: Int): CartAddOnItemsEntity?

    @Insert(entity = CartChargesEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartCharge(items: CartChargesEntity): Long

    @Query(value = """
        SELECT * FROM cart_charges WHERE orderId = :orderId AND chargesId = :chargesId
    """)
    suspend fun getCartChargesById(orderId: Int, chargesId: Int): CartChargesEntity?

    @Query(value = """
        DELETE FROM cart_charges WHERE orderId = :orderId AND chargesId = :chargesId
    """)
    suspend fun deleteCartCharges(orderId: Int, chargesId: Int): Int

    //    ----------------------------------------------------------------

    @Transaction
    @Query(value = """
        SELECT * FROM cartorder WHERE orderId = :orderId
    """)
    suspend fun getCartProductsByOrderId(
        orderId: Int
    ): OrderWithCartDto


    @Query("""
        SELECT cart.quantity AS quantity, product.productPrice AS productPrice 
        FROM cart INNER JOIN product ON cart.productId = product.productId 
        WHERE cart.orderId = :orderId AND cart.productId = :productId
    """)
    fun getProductPriceAndQuantity(orderId: Int, productId: Int): ProductPriceWithQuantity



    // ----------------

    @Query(value = """
        SELECT * FROM cartorder WHERE orderStatus = :orderStatus ORDER BY createdAt DESC
    """)
    fun getProcessingCartOrders(orderStatus: OrderStatus = OrderStatus.PROCESSING): Flow<List<CartOrderEntity>>

    @Query(value = """
        SELECT * FROM cartorder ORDER BY createdAt DESC
    """)
    fun getAllCartOrders(): Flow<List<CartOrderEntity>>


    @Query(value = """
        SELECT * FROM cartorder WHERE orderId = :orderId
    """
    )
    suspend fun getCartOrderById(orderId: Int): CartOrderEntity?

    @Upsert
    suspend fun createOrUpdateCartOrder(newOrder: CartOrderEntity): Long

    @Query(value = """
        DELETE FROM cartorder WHERE orderId = :orderId
    """
    )
    suspend fun deleteCartOrder(orderId: Int): Int

    /**
     * Deletes rows in the db matching the specified [orderIds]
     */
    @Query(
        value = """
            DELETE FROM cartorder WHERE orderId in (:orderIds)
        """,
    )
    suspend fun deleteCartOrders(orderIds: List<Int>): Int

    @Query(value = """
        UPDATE cartorder SET updatedAt = :updatedAt, orderStatus = :status WHERE orderId = :orderId
    """)
    suspend fun markAsProcessing(
        orderId: Int,
        status: OrderStatus = OrderStatus.PROCESSING,
        updatedAt: Date = Date()
    ): Int

    @Query(value = """
        UPDATE cartorder SET updatedAt = :updatedAt, orderStatus = :status WHERE orderId = :orderId
    """)
    suspend fun placeOrder(
        orderId: Int,
        status: OrderStatus = OrderStatus.PLACED,
        updatedAt: Date = Date()
    ): Int

    @Query(value = """
        UPDATE cartorder SET orderStatus = :status, updatedAt = :updatedAt WHERE orderId IN (:orderIds)
    """)
    suspend fun placeAllOrder(
        orderIds: List<Int>,
        status: OrderStatus = OrderStatus.PLACED,
        updatedAt: Date = Date()
    ): Int
}