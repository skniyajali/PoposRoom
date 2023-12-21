package com.niyaj.model

import androidx.compose.runtime.Stable
import java.util.Date

@Stable
data class CartCharges(
    val orderId: Int,

    val chargesId: Int,

    val createdAt: Date = Date(),
)