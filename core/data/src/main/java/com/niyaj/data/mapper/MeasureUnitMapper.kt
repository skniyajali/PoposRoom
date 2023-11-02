package com.niyaj.data.mapper

import com.niyaj.database.model.MeasureUnitEntity
import com.niyaj.model.MeasureUnit

fun MeasureUnit.toEntity(): MeasureUnitEntity {
    return MeasureUnitEntity(
        unitId = unitId,
        unitName = unitName,
        unitValue = unitValue,
    )
}