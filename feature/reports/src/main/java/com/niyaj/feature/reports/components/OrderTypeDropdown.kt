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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.model.OrderType
import com.niyaj.ui.components.StandardOutlinedAssistChip

@Composable
fun OrderTypeDropdown(
    text: String,
    onItemClick: (String) -> Unit = {},
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column {
        StandardOutlinedAssistChip(
            text = text,
            icon = PoposIcons.CalenderMonth,
            onClick = {
                menuExpanded = !menuExpanded
            },
            trailingIcon = PoposIcons.ArrowDropDown,
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
