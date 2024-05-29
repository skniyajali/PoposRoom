/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
        """,
    )
    fun getCartPriceByOrderId(orderId: Int): CartPriceEntity
}
