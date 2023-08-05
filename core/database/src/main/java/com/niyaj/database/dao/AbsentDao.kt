package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.database.model.AbsentEntity
import com.niyaj.database.model.EmployeeEntity
import com.niyaj.database.model.EmployeeWithAbsentCrossRef
import com.niyaj.database.model.EmployeeWithAbsentsDto
import kotlinx.coroutines.flow.Flow

@Dao
interface AbsentDao {

    @Transaction
    @Query(value = """
        SELECT * FROM employee
    """)
    fun getAllAbsentEmployee(): Flow<List<EmployeeWithAbsentsDto>>

    @Query(value = """
        SELECT * FROM employee
    """)
    fun getAllEmployee(): Flow<List<EmployeeEntity>>

    @Query(value = """
        SELECT * FROM employee WHERE employeeId = :employeeId
    """
    )
    suspend fun getEmployeeById(employeeId: Int): EmployeeEntity?

    @Query(value = """
        SELECT * FROM absent ORDER BY createdAt DESC
    """)
    fun getAllAbsent(): Flow<List<AbsentEntity>>

    @Query(value = """
        SELECT * FROM absent WHERE absentId = :absentId
    """)
    suspend fun getAbsentById(absentId: Int): AbsentEntity?

    /**
     * Inserts [AbsentEntity] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreAbsent(absent: AbsentEntity): Long

    /**
     * Updates [AbsentEntity] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateAbsent(absent: AbsentEntity): Int

    /**
     * Inserts or updates [AbsentEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertAbsent(absent: AbsentEntity): Long

    @Insert(entity = EmployeeWithAbsentCrossRef::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEmployeeWithAbsentCrossReference(employeeWithAbsent: EmployeeWithAbsentCrossRef)

    @Query(value = """
        DELETE FROM absent WHERE absentId = :absentId
    """)
    suspend fun deleteAbsent(absentId: Int): Int

    /**
     * Deletes rows in the db matching the specified [absentIds]
     */
    @Query(
        value = """
            DELETE FROM absent
            WHERE absentId in (:absentIds)
        """,
    )
    suspend fun deleteAbsents(absentIds: List<Int>): Int

    @Query(value = """
        SELECT * FROM absent WHERE
            CASE WHEN :absentId IS NULL OR :absentId = 0
            THEN absentDate = :absentDate AND employeeId = :employeeId
            ELSE absentId != :absentId AND absentDate = :absentDate AND employeeId = :employeeId
            END LIMIT 1
    """)
    fun findEmployeeByDate(absentDate: String, employeeId: Int, absentId: Int?): AbsentEntity?
}