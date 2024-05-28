/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.database.model.AddressEntity
import com.niyaj.model.AddressWiseOrder
import com.niyaj.model.OrderStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {

    @Query(value = """
        SELECT * FROM address ORDER BY createdAt DESC
    """)
    fun getAllAddresses(): Flow<List<AddressEntity>>

    @Query(value = """
        SELECT * FROM address WHERE addressId = :addressId
    """)
    fun getAddressById(addressId: Int): AddressEntity?

    /**
     * Get addressId from database by [addressName]
     */
    @Query(value = """
        SELECT addressId FROM address WHERE addressName = :addressName
    """
    )
    suspend fun getAddressByName(addressName: String): Int?

    /**
     * Inserts [AddressEntity] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreAddress(address: AddressEntity): Long

    /**
     * Updates [AddressEntity] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateAddress(address: AddressEntity): Int

    /**
     * Inserts or updates [AddressEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertAddress(address: AddressEntity): Long

    @Query(value = """
        DELETE FROM address WHERE addressId = :addressId
    """)
    suspend fun deleteAddress(addressId: Int): Int

    /**
     * Deletes rows in the db matching the specified [addressIds]
     */
    @Query(
        value = """
            DELETE FROM address
            WHERE addressId in (:addressIds)
        """,
    )
    suspend fun deleteAddresses(addressIds: List<Int>): Int

    @Query(value = """
        SELECT * FROM address WHERE
            CASE WHEN :addressId IS NULL OR :addressId = 0
            THEN addressName = :addressName
            ELSE addressId != :addressId AND addressName = :addressName
            END LIMIT 1
    """)
    fun findAddressByName(addressName: String, addressId: Int?): AddressEntity?


    @Query(
        """
            SELECT co.orderId, cu.customerPhone,
            COALESCE(cu.customerName, null) as customerName,
            COALESCE(co.updatedAt, co.createdAt) as updatedAt,
            cp.totalPrice
            FROM cartorder co
            JOIN cart_price cp ON cp.orderId = co.orderId
            INNER JOIN customer cu ON cu.customerId = co.customerId
            WHERE co.addressId = :addressId AND co.orderStatus = :orderStatus
            ORDER BY co.updatedAt DESC
        """
    )
    fun getAddressWiseOrder(
        addressId: Int,
        orderStatus: OrderStatus = OrderStatus.PLACED
    ): Flow<List<AddressWiseOrder>>
}