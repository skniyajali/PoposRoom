package com.niyaj.model

import java.util.Date

data class ProductWiseOrder(
    val orderId: Int,
    val orderedDate: Date,
    val orderType: OrderType,
    val quantity: Int,
    val customerPhone: String? = null,
    val customerAddress: String? = null,
)
