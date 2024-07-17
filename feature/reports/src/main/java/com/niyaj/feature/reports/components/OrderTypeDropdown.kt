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

package com.niyaj.feature.reports.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.model.OrderType
import com.niyaj.ui.utils.DevicePreviews

@Composable
internal fun OrderTypeDropdown(
    modifier: Modifier = Modifier,
    text: String,
    onItemClick: (String) -> Unit = {},
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
    ) {
        AssistChip(
            label = {
                Text(
                    text = text,
                )
            },
            onClick = {
                menuExpanded = !menuExpanded
            },
            trailingIcon = {
                Icon(
                    imageVector = PoposIcons.ArrowDropDown,
                    contentDescription = "Dropdown",
                )
            },
        )

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "All",
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                onClick = {
                    onItemClick("")
                    menuExpanded = false
                },
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = "DineIn",
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                onClick = {
                    onItemClick(OrderType.DineIn.name)
                    menuExpanded = false
                },
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = "DineOut",
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                onClick = {
                    onItemClick(OrderType.DineOut.name)
                    menuExpanded = false
                },
            )
        }
    }
}

@DevicePreviews
@Composable
private fun OrderTypeDropdownPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        Surface {
            OrderTypeDropdown(
                modifier = modifier,
                text = "All",
                onItemClick = {},
            )
        }
    }
}
