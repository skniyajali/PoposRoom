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

package com.niyaj.model

data class MarketListAndType(
    val marketId: Int,
    val marketDate: Long,
    val createdAt: Long,
    val listWithTypeId: Int,
    val typeId: Int,
    val typeName: String,
    val listType: String,
    val updatedAt: Long? = null,
)

fun List<MarketListAndType>.toExternalModel(): List<MarketListWithTypes> {
    return groupBy { it.marketId }.map {(marketId, group) ->
        val marketList = group.first().let { item ->
            MarketList(
                marketId = marketId,
                marketDate = item.marketDate,
                createdAt = item.createdAt,
                updatedAt = item.updatedAt
            )
        }

        val marketTypes = group.sortedBy { it.typeId }.map { item ->
            MarketListWithType(
                listWithTypeId = item.listWithTypeId,
                typeId = item.typeId,
                listType = item.listType,
                typeName = item.typeName
            )
        }

        MarketListWithTypes(
            marketList = marketList,
            marketTypes = marketTypes
        )
    }
}

fun List<MarketListAndType>.asExternalModel(): MarketListWithTypes {
    val marketList = first().let { item ->
        MarketList(
            marketId = item.marketId,
            marketDate = item.marketDate,
            createdAt = item.createdAt,
            updatedAt = item.updatedAt
        )
    }

    val marketTypes = map { item ->
        MarketListWithType(
            listWithTypeId = item.listWithTypeId,
            typeId = item.typeId,
            listType = item.listType,
            typeName = item.typeName
        )
    }

    return MarketListWithTypes(
        marketList = marketList,
        marketTypes = marketTypes
    )
}