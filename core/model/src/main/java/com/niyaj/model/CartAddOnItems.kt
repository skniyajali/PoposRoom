package com.niyaj.model

import androidx.compose.runtime.Stable
import java.util.Date

@Stable
data class CartAddOnItems(
    val orderId: Int,

    val itemId: Int,

    val createdAt: Date = Date(),
)