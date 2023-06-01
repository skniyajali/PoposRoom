package com.niyaj.poposroom.features.customer.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "customer")
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val customerId: Int,

    val customerName: String? = null,

    val customerPhone: String = "",

    val customerEmail: String? = null,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date,

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,
)


fun List<Customer>.searchCustomer(searchText: String): List<Customer> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.customerPhone.contains(searchText, true) ||
                    it.customerEmail.toString().contains(searchText, true) ||
                    it.customerName.toString().contains(searchText, true)
        }
    }else this
}