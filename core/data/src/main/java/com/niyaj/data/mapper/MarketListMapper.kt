package com.niyaj.data.mapper

import com.niyaj.database.model.MarketListEntity
import com.niyaj.model.MarketList

fun MarketList.toEntity(): MarketListEntity {
    return MarketListEntity(
        itemId = itemId,
        itemType = itemType,
        itemName = itemName,
        itemPrice = itemPrice,
        itemDescription = itemDescription,
        itemMeasureUnit = itemMeasureUnit,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}