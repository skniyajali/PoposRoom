package com.niyaj.model

data class MarketListWithItem(
    val listId: Int = 0,

    val marketId: Int,

    val itemId: Int,

    val itemQuantity: Double,

    val marketListType: MarketListType,
)
