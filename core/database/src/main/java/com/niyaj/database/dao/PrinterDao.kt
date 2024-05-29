/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
        """,
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
    """,
    )
    fun getProfileInfo(restaurantId: Int): Flow<ProfileEntity>
}
