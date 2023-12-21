package com.niyaj.model

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class MarketList(
    val marketId: Int = 0,
    val marketDate: Long,
    val createdAt: Long,
    val updatedAt: Long? = null,
    val whitelistItems: ImmutableList<Int> = persistentListOf(),
)

@Stable
enum class MarketListType {
    Needed,
    Stock
}