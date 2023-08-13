package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.niyaj.database.model.CartPriceEntity

@Dao
interface CartPriceDao {

    @Insert(entity = CartPriceEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreCartPrice(cartPriceEntity: CartPriceEntity): Long

    @Update(entity = CartPriceEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCartPrice(cartPriceEntity: CartPriceEntity): Int

    @Query(
        value = """
            SELECT * FROM cart_price WHERE orderId = :orderId
        """
    )
    fun getCartPriceByOrderId(orderId: Int): CartPriceEntity
}