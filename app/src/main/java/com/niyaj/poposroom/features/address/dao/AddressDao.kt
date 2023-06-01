package com.niyaj.poposroom.features.address.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.poposroom.features.address.domain.model.Address
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {

    @Query(value = """
        SELECT * FROM address ORDER BY createdAt DESC
    """)
    fun getAllAddresses(): Flow<List<Address>>

    @Query(value = """
        SELECT * FROM address WHERE addressId = :addressId
    """)
    fun getAddressById(addressId: Int): Address?

    /**
     * Inserts [Address] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreAddress(address: Address): Long

    /**
     * Updates [Address] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateAddress(address: Address): Int

    /**
     * Inserts or updates [Address] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertAddress(address: Address): Long

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
    fun findAddressByName(addressId: Int?, addressName: String): Address?
}