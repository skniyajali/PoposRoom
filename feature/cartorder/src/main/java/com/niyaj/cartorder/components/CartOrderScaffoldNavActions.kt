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

package com.niyaj.cartorder.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.trace
import com.niyaj.cartorder.CartOrderScreen
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.ui.components.ScaffoldNavActions

/**
 * [ScaffoldNavActions] for [CartOrderScreen]
 */
@Composable
internal fun CartOrderScaffoldNavActions(
    selectionCount: Int,
    showSearchIcon: Boolean,
    showSearchBar: Boolean,
    searchText: String,
    showMenu: Boolean,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onToggleMenu: () -> Unit,
    onDismissDropdown: () -> Unit,
    onClickViewAll: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSelectOrderClick: () -> Unit,
    onClickViewDetails: () -> Unit,
    onSelectAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) = trace("CartOrderScaffoldNavActions") {
    ScaffoldNavActions(
        selectionCount = selectionCount,
        showSearchIcon = showSearchIcon,
        searchText = searchText,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        onSelectAllClick = onSelectAllClick,
        onClearClick = onClearClick,
        onSearchClick = onSearchClick,
        onSearchTextChanged = onSearchTextChanged,
        modifier = modifier,
        showBottomBarActions = false,
        showSearchBar = showSearchBar,
        showSettings = true,
        onSettingsClick = onSettingsClick,
        content = {
            Box {
                IconButton(
                    onClick = onToggleMenu,
                ) {
                    Icon(
                        imageVector = PoposIcons.MoreVert,
                        contentDescription = "View More Settings",
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = onDismissDropdown,
                ) {
                    DropdownMenuItem(
                        onClick = onClickViewAll,
                        text = {
                            Text(
                                text = "View All",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = PoposIcons.Visibility,
                                contentDescription = "View All",
                            )
                        },
                    )
                }
            }
        },
        preActionContent = {
            AnimatedVisibility(
                visible = selectionCount == 1,
            ) {
                IconButton(
                    onClick = onSelectOrderClick,
                ) {
                    Icon(
                        imageVector = PoposIcons.TaskAlt,
                        contentDescription = "Select Order",
                    )
                }
            }
        },
        postActionContent = {
            AnimatedVisibility(
                visible = selectionCount == 1,
            ) {
                IconButton(
                    onClick = onClickViewDetails,
                ) {
                    Icon(
                        imageVector = PoposIcons.OpenInNew,
                        contentDescription = "View Details",
                    )
                }
            }
        },
    )
}
