package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.database.model.MeasureUnitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasureUnitDao {

    @Query(value = """
        SELECT * FROM measure_unit
    """)
    fun getAllMeasureUnits(): Flow<List<MeasureUnitEntity>>

    @Query(value = """
        SELECT * FROM measure_unit WHERE unitId = :unitId
    """)
    fun getMeasureUnitById(unitId: Int): MeasureUnitEntity?

    /**
     * Inserts [MeasureUnitEntity] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreMeasureUnit(measureUnit: MeasureUnitEntity): Long

    /**
     * Updates [MeasureUnitEntity] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateMeasureUnit(measureUnit: MeasureUnitEntity): Int

    /**
     * Inserts or updates [MeasureUnitEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertMeasureUnit(measureUnit: MeasureUnitEntity): Long

    @Query(value = """
        DELETE FROM measure_unit WHERE unitId = :unitId
    """)
    suspend fun deleteMeasureUnit(unitId: Int): Int

    /**
     * Deletes rows in the db matching the specified [unitIds]
     */
    @Query(
        value = """
            DELETE FROM measure_unit
            WHERE unitId in (:unitIds)
        """,
    )
    suspend fun deleteMeasureUnits(unitIds: List<Int>): Int

    @Query(value = """
        SELECT unitId FROM measure_unit WHERE
            CASE WHEN :unitId IS NULL OR :unitId = 0
            THEN unitName = :unitName
            ELSE unitId != :unitId AND unitName = :unitName
            END LIMIT 1
    """)
    fun findMeasureUnitByName(unitName: String, unitId: Int?): Int?
}