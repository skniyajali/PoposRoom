package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.Relation
import com.niyaj.model.EmployeeWithPayments

@Entity(
    primaryKeys = ["employeeId", "paymentId"],
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = arrayOf("employeeId"),
            childColumns = arrayOf("employeeId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = PaymentEntity::class,
            parentColumns = arrayOf("paymentId"),
            childColumns = arrayOf("paymentId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class EmployeeWithPaymentCrossRef(
    @ColumnInfo(index = true)
    val employeeId: Int,

    @ColumnInfo(index = true)
    val paymentId: Int
)

data class EmployeeWithPaymentsDto(
    @Embedded
    val employee: EmployeeEntity,

    @Relation(
        parentColumn = "employeeId",
        entity = PaymentEntity::class,
        entityColumn = "paymentId",
        associateBy = Junction(EmployeeWithPaymentCrossRef::class)
    )
    val payments: List<PaymentEntity> = emptyList()
)

fun EmployeeWithPaymentsDto.asExternalModel(): EmployeeWithPayments {
    return EmployeeWithPayments(
        employee = this.employee.asExternalModel(),
        payments = this.payments.map { it.asExternalModel() }
    )
}