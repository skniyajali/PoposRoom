package com.niyaj.poposroom.features.cart_order.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.niyaj.poposroom.features.cart_order.domain.model.CartAddOnItems
import com.niyaj.poposroom.features.cart_order.domain.model.CartCharges
import com.niyaj.poposroom.features.cart_order.domain.model.CartOrderEntity
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface CartOrderDao {

    @Query(value = """
        SELECT cartOrderId FROM cartorder ORDER BY cartOrderId DESC LIMIT 1
    """)
    suspend fun getLastCreatedOrderId(): Int?

    @Query(value = """
        SELECT cartOrderId FROM cartorder WHERE orderStatus = :orderStatus ORDER BY cartOrderId DESC LIMIT 1
    """)
    suspend fun getLastProcessingId(orderStatus: CartOrderStatus = CartOrderStatus.PROCESSING): Int?

    @Query(value = """
        SELECT orderStatus FROM cartorder WHERE cartOrderId = :cartOrderId
    """)
    suspend fun getOrderStatus(cartOrderId: Int): CartOrderStatus

    @Transaction
    @Query(value = """
        SELECT itemId FROM cart_addon_items WHERE cartOrderId = :cartOrderId ORDER BY createdAt DESC
    """)
    fun getCartAddOnItems(cartOrderId: Int): Flow<List<Int>>

    @Transaction
    @Query(value = """
        SELECT chargesId FROM cart_charges WHERE cartOrderId = :cartOrderId ORDER BY createdAt DESC
    """)
    fun getCartCharges(cartOrderId: Int): Flow<List<Int>>


    @Insert(entity = CartAddOnItems::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCartAddOnItems(items: CartAddOnItems): Long

    @Insert(entity = CartCharges::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCartCharges(items: CartCharges): Long

    @Query(value = """
        SELECT * FROM cart_addon_items WHERE cartOrderId = :cartOrderId AND itemId = :itemId
    """)
    suspend fun getCartAddOnItemById(cartOrderId: Int, itemId: Int): CartAddOnItems?

    @Query(value = """
        SELECT * FROM cart_charges WHERE cartOrderId = :cartOrderId AND chargesId = :chargesId
    """)
    suspend fun getCartChargesById(cartOrderId: Int, chargesId: Int): CartCharges?

    @Query(value = """
        SELECT * FROM cartorder WHERE orderStatus = :orderStatus ORDER BY createdAt DESC
    """)
    fun getAllCartOrders(orderStatus: CartOrderStatus = CartOrderStatus.PROCESSING): Flow<List<CartOrderEntity>>

    @Query(value = """
        SELECT * FROM cartorder WHERE cartOrderId = :cartOrderId
    """)
    suspend fun getCartOrderById(cartOrderId: Int): CartOrderEntity?

    @Upsert
    suspend fun createOrUpdateCartOrder(newOrder: CartOrderEntity): Long

    @Query(value = """
        DELETE FROM cartorder WHERE cartOrderId = :cartOrderId
    """)
    suspend fun deleteCartOrder(cartOrderId: Int): Int

    /**
     * Deletes rows in the db matching the specified [cartOrderIds]
     */
    @Query(
        value = """
            DELETE FROM cartorder WHERE cartOrderId in (:cartOrderIds)
        """,
    )
    suspend fun deleteCartOrders(cartOrderIds: List<Int>): Int

}