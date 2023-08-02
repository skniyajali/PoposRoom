package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.database.model.AddOnItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AddOnItemDao {

    @Query(value = """
        SELECT * FROM addonitem ORDER BY createdAt DESC
    """)
    fun getAllAddOnItems(): Flow<List<AddOnItemEntity>>

    @Query(value = """
        SELECT * FROM addonitem WHERE itemId = :itemId
    """)
    fun getAddOnItemById(itemId: Int): AddOnItemEntity?

    /**
     * Inserts [AddOnItemEntity] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreAddOnItem(addOnItem: AddOnItemEntity): Long

    /**
     * Updates [AddOnItemEntity] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateAddOnItem(addOnItem: AddOnItemEntity): Int

    /**
     * Inserts or updates [AddOnItemEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertAddOnItem(addOnItem: AddOnItemEntity): Long

    @Query(value = """
        DELETE FROM addonitem WHERE itemId = :itemId
    """)
    suspend fun deleteAddOnItem(itemId: Int): Int

    /**
     * Deletes rows in the db matching the specified [itemIds]
     */
    @Query(
        value = """
            DELETE FROM addonitem
            WHERE itemId in (:itemIds)
        """,
    )
    suspend fun deleteAddOnItems(itemIds: List<Int>): Int

    @Query(value = """
        SELECT * FROM addonitem WHERE
            CASE WHEN :itemId IS NULL OR :itemId = 0
            THEN itemName = :itemName
            ELSE itemId != :itemId AND itemName = :itemName
            END LIMIT 1
    """)
    fun findAddOnItemByName(itemName: String, itemId: Int?): AddOnItemEntity?
}
