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

package com.niyaj.ui.parameterProvider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.niyaj.model.MarketList
import com.niyaj.model.MarketListWithType
import com.niyaj.model.MarketListWithTypes
import com.niyaj.model.MarketTypeIdAndListTypes
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.MarketListPreviewData.marketListWithTypes

class MarketListPreviewParameter : PreviewParameterProvider<UiState<List<MarketListWithTypes>>> {
    override val values: Sequence<UiState<List<MarketListWithTypes>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(marketListWithTypes),
        )
}

object MarketListPreviewData {

    val marketListWithTypes = listOf(
        MarketListWithTypes(
            marketList = MarketList(
                marketId = 1,
                marketDate = 1623456789000,
                createdAt = 1623456790000,
                updatedAt = 1623456791000,
            ),
            marketTypes = listOf(
                MarketListWithType(
                    listWithTypeId = 1,
                    typeId = 1,
                    listType = "NEEDED",
                    typeName = "Vegetables",
                ),
                MarketListWithType(
                    listWithTypeId = 2,
                    typeId = 2,
                    listType = "IN_STOCK",
                    typeName = "Fruits",
                ),
                MarketListWithType(
                    listWithTypeId = 3,
                    typeId = 3,
                    listType = "OUT_OF_STOCK",
                    typeName = "Dairy",
                ),
            ),
        ),
        MarketListWithTypes(
            marketList = MarketList(
                marketId = 2,
                marketDate = 1623456792000,
                createdAt = 1623456793000,
                updatedAt = 1623456794000,
            ),
            marketTypes = listOf(
                MarketListWithType(
                    listWithTypeId = 6,
                    typeId = 6,
                    listType = "NEEDED",
                    typeName = "Seafood",
                ),
                MarketListWithType(
                    listWithTypeId = 7,
                    typeId = 7,
                    listType = "OUT_OF_STOCK",
                    typeName = "Beverages",
                ),
            ),
        ),
        MarketListWithTypes(
            marketList = MarketList(
                marketId = 3,
                marketDate = 1623456795000,
                createdAt = 1623456796000,
            ),
            marketTypes = listOf(
                MarketListWithType(
                    listWithTypeId = 11,
                    typeId = 1,
                    listType = "OUT_OF_STOCK",
                    typeName = "Vegetables",
                ),
                MarketListWithType(
                    listWithTypeId = 12,
                    typeId = 2,
                    listType = "NEEDED",
                    typeName = "Fruits",
                ),
            ),
        ),
    )

    val marketTypeIdAndListTypes = listOf(
        MarketTypeIdAndListTypes(
            typeId = 1,
            typeName = "Vegetables",
            listTypes = listOf("IN_STOCK", "NEEDED", "OUT_OF_STOCK"),
        ),
        MarketTypeIdAndListTypes(
            typeId = 2,
            typeName = "Fruits",
            listTypes = listOf("IN_STOCK", "NEEDED"),
        ),
        MarketTypeIdAndListTypes(
            typeId = 3,
            typeName = "Dairy",
            listTypes = listOf("OUT_OF_STOCK", "NEEDED"),
        ),
        MarketTypeIdAndListTypes(
            typeId = 4,
            typeName = "Bakery",
            listTypes = listOf("IN_STOCK"),
        ),
        MarketTypeIdAndListTypes(
            typeId = 5,
            typeName = "Meat",
            listTypes = listOf("NEEDED", "OUT_OF_STOCK"),
        ),
    )
}
