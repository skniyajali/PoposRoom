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

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.utils.DevicePreviews

@Composable
fun IncDecBox(
    quantity: String,
    measureUnit: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    enableDecreasing: Boolean = false,
    enableIncreasing: Boolean = false,
) {
    ElevatedCard(
        onClick = {},
        modifier = modifier
            .height(40.dp),
        shape = RoundedCornerShape(SpaceMini),
        enabled = enableDecreasing && enableIncreasing,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpaceMini),
        ) {
            Card(
                modifier = Modifier
                    .fillMaxHeight(),
                onClick = onDecrease,
                enabled = enableDecreasing,
                shape = RoundedCornerShape(
                    topStart = SpaceMini,
                    topEnd = 0.dp,
                    bottomStart = SpaceMini,
                    bottomEnd = 0.dp,
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(SpaceSmall),
                ) {
                    Spacer(modifier = Modifier.width(SpaceMini))
                    Icon(imageVector = PoposIcons.Remove, contentDescription = "remove")
                    Spacer(modifier = Modifier.width(SpaceMini))
                }
            }

            Crossfade(
                targetState = quantity,
                label = "Item quantity",
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(SpaceMini),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Text(text = measureUnit, style = MaterialTheme.typography.labelSmall)
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxHeight(),
                onClick = onIncrease,
                enabled = enableIncreasing,
                shape = RoundedCornerShape(
                    topStart = SpaceMini,
                    topEnd = 0.dp,
                    bottomStart = SpaceMini,
                    bottomEnd = 0.dp,
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(SpaceSmall),
                ) {
                    Spacer(modifier = Modifier.width(SpaceMini))
                    Icon(imageVector = PoposIcons.Add, contentDescription = "add")
                    Spacer(modifier = Modifier.width(SpaceMini))
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun IncDecBoxPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        IncDecBox(
            quantity = "1",
            measureUnit = "kg",
            onDecrease = {},
            onIncrease = {},
            modifier = modifier,
            enableDecreasing = true,
            enableIncreasing = true,
        )
    }
}
