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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.Constants
import com.niyaj.common.utils.Constants.CLEAR_ICON
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.components.PoposIconButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceLarge
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.ui.utils.DevicePreviews

const val IMPORT_EXPORT_BTN = "import_export_btn"
const val IMPORT_OPN_FILE = "Open File"
const val IMPORT_LOADING = "ImportLoading"

@Composable
@Suppress("LongParameterList")
fun ExportScaffold(
    title: String,
    exportNote: String,
    searchPlaceholder: String,
    exportButtonText: String,
    showBottomBar: Boolean,
    showBackButton: Boolean,
    searchText: String,
    showSearchBar: Boolean,
    showScrollToTop: Boolean,
    emptyButtonText: String,
    emptyText: String,
    onClickEmptyBtn: () -> Unit,
    onBackClick: () -> Unit,
    onClickDeselect: () -> Unit,
    onClickSelectAll: () -> Unit,
    onClickOpenSearch: () -> Unit,
    onClickCloseSearch: () -> Unit,
    onClearClick: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    onClickExport: () -> Unit,
    onClickScrollToTop: () -> Unit,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(SpaceSmallMax, 0.dp, SpaceSmallMax, SpaceLarge),
    content: @Composable () -> Unit,
) {
    PoposSecondaryScaffold(
        title = title,
        onBackClick = if (showSearchBar) onClickCloseSearch else onBackClick,
        modifier = modifier,
        showBackButton = showBackButton || showSearchBar,
        showBottomBar = showBottomBar,
        showSecondaryBottomBar = true,
        fabPosition = FabPosition.End,
        navigationIcon = {
            AnimatedVisibility(
                visible = !showBackButton,
            ) {
                PoposIconButton(
                    icon = PoposIcons.Close,
                    onClick = onClickDeselect,
                    modifier = Modifier.testTag(CLEAR_ICON),
                    contentDescription = "Deselect All",
                )
            }
        },
        navActions = {
            ExportNavActions(
                placeholderText = searchPlaceholder,
                showSearchBar = showSearchBar,
                showActions = showBottomBar,
                searchText = searchText,
                onClickSelectAll = onClickSelectAll,
                onClickOpenSearch = onClickOpenSearch,
                onClearClick = onClearClick,
                onSearchTextChanged = onSearchTextChanged,
            )
        },
        floatingActionButton = {
            ScrollToTop(
                visible = showScrollToTop,
                onClick = onClickScrollToTop,
            )
        },
        bottomBar = {
            ImportExportButton(
                btnText = exportButtonText,
                noteText = exportNote,
                enabled = showBottomBar,
                onClickExport = onClickExport,
                modifier = Modifier.padding(padding),
            )
        },
    ) {
        Box(
            modifier = Modifier.padding(it),
        ) {
            if (!showBottomBar) {
                ItemNotAvailable(
                    text = emptyText,
                    buttonText = emptyButtonText,
                    onClick = onClickEmptyBtn,
                )
            } else {
                content()
            }
        }
    }
}

@Composable
fun ImportScaffold(
    title: String,
    importFileNote: String,
    importNote: String,
    importButtonText: String,
    showBottomBar: Boolean,
    showBackButton: Boolean,
    showScrollToTop: Boolean,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onClickDeselect: () -> Unit,
    onClickSelectAll: () -> Unit,
    onClickImport: () -> Unit,
    onClickOpenFile: () -> Unit,
    onClickScrollToTop: () -> Unit,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(SpaceSmallMax, 0.dp, SpaceSmallMax, SpaceLarge),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    content: @Composable () -> Unit,
) {
    PoposSecondaryScaffold(
        title = title,
        onBackClick = onBackClick,
        modifier = modifier,
        showBackButton = showBackButton,
        showBottomBar = showBottomBar,
        showSecondaryBottomBar = true,
        fabPosition = FabPosition.End,
        snackbarHostState = snackbarHostState,
        navigationIcon = {
            PoposIconButton(
                icon = PoposIcons.Close,
                onClick = onClickDeselect,
                modifier = Modifier.testTag(CLEAR_ICON),
                contentDescription = "Deselect All",
            )
        },
        navActions = {
            AnimatedVisibility(
                visible = showBottomBar,
            ) {
                IconButton(
                    onClick = onClickSelectAll,
                    modifier = Modifier.testTag(NAV_SELECT_ALL_BTN),
                ) {
                    Icon(
                        imageVector = PoposIcons.Checklist,
                        contentDescription = Constants.SELECT_ALL_ICON,
                    )
                }
            }
        },
        floatingActionButton = {
            ScrollToTop(
                visible = showScrollToTop,
                onClick = onClickScrollToTop,
            )
        },
        bottomBar = {
            ImportExportButton(
                btnText = importButtonText,
                noteText = importNote,
                enabled = showBottomBar,
                icon = PoposIcons.Download,
                onClickExport = onClickImport,
                modifier = Modifier.padding(padding),
            )
        },
    ) {
        Box(
            modifier = Modifier.padding(it),
        ) {
            if (!showBottomBar) {
                EmptyImportScreen(
                    text = importFileNote,
                    buttonText = IMPORT_OPN_FILE,
                    icon = PoposIcons.FileOpen,
                    onClick = onClickOpenFile,
                )
            } else if (isLoading) {
                LoadingIndicator(contentDesc = IMPORT_LOADING)
            } else {
                content()
            }
        }
    }
}

@Composable
fun ExportNavActions(
    placeholderText: String,
    showSearchBar: Boolean,
    showActions: Boolean,
    searchText: String,
    onClickSelectAll: () -> Unit,
    onClickOpenSearch: () -> Unit,
    onClearClick: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        if (showSearchBar) {
            StandardSearchBar(
                searchText = searchText,
                placeholderText = placeholderText,
                onClearClick = onClearClick,
                onSearchTextChanged = onSearchTextChanged,
            )
        } else {
            if (showActions) {
                IconButton(
                    onClick = onClickSelectAll,
                    modifier = Modifier.testTag(NAV_SELECT_ALL_BTN),
                ) {
                    Icon(
                        imageVector = PoposIcons.Checklist,
                        contentDescription = Constants.SELECT_ALL_ICON,
                    )
                }

                IconButton(
                    onClick = onClickOpenSearch,
                    modifier = Modifier.testTag(NAV_SEARCH_BTN),
                ) {
                    Icon(
                        imageVector = PoposIcons.Search,
                        contentDescription = "Search Icon",
                    )
                }
            }
        }
    }
}

@Composable
fun ImportExportButton(
    btnText: String,
    noteText: String,
    enabled: Boolean,
    onClickExport: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = PoposIcons.Upload,
    containerColor: Color = if (icon == PoposIcons.Upload) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.primary
    },
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
    ) {
        InfoText(text = noteText)

        PoposButton(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(IMPORT_EXPORT_BTN),
            enabled = enabled,
            text = btnText,
            icon = PoposIcons.Upload,
            colors = ButtonDefaults.buttonColors(containerColor = containerColor),
            onClick = onClickExport,
        )
    }
}

@DevicePreviews
@Composable
private fun ImportExportButtonPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        ImportExportButton(
            btnText = "Export",
            noteText = "Export your data",
            enabled = true,
            onClickExport = {},
            modifier = modifier,
        )
    }
}
