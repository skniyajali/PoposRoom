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

package com.niyaj.addonitem.settings

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.addonitem.components.AddOnItemData
import com.niyaj.addonitem.destinations.AddEditAddOnItemScreenDestination
import com.niyaj.common.tags.AddOnTestTags.ADDON_ITEM_LIST
import com.niyaj.common.tags.AddOnTestTags.ADDON_NOT_AVAILABLE
import com.niyaj.common.tags.AddOnTestTags.ADDON_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.AddOnTestTags.CREATE_NEW_ADD_ON
import com.niyaj.common.tags.AddOnTestTags.EXPORT_ADDON_BTN
import com.niyaj.common.tags.AddOnTestTags.EXPORT_ADDON_FILE_NAME
import com.niyaj.common.tags.AddOnTestTags.EXPORT_ADDON_TITLE
import com.niyaj.common.utils.Constants.SEARCH_ITEM_NOT_FOUND
import com.niyaj.common.utils.createExportNote
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.domain.utils.ImportExport
import com.niyaj.model.AddOnItem
import com.niyaj.ui.components.ExportScaffold
import com.niyaj.ui.parameterProvider.AddOnPreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens.ADD_ON_EXPORT_SCREEN
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@Destination(route = ADD_ON_EXPORT_SCREEN)
@Composable
fun AddOnExportScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: AddOnSettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val addOnItems = viewModel.addonItems.collectAsStateWithLifecycle().value
    val exportedItems = viewModel.exportedItems.collectAsStateWithLifecycle().value

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value
    val selectedItems = viewModel.selectedItems.toList()

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(data.errorMessage)
                }

                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(data.successMessage)
                }
            }
        }
    }

    val exportLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            it.data?.data?.let {
                scope.launch {
                    val result = ImportExport.writeDataAsync(context, it, exportedItems)

                    if (result.isSuccess) {
                        resultBackNavigator.navigateBack("${exportedItems.size} Items has been exported.")
                    } else {
                        resultBackNavigator.navigateBack("Unable to export addon items.")
                    }
                }
            }
        }

    AddOnExportScreenContent(
        addOnItems = addOnItems.toImmutableList(),
        selectedItems = selectedItems.toImmutableList(),
        showSearchBar = showSearchBar,
        searchText = searchText,
        onClearClick = viewModel::clearSearchText,
        onSearchTextChanged = viewModel::searchTextChanged,
        onClickOpenSearch = viewModel::openSearchBar,
        onClickCloseSearch = viewModel::closeSearchBar,
        onClickSelectAll = viewModel::selectAllItems,
        onClickDeselect = viewModel::deselectItems,
        onSelectItem = viewModel::selectItem,
        onClickExport = {
            scope.launch {
                val result = ImportExport.createFile(
                    context = context,
                    fileName = EXPORT_ADDON_FILE_NAME,
                )
                exportLauncher.launch(result)
                viewModel.onEvent(AddOnSettingsEvent.GetExportedItems)
            }
        },
        onBackClick = navigator::navigateUp,
        onClickToAddItem = {
            navigator.navigate(AddEditAddOnItemScreenDestination())
        },
        modifier = Modifier,
    )
}

