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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.niyaj.common.utils.Constants
import com.niyaj.common.utils.Constants.SEARCH_ITEM_PLACEHOLDER

/**
 * @param placeholderText: Show placeholder text into search bar
 * @param showSearchIcon : if all items are empty, then show search icon
 * @param selectionCount : if selected items are empty, then show search icon
 * @param onEditClick : edit button click listener (only visible when one item is selected)
 * @param onDeleteClick : delete button click listener (only visible when one or more items are selected)
 * @param showSearchBar : show search bar
 * @param searchText : search text
 * @param onSearchTextChanged : search text change listener (only visible when search bar is visible)
 * @param onClearClick : clear button click listener (only visible when search bar is visible)
 * @param onSearchClick : search button click listener (only visible when search bar is not visible)
 * @param showSettingsIcon: show settings icon
 * @param onSettingsClick: on setting click listener (only visible when show setting icon is visible)
 * @param content : additional content
 * @param preActionContent : pre action content
 * @param postActionContent: post action content
 */
@Composable
fun ScaffoldNavActions(
    selectionCount: Int,
    showSearchIcon: Boolean,
    searchText: String,
    placeholderText: String = SEARCH_ITEM_PLACEHOLDER,
    showSearchBar: Boolean = false,
    showSettingsIcon: Boolean = false,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSelectAllClick: () -> Unit,
    onClearClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onSearchTextChanged: (String) -> Unit,
    content: @Composable () -> Unit = {},
    preActionContent: @Composable () -> Unit = {},
    postActionContent: @Composable () -> Unit = {},
) {
    if (selectionCount != 0) {
        preActionContent()

        if (selectionCount == 1) {
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.testTag(NAV_EDIT_BTN),
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Item",
                )
            }
        }

        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.testTag(NAV_DELETE_BTN),
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Item",
            )
        }

        IconButton(
            onClick = onSelectAllClick,
            modifier = Modifier.testTag(NAV_SELECT_ALL_BTN),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Rule,
                contentDescription = "Select All Item",
            )
        }

        postActionContent()
    } else if (showSearchBar) {
        StandardSearchBar(
            searchText = searchText,
            placeholderText = placeholderText,
            onClearClick = onClearClick,
            onSearchTextChanged = onSearchTextChanged,
        )
    } else {
        if (showSearchIcon) {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag(NAV_SEARCH_BTN),
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                )
            }
        }

        if (showSettingsIcon) {
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.testTag(NAV_SETTING_BTN),
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                )
            }
        }

        content()
    }
}

/**
 * @param placeholderText: Show placeholder text into search bar
 * @param showSearchIcon : if all items are empty, then show search icon
 * @param selectionCount : if selected items are empty, then show search icon
 * @param onEditClick : edit button click listener (only visible when one item is selected)
 * @param onDeleteClick : delete button click listener (only visible when one or more items are selected)
 * @param showSearchBar : show search bar
 * @param searchText : search text
 * @param onSearchTextChanged : search text change listener (only visible when search bar is visible)
 * @param onClearClick : clear button click listener (only visible when search bar is visible)
 * @param onSearchClick : search button click listener (only visible when search bar is not visible)
 * @param showSettings: show settings icon
 * @param onSettingsClick: on setting click listener (only visible when show setting icon is visible)
 * @param content : additional content
 * @param preActionContent : pre action content
 * @param postActionContent: post action content
 */
@Composable
fun ScaffoldNavActions(
    selectionCount: Int,
    showSearchIcon: Boolean,
    searchText: String,
    showBottomBarActions: Boolean = false,
    placeholderText: String = SEARCH_ITEM_PLACEHOLDER,
    showSearchBar: Boolean = false,
    showSettings: Boolean = false,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSelectAllClick: () -> Unit,
    onClearClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onSearchTextChanged: (String) -> Unit,
    content: @Composable () -> Unit = {},
    preActionContent: @Composable () -> Unit = {},
    postActionContent: @Composable () -> Unit = {},
) {
    val selectedState = MutableTransitionState(selectionCount)

    if (showSearchBar) {
        StandardSearchBar(
            searchText = searchText,
            placeholderText = placeholderText,
            onClearClick = onClearClick,
            onSearchTextChanged = onSearchTextChanged,
        )
    } else {
        AnimatedContent(
            targetState = selectedState,
            transitionSpec = {
                (fadeIn()).togetherWith(
                    fadeOut(animationSpec = tween(200)),
                )
            },
            label = "navActions",
        ) { state ->
            Row {
                if (state.currentState != 0) {
                    if (!showBottomBarActions) {
                        preActionContent()

                        if (state.currentState == 1) {
                            IconButton(onClick = onEditClick) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = Constants.EDIT_ICON,
                                )
                            }
                        }

                        IconButton(onClick = onDeleteClick) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = Constants.DELETE_ICON,
                            )
                        }

                        IconButton(onClick = onSelectAllClick) {
                            Icon(
                                imageVector = Icons.Default.Checklist,
                                contentDescription = Constants.SELECT_ALL_ICON,
                            )
                        }

                        postActionContent()
                    }
                } else {
                    if (showSearchIcon) {
                        IconButton(
                            onClick = onSearchClick,
                            modifier = Modifier.testTag(NAV_SEARCH_BTN),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                            )
                        }
                    }

                    if (showSettings) {
                        IconButton(
                            onClick = onSettingsClick,
                            modifier = Modifier.testTag(NAV_SETTING_BTN),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                            )
                        }
                    }

                    content()
                }
            }
        }
    }
}

const val NAV_SEARCH_BTN = "navigation_search_icon"
const val NAV_SELECT_ALL_BTN = "navigation_select_all"
const val NAV_DELETE_BTN = "navigation_delete_btn"
const val NAV_EDIT_BTN = "navigation_edit_btn"
const val NAV_SETTING_BTN = "navigation_settings_btn"
