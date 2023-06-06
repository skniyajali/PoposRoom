package com.niyaj.poposroom.features.employee_payment.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.niyaj.poposroom.features.employee_payment.domain.model.EmployeeWithPayment
import com.niyaj.poposroom.features.employee_payment.domain.model.EmployeeWithPaymentCrossRef
import com.niyaj.poposroom.features.employee_payment.domain.model.Payment
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {

    @Transaction
    @Query(value = """
        SELECT * FROM employee
    """)
    fun getAllEmployeePayment(): Flow<List<EmployeeWithPayment>>

    @Query(value = """
        SELECT * FROM payment ORDER BY createdAt DESC
    """)
    fun getAllPayment(): Flow<List<Payment>>

    @Query(value = """
        SELECT * FROM payment WHERE paymentId = :paymentId
    """)
    suspend fun getPaymentById(paymentId: Int): Payment?

    /**
     * Inserts [Payment] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnorePayment(payment: Payment): Long

    /**
     * Updates [Payment] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updatePayment(payment: Payment): Int

    /**
     * Inserts or updates [Payment] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertPayment(payment: Payment): Long

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
}