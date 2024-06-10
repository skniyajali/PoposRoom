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

@file:Suppress("ktlint:standard:max-line-length")

package com.niyaj.ui.parameterProvider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.niyaj.model.AddOnItem
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.AddOnPreviewData.addOnItemList

/**
 * This [PreviewParameterProvider](https://developer.android.com/reference/kotlin/androidx/compose/ui/tooling/preview/PreviewParameterProvider)
 * provides list of [AddOnItem] for Composable previews.
 */
class AddOnItemPreviewParameterProvider : PreviewParameterProvider<UiState<List<AddOnItem>>> {
    override val values: Sequence<UiState<List<AddOnItem>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(addOnItemList),
        )
}

object AddOnPreviewData {
    val addOnItemList: List<AddOnItem> = listOf(
        AddOnItem(
            itemId = 1,
            itemName = "Extra Cheese",
            itemPrice = 100,
            isApplicable = true,
            createdAt = 1621537200000,
            updatedAt = null,
        ),
        AddOnItem(
            itemId = 2,
            itemName = "Avocado",
            itemPrice = 150,
            isApplicable = false,
            createdAt = 1623955200000,
            updatedAt = 1624041600000,
        ),
        AddOnItem(
            itemId = 3,
            itemName = "Extra Sauce",
            itemPrice = 50,
            isApplicable = true,
            createdAt = 1625510400000,
            updatedAt = null,
        ),
        AddOnItem(
            itemId = 4,
            itemName = "Side Salad",
            itemPrice = 300,
            isApplicable = true,
            createdAt = 1627924800000,
            updatedAt = 1628011200000,
        ),
        AddOnItem(
            itemId = 5,
            itemName = "Drink Upgrade",
            itemPrice = 200,
            isApplicable = false,
            createdAt = 1630339200000,
            updatedAt = null,
        ),
        AddOnItem(
            itemId = 6,
            itemName = "Extra Bacon",
            itemPrice = 250,
            isApplicable = true,
            createdAt = 1632753600000,
            updatedAt = 1632840000000,
        ),
        AddOnItem(
            itemId = 7,
            itemName = "Gluten-free Option",
            itemPrice = 150,
            isApplicable = true,
            createdAt = 1635168000000,
            updatedAt = null,
        ),
        AddOnItem(
            itemId = 8,
            itemName = "Vegan Substitute",
            itemPrice = 100,
            isApplicable = false,
            createdAt = 1637582400000,
            updatedAt = 1637668800000,
        ),
        AddOnItem(
            itemId = 9,
            itemName = "Extra Fries",
            itemPrice = 150,
            isApplicable = true,
            createdAt = 1639996800000,
            updatedAt = null,
        ),
        AddOnItem(
            itemId = 10,
            itemName = "Dessert",
            itemPrice = 300,
            isApplicable = true,
            createdAt = 1642411200000,
            updatedAt = 1642497600000,
        ),
    )
}
