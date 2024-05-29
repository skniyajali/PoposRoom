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
import androidx.room.PrimaryKey

@Entity(
    tableName = "market_list_with_type",
    foreignKeys = [
        ForeignKey(
            entity = MarketListEntity::class,
            parentColumns = arrayOf("marketId"),
            childColumns = arrayOf("marketId"),
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = MarketTypeEntity::class,
            parentColumns = arrayOf("typeId"),
            childColumns = arrayOf("typeId"),
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class MarketListWithTypeEntity(

    @PrimaryKey(autoGenerate = true)
    val listWithTypeId: Int,

    @ColumnInfo(index = true)
    val marketId: Int,

    @ColumnInfo(index = true)
    val typeId: Int,

    val listType: String,
)
