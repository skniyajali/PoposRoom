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
import com.niyaj.database.model.CustomerEntity
import com.niyaj.model.CustomerWiseOrder
import com.niyaj.model.OrderStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {

    @Query(value = """
        SELECT * FROM customer ORDER BY createdAt DESC
    """)
    fun getAllCustomer(): Flow<List<CustomerEntity>>

    @Query(value = """
        SELECT * FROM customer WHERE customerId = :customerId
    """)
    fun getCustomerById(customerId: Int): CustomerEntity?


    /**
     * Get customerId from database if it exist by [customerPhone]
     */
    @Query(value = """
        SELECT customerId FROM customer WHERE customerPhone = :customerPhone
    """
    )
    suspend fun getCustomerByPhone(customerPhone: String): Int?

    /**
     * Inserts [CustomerEntity] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreCustomer(customer: CustomerEntity): Long

    /**
     * Updates [CustomerEntity] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateCustomer(customer: CustomerEntity): Int

    /**
     * Inserts or updates [CustomerEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertCustomer(customer: CustomerEntity): Long

    @Query(value = """
        DELETE FROM customer WHERE customerId = :customerId
    """)
    suspend fun deleteCustomer(customerId: Int): Int

    /**
     * Deletes rows in the db matching the specified [customerIds]
     */
    @Query(
        value = """
            DELETE FROM customer
            WHERE customerId in (:customerIds)
        """,
    )
    suspend fun deleteCustomer(customerIds: List<Int>): Int

    @Query(value = """
        SELECT * FROM customer WHERE
            CASE WHEN :customerId IS NULL OR :customerId = 0
            THEN customerPhone = :customerPhone
            ELSE customerId != :customerId AND customerPhone = :customerPhone
            END LIMIT 1
    """
    )
    fun findCustomerByPhone(customerPhone: String, customerId: Int?): CustomerEntity?

    @Query(
        """
            SELECT co.orderId, ad.addressName as customerAddress,
            COALESCE(co.updatedAt, co.createdAt) as updatedAt,
            cp.totalPrice
            FROM cartorder co
            JOIN cart_price cp ON cp.orderId = co.orderId
            INNER JOIN address ad ON ad.addressId = co.addressId
            WHERE co.customerId = :customerId AND co.orderStatus = :orderStatus
            ORDER BY co.updatedAt DESC
        """
    )
    fun getCustomerWiseOrder(
        customerId: Int,
        orderStatus: OrderStatus = OrderStatus.PLACED
    ): Flow<List<CustomerWiseOrder>>
}