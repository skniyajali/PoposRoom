package com.niyaj.model

data class MarketList(
    val marketId: Int = 0,
    val marketDate: Long,
    val createdAt: Long,
    val updatedAt: Long? = null,
    val whitelistItems: List<Int> = emptyList(),
)


enum class MarketListType {
    Needed,
    Stock
}