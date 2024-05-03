package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.niyaj.database.model.PrinterEntity
import com.niyaj.database.model.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrinterDao {

    @Query(
        value = """
            SELECT * FROM printerInfo WHERE printerId = :printerId
        """
    )
    fun printerInfo(printerId: String): Flow<PrinterEntity?>


    /**
     * Inserts or update [PrinterEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun insertOrUpdatePrinterInfo(printerEntity: PrinterEntity): Long

    @Query(
        value = """
        SELECT * FROM profile WHERE restaurantId = :restaurantId
    """
    )
    fun getProfileInfo(restaurantId: Int): Flow<ProfileEntity?>

}