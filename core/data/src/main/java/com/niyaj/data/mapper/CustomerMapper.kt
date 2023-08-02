package com.niyaj.data.mapper

import com.niyaj.database.model.CustomerEntity
import com.niyaj.model.Customer

fun Customer.toEntity(): CustomerEntity {
    return CustomerEntity(
        customerId = this.customerId,
        customerName = this.customerName,
        customerPhone = this.customerPhone,
        customerEmail = this.customerEmail,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}