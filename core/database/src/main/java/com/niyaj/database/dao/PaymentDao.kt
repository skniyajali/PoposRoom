package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.database.model.EmployeeEntity
import com.niyaj.database.model.EmployeeWithPaymentCrossRef
import com.niyaj.database.model.EmployeeWithPaymentsDto
import com.niyaj.database.model.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {

    @Transaction
    @Query(value = """
        SELECT * FROM employee
    """)
    fun getAllEmployeePayment(): Flow<List<EmployeeWithPaymentsDto>>

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
        SELECT * FROM payment ORDER BY createdAt DESC
    """)
    fun getAllPayment(): Flow<List<PaymentEntity>>

    @Query(value = """
        SELECT * FROM payment WHERE paymentId = :paymentId
    """)
    suspend fun getPaymentById(paymentId: Int): PaymentEntity?

    /**
     * Inserts [PaymentEntity] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnorePayment(payment: PaymentEntity): Long

    /**
     * Updates [PaymentEntity] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updatePayment(payment: PaymentEntity): Int

    /**
     * Inserts or updates [PaymentEntity] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertPayment(payment: PaymentEntity): Long

    @Insert(entity = EmployeeWithPaymentCrossRef::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEmployeeWithPaymentCrossReference(employeeWithPayment: EmployeeWithPaymentCrossRef)

    @Query(value = """
        DELETE FROM payment WHERE paymentId = :paymentId
    """)
    suspend fun deletePayment(paymentId: Int): Int

    /**
     * Deletes rows in the db matching the specified [paymentIds]
     */
    @Query(
        value = """
            DELETE FROM payment
            WHERE paymentId in (:paymentIds)
        """,
    )
    suspend fun deletePayments(paymentIds: List<Int>): Int

    @Query(
        value = """
        SELECT employeeId FROM employee WHERE employeeId == :employeeId OR employeeName = :employeeName
    """
    )
    fun findEmployeeByName(employeeName: String, employeeId: Int?): Int?

    /**
     * Inserts or updates [EmployeeEntity] in the db under the specified primary keys
     */
    @Upsert(entity = EmployeeEntity::class)
    suspend fun upsertEmployee(employeeEntity: EmployeeEntity): Long
}