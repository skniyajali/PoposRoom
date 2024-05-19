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

import androidx.room.Embedded
import androidx.room.Relation
import com.niyaj.model.MarketItem
import com.niyaj.model.MarketTypeIdAndName
import com.niyaj.model.MeasureUnit

data class MarketItemDto(
    @Embedded
    val marketItem: MarketItemEntity,

    @Relation(
        parentColumn = "typeId",
        entityColumn = "typeId",
        entity = MarketTypeEntity::class,
        projection = ["typeId", "typeName"],
    )
    val itemType: MarketTypeIdAndName,

    @Relation(
        parentColumn = "unitId",
        entityColumn = "unitId",
        entity = MeasureUnitEntity::class,
    )
    val itemMeasureUnit: MeasureUnit,
)


fun MarketItemDto.asExternalModel() = MarketItem(
    itemId = marketItem.itemId,
    itemName = marketItem.itemName,
    itemType = itemType,
    itemMeasureUnit = itemMeasureUnit,
    itemPrice = marketItem.itemPrice,
    itemDescription = marketItem.itemDescription,
    createdAt = marketItem.createdAt,
    updatedAt = marketItem.updatedAt,
)

fun List<MarketItemDto>.asExternalModel() = map { it.asExternalModel() }