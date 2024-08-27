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
import com.niyaj.model.MarketItemAndQuantity
import com.niyaj.model.MarketListAndType
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.MarketItemAndQuantityData.marketItemsAndQuantities
import com.niyaj.ui.parameterProvider.MarketItemAndQuantityData.marketItemsAndQuantity

class MarketItemAndQuantityWithDifferentTypePreviewParameter :
    PreviewParameterProvider<UiState<List<MarketItemAndQuantity>>> {
    override val values: Sequence<UiState<List<MarketItemAndQuantity>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(marketItemsAndQuantities),
        )
}

class MarketItemAndQuantityPreviewParameter :
    PreviewParameterProvider<UiState<List<MarketItemAndQuantity>>> {
    override val values: Sequence<UiState<List<MarketItemAndQuantity>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(marketItemsAndQuantity),
        )
}

object MarketItemAndQuantityData {
    val marketItemsAndQuantities = listOf(
        MarketItemAndQuantity(
            listWithTypeId = 1,
            itemId = 1,
            itemName = "Tomatoes",
            typeName = "Vegetables",
            unitName = "Liter",
            unitValue = 1.0,
            itemPrice = "50",
            listType = "NEEDED",
            itemQuantity = null,
        ),
        MarketItemAndQuantity(
            listWithTypeId = 1,
            itemId = 2,
            itemName = "Carrots",
            typeName = "Vegetables",
            unitName = "Pound",
            unitValue = 0.453592,
            itemPrice = "30",
            listType = "NEEDED",
            itemQuantity = 1.8,
        ),
        MarketItemAndQuantity(
            listWithTypeId = 2,
            itemId = 3,
            itemName = "Apples",
            typeName = "Fruits",
            unitName = "Pound",
            unitValue = 0.453592,
            itemPrice = "80",
            listType = "IN_STOCK",
            itemQuantity = null,
        ),
        MarketItemAndQuantity(
            listWithTypeId = 2,
            itemId = 4,
            itemName = "Oranges",
            typeName = "Fruits",
            unitName = "Meter",
            unitValue = 1.0,
            itemPrice = "60",
            listType = "IN_STOCK",
            itemQuantity = 2.2,
        ),
        MarketItemAndQuantity(
            listWithTypeId = 3,
            itemId = 5,
            itemName = "Milk",
            typeName = "Dairy",
            unitName = "Gallon",
            unitValue = 3.78541,
            itemPrice = "120",
            listType = "OUT_OF_STOCK",
            itemQuantity = 1.0,
        ),
        MarketItemAndQuantity(
            listWithTypeId = 3,
            itemId = 6,
            itemName = "Cheese",
            typeName = "Dairy",
            unitName = "Pound",
            unitValue = 0.453592,
            itemPrice = "80",
            listType = "OUT_OF_STOCK",
            itemQuantity = null,
        ),
        MarketItemAndQuantity(
            listWithTypeId = 4,
            itemId = 7,
            itemName = "Bread",
            typeName = "Bakery",
            unitName = "Meter",
            unitValue = 1.0,
            itemPrice = "30",
            listType = "NEEDED",
            itemQuantity = null,
        ),
        MarketItemAndQuantity(
            listWithTypeId = 4,
            itemId = 8,
            itemName = "Muffins",
            typeName = "Bakery",
            unitName = "Pound",
            unitValue = 0.453592,
            itemPrice = "40",
            listType = "NEEDED",
            itemQuantity = 1.5,
        ),
        MarketItemAndQuantity(
            listWithTypeId = 5,
            itemId = 9,
            itemName = "Chicken",
            typeName = "Meat",
            unitName = "Kilogram",
            unitValue = 1.0,
            itemPrice = "200",
            listType = "NEEDED",
            itemQuantity = 1.5,
        ),
        MarketItemAndQuantity(
            listWithTypeId = 5,
            itemId = 10,
            itemName = "Beef",
            typeName = "Meat",
            unitName = "Pound",
            unitValue = 0.453592,
            itemPrice = "150",
            listType = "NEEDED",
            itemQuantity = 2.0,
        ),
    )

    val maretListAndType = MarketListAndType(
        marketId = 2,
        marketDate = 1720722600000,
        createdAt = 1720722600000,
        listWithTypeId = 1,
        typeId = 1,
        typeName = "Vegetable",
        listType = "NEEDED",
        updatedAt = null,
    )

    val marketItemsAndQuantity = listOf(
        MarketItemAndQuantity(
            listWithTypeId = 1,
            itemId = 1,
            itemName = "Tomatoes",
            typeName = "Vegetables",
            unitName = "Liter",
            unitValue = 1.0,
            itemPrice = "50",
            listType = "NEEDED",
            itemQuantity = 2.5,
        ),
        MarketItemAndQuantity(
            listWithTypeId = 1,
            itemId = 2,
            itemName = "Carrots",
            typeName = "Vegetables",
            unitName = "Pound",
            unitValue = 0.453592,
            itemPrice = "30",
            listType = "NEEDED",
            itemQuantity = null,
        ),
        MarketItemAndQuantity(
            listWithTypeId = 1,
            itemId = 3,
            itemName = "Broccoli",
            typeName = "Vegetables",
            unitName = "Kilogram",
            unitValue = 1.0,
            itemPrice = "60",
            listType = "NEEDED",
            itemQuantity = null,
        ),
        MarketItemAndQuantity(
            listWithTypeId = 1,
            itemId = 4,
            itemName = "Spinach",
            typeName = "Vegetables",
            unitName = "Pound",
            unitValue = 0.453592,
            itemPrice = "40",
            listType = "NEEDED",
            itemQuantity = 0.0,
        ),
        MarketItemAndQuantity(
            listWithTypeId = 1,
            itemId = 5,
            itemName = "Potatoes",
            typeName = "Vegetables",
            unitName = "Kilogram",
            unitValue = 1.0,
            itemPrice = "35",
            listType = "NEEDED",
            itemQuantity = 0.0,
        ),
        MarketItemAndQuantity(
            listWithTypeId = 1,
            itemId = 6,
            itemName = "Bell Peppers",
            typeName = "Vegetables",
            unitName = "Liter",
            unitValue = 1.0,
            itemPrice = "45",
            listType = "NEEDED",
            itemQuantity = null,
        ),
        MarketItemAndQuantity(
            listWithTypeId = 1,
            itemId = 7,
            itemName = "Onions",
            typeName = "Vegetables",
            unitName = "Pound",
            unitValue = 0.453592,
            itemPrice = "25",
            listType = "NEEDED",
            itemQuantity = 1.0,
        ),
        MarketItemAndQuantity(
            listWithTypeId = 1,
            itemId = 8,
            itemName = "Cucumbers",
            typeName = "Vegetables",
            unitName = "Liter",
            unitValue = 1.0,
            itemPrice = "55",
            listType = "NEEDED",
            itemQuantity = 0.0,
        ),
        MarketItemAndQuantity(
            listWithTypeId = 1,
            itemId = 9,
            itemName = "Lettuce",
            typeName = "Vegetables",
            unitName = "Pound",
            unitValue = 0.453592,
            itemPrice = "35",
            listType = "NEEDED",
            itemQuantity = null,
        ),
        MarketItemAndQuantity(
            listWithTypeId = 1,
            itemId = 10,
            itemName = "Cabbage",
            typeName = "Vegetables",
            unitName = "Kilogram",
            unitValue = 1.0,
            itemPrice = "45",
            listType = "NEEDED",
            itemQuantity = 0.0,
        ),
    )

    val marketItemAndQuantity = marketItemsAndQuantity.first()
}
