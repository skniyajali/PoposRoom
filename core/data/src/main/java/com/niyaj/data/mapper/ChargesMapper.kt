package com.niyaj.data.mapper

import com.niyaj.common.utils.toDate
import com.niyaj.database.model.ChargesEntity
import com.niyaj.model.Charges

fun Charges.toEntity(): ChargesEntity {
    return ChargesEntity(
        chargesId = this.chargesId,
        chargesName = this.chargesName,
        chargesPrice = this.chargesPrice,
        isApplicable = this.isApplicable,
        createdAt = this.createdAt.toDate,
        updatedAt = this.updatedAt?.toDate
    )
}