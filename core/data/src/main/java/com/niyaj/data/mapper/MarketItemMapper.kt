package com.niyaj.data.mapper

import com.niyaj.database.model.MarketItemEntity
import com.niyaj.model.MarketItem

fun MarketItem.toEntity(): MarketItemEntity {
    return MarketItemEntity(
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