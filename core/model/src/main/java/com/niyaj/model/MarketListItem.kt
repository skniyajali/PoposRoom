package com.niyaj.model

import androidx.compose.runtime.Stable

@Stable
data class MarketListItem(
    val listId: Int = 0,

    val marketId: Int,

    val itemId: Int,

    val itemQuantity: Double,

    val marketListType: MarketListType,
)
