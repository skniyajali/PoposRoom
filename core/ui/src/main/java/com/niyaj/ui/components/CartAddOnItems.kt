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

package com.niyaj.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.components.StandardRoundedFilterChip
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.model.AddOnItem
import com.niyaj.ui.parameterProvider.AddOnPreviewData
import com.niyaj.ui.utils.DevicePreviews

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CartAddOnItems(
    modifier: Modifier = Modifier,
    addOnItems: List<AddOnItem> = emptyList(),
    selectedAddOnItem: List<Int> = emptyList(),
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    selectedColor: Color = MaterialTheme.colorScheme.tertiary,
    onClick: (Int) -> Unit = {},
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor),
        horizontalArrangement = Arrangement.spacedBy(SpaceSmallMax, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.Center,
    ) {
        addOnItems.forEach { addOnItem ->
            key(addOnItem.itemId) {
                val text = if (addOnItem.itemName.startsWith("Cold")) {
                    "${addOnItem.itemName}-${addOnItem.itemPrice.toRupee}"
                } else {
                    addOnItem.itemName
                }
                StandardRoundedFilterChip(
                    text = text,
                    selected = selectedAddOnItem.contains(addOnItem.itemId),
                    selectedColor = selectedColor,
                    onClick = {
                        onClick(addOnItem.itemId)
                    },
                )
            }
        }
    }
}


@DevicePreviews
@Composable
private fun CartAddOnItemsPreview(
    modifier: Modifier = Modifier,
    addOnItems: List<AddOnItem> = AddOnPreviewData.addOnItemList,
    selectedAddOnItem: List<Int> = addOnItems.filter { it.itemId % 2 == 0 }.map { it.itemId },
) {
    PoposRoomTheme {
        CartAddOnItems(
            modifier = modifier,
            addOnItems = addOnItems,
            selectedAddOnItem = selectedAddOnItem,
            onClick = {}
        )
    }
}