package com.niyaj.poposroom.features.employee_payment.domain.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.Relation
import com.niyaj.poposroom.features.employee.domain.model.Employee
import com.niyaj.poposroom.features.employee.domain.model.filterEmployee

@Entity(
    primaryKeys = ["employeeId", "paymentId"],
    foreignKeys = [
        ForeignKey(
            entity = Employee::class,
            parentColumns = arrayOf("employeeId"),
            childColumns = arrayOf("employeeId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = Payment::class,
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

data class EmployeeWithPayment(
    @Embedded
    val employee: Employee,

    @Relation(
        parentColumn = "employeeId",
        entity = Payment::class,
        entityColumn = "paymentId",
        associateBy = Junction(EmployeeWithPaymentCrossRef::class)
    )
    val payments: List<Payment> = emptyList()
)


fun List<EmployeeWithPayment>.searchEmployeeWithPayments(searchText: String): List<EmployeeWithPayment> {
    return if (searchText.isNotEmpty()) {
        this.filter { withSalary ->
            withSalary.employee.filterEmployee(searchText) ||
                    withSalary.payments.any { it.filterPayment(searchText) }
        }
    } else this
}