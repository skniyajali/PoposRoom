package com.niyaj.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niyaj.model.MeasureUnit

@Entity(
    tableName = "measure_unit"
)
data class MeasureUnitEntity(
    @PrimaryKey(autoGenerate = true)
    val unitId: Int = 0,

    val unitName: String,

    val unitValue: Double
)


fun MeasureUnitEntity.asExternalModel() = MeasureUnit(
    unitId = unitId,
    unitName = unitName,
    unitValue = unitValue
)