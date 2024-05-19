/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niyaj.model.MeasureUnit

@Entity(tableName = "measure_unit")
data class MeasureUnitEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    val unitId: Int = 0,

    val unitName: String,

    val unitValue: Double,
)


fun MeasureUnitEntity.asExternalModel() = MeasureUnit(
    unitId = unitId,
    unitName = unitName,
    unitValue = unitValue
)