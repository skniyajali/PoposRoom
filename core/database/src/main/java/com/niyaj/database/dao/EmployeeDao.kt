package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.database.model.AbsentEntity
import com.niyaj.database.model.EmployeeEntity
import com.niyaj.database.model.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {
    @Query(
        value = """
        SELECT * FROM employee ORDER BY createdAt DESC
    """
    )
    fun getAllEmployee(): Flow<List<EmployeeEntity>>

    @Query(
        value = """
        SELECT * FROM employee WHERE employeeId = :employeeId
    """
    )
    fun getEmployeeById(employeeId: Int): EmployeeEntity?

    /**
     * Inserts [EmployeeEntity] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreEmployee(employee: EmployeeEntity): Long

    /**
     * Updates [EmployeeEntity] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateEmployee(employee: EmployeeEntity): Int

    /**
     * Inserts or updates [EmployeeEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertEmployee(employee: EmployeeEntity): Long

    @Query(
        value = """
        DELETE FROM employee WHERE employeeId = :employeeId
    """
    )
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

    @Query(
        value = """
        SELECT employeeId FROM employee WHERE
            CASE WHEN :employeeId IS NULL OR :employeeId = 0
            THEN employeePhone = :employeePhone
            ELSE employeeId != :employeeId AND employeePhone = :employeePhone
            END LIMIT 1
    """
    )
    fun findEmployeeByPhone(employeePhone: String, employeeId: Int?): Int?

    @Query(
        value = """
        SELECT employeeId FROM employee WHERE
            CASE WHEN :employeeId IS NULL OR :employeeId = 0
            THEN employeeName = :employeeName
            ELSE employeeId != :employeeId AND employeeName = :employeeName
            END LIMIT 1
    """
    )
    fun findEmployeeByName(employeeName: String, employeeId: Int?): Int?

    @Query(
        value = """
            SELECT * FROM payment WHERE employeeId = :employeeId
        """
    )
    suspend fun getEmployeePaymentById(employeeId: Int): PaymentEntity?

    @Query(
        value = """
            SELECT * FROM absent WHERE employeeId = :employeeId
        """
    )
    suspend fun getEmployeeAbsentById(employeeId: Int): AbsentEntity?

    @Query(
        value = """
            SELECT employeeJoinedDate FROM employee WHERE employeeId = :employeeId
        """
    )
    suspend fun getEmployeeJoinedDate(employeeId: Int): String?

    @Query(
        value = """
            SELECT employeeSalary FROM employee WHERE employeeId = :employeeId
        """
    )
    suspend fun getEmployeeSalary(employeeId: Int): String?


    @Query(
        value = """
            SELECT absentDate FROM absent WHERE employeeId = :employeeId AND absentDate >= :startDate AND absentDate <= :endDate ORDER BY absentDate DESC
        """
    )
    fun getEmployeeAbsentDatesByDate(
        employeeId: Int,
        startDate: String,
        endDate: String,
    ): Flow<List<String>>

    @Query(
        value = """
            SELECT * FROM payment WHERE employeeId = :employeeId AND paymentDate >= :startDate AND paymentDate <= :endDate ORDER BY paymentDate DESC
        """
    )
    fun getEmployeePaymentsByDate(
        employeeId: Int,
        startDate: String,
        endDate: String,
    ): Flow<List<PaymentEntity>>


    @Query(
        value = """
            SELECT paymentAmount FROM payment WHERE employeeId = :employeeId AND paymentDate >= :startDate AND paymentDate <= :endDate ORDER BY paymentDate DESC
        """
    )
    fun getEmployeePaymentAmountsByDate(
        employeeId: Int,
        startDate: String,
        endDate: String,
    ): Flow<List<String>>


    @Query(
        value = """
            SELECT absentDate FROM absent WHERE CASE WHEN :absentId IS NULL OR :absentId = 0
            THEN absentDate = :absentDate AND employeeId = :employeeId
            ELSE absentId != :absentId AND absentDate = :absentDate AND employeeId = :employeeId
            END LIMIT 1
        """
    )
    suspend fun findEmployeeAbsentDateByIdAndDate(
        absentDate: String,
        employeeId: Int,
        absentId: Int? = null,
    ): String?

}