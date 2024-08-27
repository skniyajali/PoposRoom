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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.niyaj.designsystem.components.StandardRoundedFilterChip
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.model.Charges
import com.niyaj.ui.parameterProvider.ChargesPreviewData
import com.niyaj.ui.utils.DevicePreviews

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CartChargesItem(
    modifier: Modifier = Modifier,
    chargesList: List<Charges> = emptyList(),
    selectedItems: List<Int> = emptyList(),
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    onClick: (Int) -> Unit = {},
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor),
        horizontalArrangement = Arrangement.spacedBy(SpaceSmallMax, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.Center,
    ) {
        for (charges in chargesList) {
            StandardRoundedFilterChip(
                text = charges.chargesName,
                modifier = Modifier,
                selected = selectedItems.contains(charges.chargesId),
                onClick = {
                    onClick(charges.chargesId)
                },
            )
        }
    }
}

@DevicePreviews
@Composable
private fun CartChargesItemPreview(
    modifier: Modifier = Modifier,
    chargesList: List<Charges> = ChargesPreviewData.chargesList,
    selectedItems: List<Int> = chargesList.filter { it.chargesId % 2 == 0 }.map { it.chargesId },
) {
    PoposRoomTheme {
        CartChargesItem(
            modifier = modifier,
            chargesList = chargesList,
            selectedItems = selectedItems,
            onClick = {},
        )
    }
}
