package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.database.model.CustomerEntity
import com.niyaj.database.model.CustomerWiseOrderDto
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

    @Transaction
    @Query(
        value = """
            SELECT orderId, createdAt, updatedAt, addressId FROM cartorder WHERE customerId = :customerId ORDER BY updatedAt DESC
        """
    )
    fun getCustomerWiseOrders(customerId: Int): Flow<List<CustomerWiseOrderDto>>
}