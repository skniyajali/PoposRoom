package com.niyaj.data.mapper

import com.niyaj.common.utils.toDate
import com.niyaj.database.model.AddOnItemEntity
import com.niyaj.model.AddOnItem

fun AddOnItem.toEntity(): AddOnItemEntity {
    return AddOnItemEntity(
        itemId = this.itemId,
        itemName = this.itemName,
        itemPrice = this.itemPrice,
        isApplicable = this.isApplicable,
        createdAt = this.createdAt.toDate,
        updatedAt = this.updatedAt?.toDate
    )
}

