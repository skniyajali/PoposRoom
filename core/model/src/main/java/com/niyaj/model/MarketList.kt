package com.niyaj.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class MarketList(
    val marketId: Int = 0,
    val marketDate: Long,
    val createdAt: Long,
    val updatedAt: Long? = null,
    val whitelistItems: ImmutableList<Int> = persistentListOf(),
)

enum class MarketListType {
    Needed,
    Stock
}