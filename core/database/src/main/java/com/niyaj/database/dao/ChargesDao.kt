package com.niyaj.database.dao

import androidx.room.*
import com.niyaj.database.model.ChargesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChargesDao {

    @Query(value = """
        SELECT * FROM charges ORDER BY createdAt DESC
    """)
    fun getAllCharges(): Flow<List<ChargesEntity>>

    @Query(value = """
        SELECT * FROM charges WHERE chargesId = :chargesId
    """)
    fun getChargesById(chargesId: Int): ChargesEntity?

    /**
     * Inserts [ChargesEntity] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreCharges(charges: ChargesEntity): Long

    /**
     * Updates [ChargesEntity] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateCharges(charges: ChargesEntity): Int

    /**
     * Inserts or updates [ChargesEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertCharges(charges: ChargesEntity): Long

    @Query(value = """
        DELETE FROM charges WHERE chargesId = :chargesId
    """)
    suspend fun deleteCharges(chargesId: Int): Int

    /**
     * Deletes rows in the db matching the specified [chargesIds]
     */
    @Query(
        value = """
            DELETE FROM charges
            WHERE chargesId in (:chargesIds)
        """,
    )
    suspend fun deleteCharges(chargesIds: List<Int>): Int

    @Query(value = """
        SELECT * FROM charges WHERE
            CASE WHEN :chargesId IS NULL OR :chargesId = 0
            THEN chargesName = :chargesName
            ELSE chargesId != :chargesId AND chargesName = :chargesName
            END LIMIT 1
    """)
    fun findChargesByName(chargesId: Int?, chargesName: String): ChargesEntity?
}