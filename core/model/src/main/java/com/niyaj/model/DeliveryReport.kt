package com.niyaj.model

import java.util.Date

data class DeliveryReport(
    val orderId: Int,

    val createdAt: Date,

    val updatedAt: Date? = null,

    val addressName: String,

    val orderPrice: Long,
)


