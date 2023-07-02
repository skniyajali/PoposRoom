package com.niyaj.poposroom.features.cart_order.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.niyaj.poposroom.features.addon_item.domain.model.AddOnItem
import com.niyaj.poposroom.features.addon_item.domain.model.AddOnPriceWithApplicable
import com.niyaj.poposroom.features.cart.domain.model.OrderWithCart
import com.niyaj.poposroom.features.cart.domain.model.ProductPriceWithQuantity
import com.niyaj.poposroom.features.cart_order.domain.model.CartAddOnItems
import com.niyaj.poposroom.features.cart_order.domain.model.CartCharges
import com.niyaj.poposroom.features.cart_order.domain.model.CartOrderEntity
import com.niyaj.poposroom.features.cart_order.domain.utils.OrderStatus
import com.niyaj.poposroom.features.charges.domain.model.Charges
import com.niyaj.poposroom.features.charges.domain.model.ChargesPriceWithApplicable
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
    fun getAddOnItemById(itemId: Int): AddOnItem

    @Transaction
    @Query(value = """
        SELECT * FROM charges WHERE chargesId = :chargesId
    """)
    fun getChargesById(chargesId: Int): Charges

    @Insert(entity = CartAddOnItems::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartAddOnItem(items: CartAddOnItems): Long

    @Query(value = """
        DELETE FROM cart_addon_items WHERE orderId = :orderId AND itemId = :itemId
    """)
    suspend fun deleteCartAddOnItem(orderId: Int, itemId: Int): Int

    @Query(value = """
        SELECT * FROM cart_addon_items WHERE orderId = :orderId AND itemId = :itemId
    """)
    suspend fun getCartAddOnItemById(orderId: Int, itemId: Int): CartAddOnItems?

    @Insert(entity = CartCharges::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartCharge(items: CartCharges): Long

    @Query(value = """
        SELECT * FROM cart_charges WHERE orderId = :orderId AND chargesId = :chargesId
    """)
    suspend fun getCartChargesById(orderId: Int, chargesId: Int): CartCharges?

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
    ): OrderWithCart


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
    fun getAllCartOrders(orderStatus: OrderStatus = OrderStatus.PROCESSING): Flow<List<CartOrderEntity>>

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