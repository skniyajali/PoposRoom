package com.niyaj.data.mapper

import com.niyaj.database.model.MarketListEntity
import com.niyaj.database.model.MarketListWithItemEntity
import com.niyaj.model.MarketList
import com.niyaj.model.MarketListItem

fun MarketList.toEntity(): MarketListEntity {
    return MarketListEntity(
        marketId = marketId,
        marketDate = marketDate,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun MarketListItem.toEntity(): MarketListWithItemEntity {
    return MarketListWithItemEntity(
        listId = listId,
        marketId = marketId,
        itemId = itemId,
        itemQuantity = itemQuantity,
        marketListType = marketListType
    )
}