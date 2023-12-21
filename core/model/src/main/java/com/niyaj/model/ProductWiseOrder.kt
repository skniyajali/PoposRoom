package com.niyaj.model

import androidx.compose.runtime.Stable
import java.util.Date

@Stable
data class ProductWiseOrder(
    val orderId: Int,
    val orderedDate: Date,
    val orderType: OrderType,
    val quantity: Int,
    val customerPhone: String? = null,
    val customerAddress: String? = null,
)
