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

package com.niyaj.ui.parameterProvider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.niyaj.model.MarketType
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.MarketTypePreviewData.marketTypes

class MarketTypePreviewParameter : PreviewParameterProvider<UiState<List<MarketType>>> {
    override val values: Sequence<UiState<List<MarketType>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(marketTypes)
        )
}

object MarketTypePreviewData {
    val marketTypes = listOf(
        MarketType(
            typeId = 1,
            typeName = "Vegetables",
            typeDesc = "Fresh vegetables",
            supplierId = 101,
            listTypes = listOf("IN_STOCK", "NEEDED", "OUT_OF_STOCK"),
            createdAt = 1623456789000
        ),
        MarketType(
            typeId = 2,
            typeName = "Fruits",
            typeDesc = "Fresh fruits",
            supplierId = 102,
            listTypes = listOf("IN_STOCK", "NEEDED"),
            createdAt = 1623456790000
        ),
        MarketType(
            typeId = 3,
            typeName = "Dairy",
            supplierId = 103,
            listTypes = listOf("OUT_OF_STOCK", "NEEDED"),
            createdAt = 1623456791000,
            updatedAt = 1623456792000
        ),
        MarketType(
            typeId = 4,
            typeName = "Bakery",
            typeDesc = "Baked goods",
            supplierId = 104,
            listTypes = listOf("IN_STOCK"),
            createdAt = 1623456793000
        ),
        MarketType(
            typeId = 5,
            typeName = "Meat",
            supplierId = 105,
            listTypes = listOf("NEEDED", "OUT_OF_STOCK"),
            createdAt = 1623456794000
        ),
        MarketType(
            typeId = 6,
            typeName = "Seafood",
            typeDesc = "Fresh seafood",
            supplierId = 106,
            listTypes = listOf("IN_STOCK", "NEEDED"),
            createdAt = 1623456795000
        ),
        MarketType(
            typeId = 7,
            typeName = "Beverages",
            supplierId = 107,
            listTypes = listOf("OUT_OF_STOCK"),
            createdAt = 1623456796000,
            updatedAt = 1623456797000
        ),
        MarketType(
            typeId = 8,
            typeName = "Snacks",
            typeDesc = "Chips, cookies, and more",
            supplierId = 108,
            listTypes = listOf("IN_STOCK", "NEEDED", "OUT_OF_STOCK"),
            createdAt = 1623456798000
        ),
        MarketType(
            typeId = 9,
            typeName = "Household",
            supplierId = 109,
            listTypes = listOf("NEEDED"),
            createdAt = 1623456799000
        ),
        MarketType(
            typeId = 10,
            typeName = "Personal Care",
            typeDesc = "Toiletries and cosmetics",
            supplierId = 110,
            listTypes = listOf("IN_STOCK", "OUT_OF_STOCK"),
            createdAt = 1623456800000
        )
    )
}