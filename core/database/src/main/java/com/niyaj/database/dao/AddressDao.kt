package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.database.model.AddressEntity
import com.niyaj.database.model.AddressWiseOrderDto
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


    @Transaction
    @Query(
        value = """
            SELECT orderId, createdAt, updatedAt, customerId FROM cartorder WHERE addressId = :addressId ORDER BY updatedAt DESC
        """
    )
    fun getAddressOrderDetails(addressId: Int): Flow<List<AddressWiseOrderDto>>
}