/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.niyaj.model.MarketItem
import com.niyaj.model.MarketTypeIdAndName
import com.niyaj.model.MeasureUnit

@Entity(
    tableName = "market_item",
    foreignKeys = [
        ForeignKey(
            entity = MarketTypeEntity::class,
            parentColumns = ["typeId"],
            childColumns = ["typeId"],
            onDelete = CASCADE,
        ),
        ForeignKey(
            entity = MeasureUnitEntity::class,
            parentColumns = ["unitId"],
            childColumns = ["unitId"],
            onDelete = CASCADE,
        ),
    ],
)
data class MarketItemEntity(

    @PrimaryKey(autoGenerate = true)
    val itemId: Int = 0,

    @ColumnInfo(index = true)
    val typeId: Int,

    @ColumnInfo(index = true)
    val unitId: Int,

    val itemName: String,

    val itemPrice: String? = null,

    val itemDescription: String? = null,

    val createdAt: Long,

    val updatedAt: Long? = null,
)

fun MarketItemEntity.asExternalModel(): MarketItem {
    return MarketItem(
        itemId = itemId,
        itemType = MarketTypeIdAndName(typeId = typeId, typeName = "Unknown"),
        itemName = itemName,
        itemPrice = itemPrice,
        itemDescription = itemDescription,
        itemMeasureUnit = MeasureUnit(unitId = unitId, unitName = "kg", unitValue = 1.0),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
