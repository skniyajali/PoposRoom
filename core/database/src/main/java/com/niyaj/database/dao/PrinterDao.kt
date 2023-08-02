package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.niyaj.database.model.PrinterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrinterDao {

    @Query(
        value = """
            SELECT * FROM printerInfo WHERE printerId = :printerId
        """
    )
    suspend fun printerInfo(printerId: String): PrinterEntity?


    /**
     * Inserts or update [PrinterEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun insertOrUpdatePrinterInfo(printerEntity: PrinterEntity): Long

}