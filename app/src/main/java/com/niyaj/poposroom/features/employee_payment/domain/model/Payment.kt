package com.niyaj.poposroom.features.employee_payment.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.niyaj.poposroom.features.employee.domain.model.Employee
import com.niyaj.poposroom.features.employee.domain.utils.PaymentMode
import com.niyaj.poposroom.features.employee.domain.utils.PaymentType
import java.util.Date

@Entity(
    tableName = "payment",
    foreignKeys = [ForeignKey(
        entity = Employee::class,
        parentColumns = arrayOf("employeeId"),
        childColumns = arrayOf("employeeId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Payment(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    val paymentId: Int,

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


fun List<Payment>.searchPayment(searchText: String): List<Payment> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.paymentAmount.contains(searchText, true) ||
                    it.paymentType.name.contains(searchText, true) ||
                    it.paymentDate.contains(searchText, true) ||
                    it.paymentMode.name.contains(searchText, true) ||
                    it.paymentNote.contains(searchText, true)
        }
    } else this
}

fun Payment.filterPayment(searchText: String): Boolean {
    return if (searchText.isNotEmpty()) {
        this.paymentAmount.contains(searchText, true) ||
                this.paymentType.name.contains(searchText, true) ||
                this.paymentDate.contains(searchText, true) ||
                this.paymentMode.name.contains(searchText, true) ||
                this.paymentNote.contains(searchText, true)
    } else true
}