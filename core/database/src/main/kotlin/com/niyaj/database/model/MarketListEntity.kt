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

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niyaj.model.MarketList

@Entity(tableName = "market_list")
data class MarketListEntity(

    @PrimaryKey(autoGenerate = true)
    val marketId: Int = 0,

    val marketDate: Long,

    val createdAt: Long,

    val updatedAt: Long? = null,
)

fun MarketListEntity.asExternalModel(): MarketList {
    return MarketList(
        marketId = marketId,
        marketDate = marketDate,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
