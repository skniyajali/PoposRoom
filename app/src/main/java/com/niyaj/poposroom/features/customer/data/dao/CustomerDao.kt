package com.niyaj.poposroom.features.customer.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.poposroom.features.customer.domain.model.Customer
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {

    @Query(value = """
        SELECT * FROM customer ORDER BY createdAt DESC
    """)
    fun getAllCustomer(): Flow<List<Customer>>

    @Query(value = """
        SELECT * FROM customer WHERE customerId = :customerId
    """)
    fun getCustomerById(customerId: Int): Customer?


    /**
     * Get customerId from database if it exist by [customerPhone]
     */
    @Query(value = """
        SELECT customerId FROM customer WHERE customerPhone = :customerPhone
    """
    )
    suspend fun getCustomerByPhone(customerPhone: String): Int?

    /**
     * Inserts [Customer] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreCustomer(customer: Customer): Long

    /**
     * Updates [Customer] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateCustomer(customer: Customer): Int

    /**
     * Inserts or updates [Customer] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertCustomer(customer: Customer): Long

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
    fun findCustomerByPhone(customerPhone: String, customerId: Int?): Customer?
}