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
import com.niyaj.model.MarketItem
import com.niyaj.model.MarketTypeIdAndName
import com.niyaj.model.MeasureUnit
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.MarketItemPreviewData.marketItems

class MarketItemPreviewParameter: PreviewParameterProvider<UiState<List<MarketItem>>> {
    override val values: Sequence<UiState<List<MarketItem>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(marketItems)
        )
}

object MarketItemPreviewData {

    val marketItems = listOf(
        MarketItem(
            itemId = 1,
            itemName = "Tomatoes",
            itemType = MarketTypeIdAndName(typeId = 1, typeName = "Vegetables"),
            itemMeasureUnit = MeasureUnit(unitId = 5, unitName = "Liter", unitValue = 1.0),
            itemPrice = "50",
            itemDescription = "Fresh, ripe tomatoes"
        ),
        MarketItem(
            itemId = 2,
            itemName = "Carrots",
            itemType = MarketTypeIdAndName(typeId = 1, typeName = "Vegetables"),
            itemMeasureUnit = MeasureUnit(unitId = 4, unitName = "Pound", unitValue = 0.453592),
            itemPrice = "30"
        ),
        MarketItem(
            itemId = 3,
            itemName = "Apples",
            itemType = MarketTypeIdAndName(typeId = 2, typeName = "Fruits"),
            itemMeasureUnit = MeasureUnit(unitId = 4, unitName = "Pound", unitValue = 0.453592),
            itemPrice = "80"
        ),
        MarketItem(
            itemId = 4,
            itemName = "Oranges",
            itemType = MarketTypeIdAndName(typeId = 2, typeName = "Fruits"),
            itemMeasureUnit = MeasureUnit(unitId = 1, unitName = "Meter", unitValue = 1.0),
            itemPrice = "60"
        ),
        MarketItem(
            itemId = 5,
            itemName = "Milk",
            itemType = MarketTypeIdAndName(typeId = 3, typeName = "Dairy"),
            itemMeasureUnit = MeasureUnit(unitId = 6, unitName = "Gallon", unitValue = 3.78541),
            itemPrice = "120",
            itemDescription = "Fresh, whole milk"
        ),
        MarketItem(
            itemId = 6,
            itemName = "Cheese",
            itemType = MarketTypeIdAndName(typeId = 3, typeName = "Dairy"),
            itemMeasureUnit = MeasureUnit(unitId = 4, unitName = "Pound", unitValue = 0.453592),
            itemPrice = "80"
        ),
        MarketItem(
            itemId = 7,
            itemName = "Bread",
            itemType = MarketTypeIdAndName(typeId = 4, typeName = "Bakery"),
            itemMeasureUnit = MeasureUnit(unitId = 1, unitName = "Meter", unitValue = 1.0),
            itemPrice = "30"
        ),
        MarketItem(
            itemId = 8,
            itemName = "Muffins",
            itemType = MarketTypeIdAndName(typeId = 4, typeName = "Bakery"),
            itemMeasureUnit = MeasureUnit(unitId = 4, unitName = "Pound", unitValue = 0.453592),
            itemPrice = "40"
        ),
        MarketItem(
            itemId = 9,
            itemName = "Chicken",
            itemType = MarketTypeIdAndName(typeId = 5, typeName = "Meat"),
            itemMeasureUnit = MeasureUnit(unitId = 3, unitName = "Kilogram", unitValue = 1.0),
            itemPrice = "200",
            itemDescription = "Fresh, boneless chicken breasts"
        ),
        MarketItem(
            itemId = 10,
            itemName = "Beef",
            itemType = MarketTypeIdAndName(typeId = 5, typeName = "Meat"),
            itemMeasureUnit = MeasureUnit(unitId = 4, unitName = "Pound", unitValue = 0.453592),
            itemPrice = "150"
        )
    )
}