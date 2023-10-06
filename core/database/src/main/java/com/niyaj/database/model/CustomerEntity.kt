package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niyaj.model.Customer
import java.util.Date

@Entity(tableName = "customer")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    val customerId: Int = 0,

    val customerPhone: String,

    val customerName: String? = null,

    val customerEmail: String? = null,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date,

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,
)

fun CustomerEntity.asExternalModel(): Customer {
    return Customer(
        customerId = this.customerId,
        customerName = this.customerName,
        customerPhone = this.customerPhone,
        customerEmail = this.customerEmail,
        createdAt = this.createdAt.time,
        updatedAt = this.updatedAt?.time
    )
}