package com.niyaj.poposroom.features.employee_absent.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.poposroom.features.employee.domain.model.Employee
import com.niyaj.poposroom.features.employee_absent.domain.model.Absent
import com.niyaj.poposroom.features.employee_absent.domain.model.EmployeeWithAbsent
import com.niyaj.poposroom.features.employee_absent.domain.model.EmployeeWithAbsentCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface AbsentDao {

    @Transaction
    @Query(value = """
        SELECT * FROM employee
    """)
    fun getAllAbsentEmployee(): Flow<List<EmployeeWithAbsent>>

    @Query(value = """
        SELECT * FROM employee
    """)
    fun getAllEmployee(): Flow<List<Employee>>

    @Query(value = """
        SELECT * FROM employee WHERE employeeId = :employeeId
    """
    )
    suspend fun getEmployeeById(employeeId: Int): Employee?

    @Query(value = """
        SELECT * FROM absent ORDER BY createdAt DESC
    """)
    fun getAllAbsent(): Flow<List<Absent>>

    @Query(value = """
        SELECT * FROM absent WHERE absentId = :absentId
    """)
    suspend fun getAbsentById(absentId: Int): Absent?

    /**
     * Inserts [Absent] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreAbsent(absent: Absent): Long

    /**
     * Updates [Absent] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateAbsent(absent: Absent): Int

    /**
     * Inserts or updates [Absent] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertAbsent(absent: Absent): Long

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
    fun findEmployeeByDate(absentDate: String, employeeId: Int, absentId: Int?,): Absent?
}