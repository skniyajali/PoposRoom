package com.niyaj.model

import java.util.Date

data class CartCharges(
    val orderId: Int,

    val chargesId: Int,

    val createdAt: Date = Date(),
)