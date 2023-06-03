package com.niyaj.poposroom.features.employee.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.poposroom.features.employee.domain.model.Employee
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {
    @Query(value = """
        SELECT * FROM employee ORDER BY createdAt DESC
    """)
    fun getAllEmployee(): Flow<List<Employee>>

    @Query(value = """
        SELECT * FROM employee WHERE employeeId = :employeeId
    """)
    fun getEmployeeById(employeeId: Int): Employee?

    /**
     * Inserts [Employee] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreEmployee(employee: Employee): Long

    /**
     * Updates [Employee] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateEmployee(employee: Employee): Int

    /**
     * Inserts or updates [Employee] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertEmployee(employee: Employee): Long

    @Query(value = """
        DELETE FROM employee WHERE employeeId = :employeeId
    """)
    suspend fun deleteEmployee(employeeId: Int): Int

    /**
     * Deletes rows in the db matching the specified [employeeIds]
     */
    @Query(
        value = """
            DELETE FROM employee
            WHERE employeeId in (:employeeIds)
        """,
    )
    suspend fun deleteEmployee(employeeIds: List<Int>): Int

    @Query(value = """
        SELECT * FROM employee WHERE
            CASE WHEN :employeeId IS NULL OR :employeeId = 0
            THEN employeePhone = :employeePhone
            ELSE employeeId != :employeeId AND employeePhone = :employeePhone
            END LIMIT 1
    """
    )
    fun findEmployeeByPhone(employeePhone: String, employeeId: Int?): Employee?

    @Query(value = """
        SELECT * FROM employee WHERE
            CASE WHEN :employeeId IS NULL OR :employeeId = 0
            THEN employeeName = :employeeName
            ELSE employeeId != :employeeId AND employeeName = :employeeName
            END LIMIT 1
    """
    )
    fun findEmployeeByName(employeeName: String, employeeId: Int?): Employee?
}