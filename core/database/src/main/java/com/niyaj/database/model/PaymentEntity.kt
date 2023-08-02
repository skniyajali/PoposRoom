package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.niyaj.model.Payment
import com.niyaj.model.PaymentMode
import com.niyaj.model.PaymentType
import java.util.Date

@Entity(
    tableName = "payment",
    foreignKeys = [ForeignKey(
        entity = EmployeeEntity::class,
        parentColumns = arrayOf("employeeId"),
        childColumns = arrayOf("employeeId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    val paymentId: Int = 0,

    @ColumnInfo(index = true)
    val employeeId: Int,

    val paymentAmount: String = "",

    val paymentDate: String = "",

    val paymentType: PaymentType = PaymentType.Advanced,

    val paymentMode: PaymentMode = PaymentMode.Cash,

    val paymentNote: String = "",

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date,

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,
)

fun PaymentEntity.asExternalModel(): Payment {
    return Payment(
        paymentId = this.paymentId,
        employeeId = this.employeeId,
        paymentAmount = this.paymentAmount,
        paymentDate = this.paymentDate,
        paymentType = this.paymentType,
        paymentMode = this.paymentMode,
        paymentNote = this.paymentNote,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}