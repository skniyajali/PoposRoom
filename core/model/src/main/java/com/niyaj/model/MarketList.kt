package com.niyaj.model

data class MarketList(
    val marketId: Int = 0,
    val marketDate: Long,
    val createdAt: Long,
    val updatedAt: Long? = null,
)


enum class MarketListType {
    Needed,
    Stock
}