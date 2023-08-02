package com.niyaj.data.mapper

import com.niyaj.database.model.AddOnItemEntity
import com.niyaj.model.AddOnItem

fun AddOnItem.toEntity(): AddOnItemEntity {
    return AddOnItemEntity(
        itemId = this.itemId,
        itemName = this.itemName,
        itemPrice = this.itemPrice,
        isApplicable = this.isApplicable,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

