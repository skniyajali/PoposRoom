package com.niyaj.model

import java.util.Date

data class CartAddOnItems(
    val orderId: Int,

    val itemId: Int,

    val createdAt: Date = Date(),
)