@VisibleForTesting
@Composable
internal fun AddOnExportScreenContent(
    addOnItems: ImmutableList<AddOnItem>,
    selectedItems: ImmutableList<Int>,
    showSearchBar: Boolean,
    searchText: String,
    onClearClick: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    onClickOpenSearch: () -> Unit,
    onClickCloseSearch: () -> Unit,
    onClickSelectAll: () -> Unit,
    onClickDeselect: () -> Unit,
    onSelectItem: (Int) -> Unit,
    onClickExport: () -> Unit,
    onBackClick: () -> Unit,
    onClickToAddItem: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TrackScreenViewEvent(screenName = ADD_ON_EXPORT_SCREEN)

    val scope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val emptyText = if (searchText.isEmpty()) ADDON_NOT_AVAILABLE else SEARCH_ITEM_NOT_FOUND
    val title =
        if (selectedItems.isEmpty()) EXPORT_ADDON_TITLE else "${selectedItems.size} Selected"

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            onClickDeselect()
        } else if (showSearchBar) {
            onClickCloseSearch()
        } else {
            onBackClick()
        }
    }

    ExportScaffold(
        title = title,
        exportNote = createExportNote(selectedItems, addOnItems.size, "addon item"),
        searchPlaceholder = ADDON_SEARCH_PLACEHOLDER,
        exportButtonText = EXPORT_ADDON_BTN,
        emptyButtonText = CREATE_NEW_ADD_ON,
        emptyText = emptyText,
        showBottomBar = addOnItems.isNotEmpty(),
        showBackButton = selectedItems.isEmpty(),
        searchText = searchText,
        showSearchBar = showSearchBar,
        showScrollToTop = !lazyGridState.isScrollingUp(),
        onBackClick = onBackClick,
        onClickDeselect = onClickDeselect,
        onClickSelectAll = onClickSelectAll,
        onClickOpenSearch = onClickOpenSearch,
        onClickCloseSearch = onClickCloseSearch,
        onClearClick = onClearClick,
        onSearchTextChanged = onSearchTextChanged,
        onClickExport = onClickExport,
        onClickEmptyBtn = onClickToAddItem,
        onClickScrollToTop = {
            scope.launch {
                lazyGridState.animateScrollToItem(0)
            }
        },
        modifier = modifier,
    ) {
        AddOnExportScreenData(
            addOnItems = addOnItems,
            onSelectItem = onSelectItem,
            doesSelected = selectedItems::contains,
            modifier = Modifier
                .fillMaxSize(),
            lazyGridState = lazyGridState,
        )
    }
}

@Composable
private fun AddOnExportScreenData(
    addOnItems: ImmutableList<AddOnItem>,
    onSelectItem: (Int) -> Unit,
    doesSelected: (Int) -> Boolean,
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {
    TrackScrollJank(scrollableState = lazyGridState, stateName = "addon-export:list")

    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .testTag(ADDON_ITEM_LIST),
        contentPadding = PaddingValues(SpaceSmall),
        columns = GridCells.Fixed(2),
        state = lazyGridState,
    ) {
        items(
            items = addOnItems,
            key = { it.itemId },
        ) { item: AddOnItem ->
            AddOnItemData(
                item = item,
                doesSelected = doesSelected,
                onClick = onSelectItem,
                onLongClick = onSelectItem,
            )
        }
    }
}

@DevicePreviews
@Composable
private fun AddOnExportScreenContentPreview(
    items: ImmutableList<AddOnItem> = AddOnPreviewData.addOnItemList.toImmutableList(),
) {
    PoposRoomTheme {
        AddOnExportScreenContent(
            addOnItems = items,
            selectedItems = persistentListOf(),
            showSearchBar = false,
            searchText = "",
            onClearClick = {},
            onSearchTextChanged = {},
            onClickOpenSearch = {},
            onClickCloseSearch = {},
            onClickSelectAll = {},
            onClickDeselect = {},
            onSelectItem = {},
            onClickExport = {},
            onBackClick = {},
            onClickToAddItem = {},
            modifier = Modifier,
        )
    }
}

@DevicePreviews
@Composable
private fun AddOnExportScreenEmptyDataPreview() {
    PoposRoomTheme {
        AddOnExportScreenData(
            addOnItems = persistentListOf(),
            onSelectItem = {},
            doesSelected = { false },
        )
    }
}

@DevicePreviews
@Composable
private fun AddOnExportScreenDataPreview(
    items: ImmutableList<AddOnItem> = AddOnPreviewData.addOnItemList.toImmutableList(),
) {
    PoposRoomTheme {
        AddOnExportScreenData(
            addOnItems = items,
            onSelectItem = {},
            doesSelected = { false },
        )
    }
}
