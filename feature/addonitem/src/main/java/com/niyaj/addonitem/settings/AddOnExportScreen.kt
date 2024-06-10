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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.niyaj.common.tags.AddOnTestTags
import com.niyaj.common.tags.AddOnTestTags.ADDON_NOT_AVAILABLE
import com.niyaj.common.tags.AddOnTestTags.EXPORT_ADDON_BTN
import com.niyaj.common.tags.AddOnTestTags.EXPORT_ADDON_BTN_TEXT
import com.niyaj.common.tags.AddOnTestTags.EXPORT_ADDON_FILE_NAME
import com.niyaj.common.tags.AddOnTestTags.EXPORT_ADDON_TITLE
import com.niyaj.common.utils.Constants
import com.niyaj.common.utils.Constants.SEARCH_ITEM_NOT_FOUND
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceLarge
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.domain.utils.ImportExport
import com.niyaj.model.AddOnItem
import com.niyaj.ui.components.InfoText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardSearchBar
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
        modifier = Modifier,
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
    )
}

// TODO:: fix bottomBar padding issue
@VisibleForTesting
@Composable
internal fun AddOnExportScreenContent(
    modifier: Modifier = Modifier,
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
) {
    TrackScreenViewEvent(screenName = ADD_ON_EXPORT_SCREEN)

    val scope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val emptyText = if (searchText.isEmpty()) ADDON_NOT_AVAILABLE else SEARCH_ITEM_NOT_FOUND

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            onClickDeselect()
        } else if (showSearchBar) {
            onClickCloseSearch()
        } else {
            onBackClick()
        }
    }

    PoposSecondaryScaffold(
        title = if (selectedItems.isEmpty()) EXPORT_ADDON_TITLE else "${selectedItems.size} Selected",
        showBackButton = selectedItems.isEmpty() || showSearchBar,
        showBottomBar = addOnItems.isNotEmpty(),
        navActions = {
            if (showSearchBar) {
                StandardSearchBar(
                    searchText = searchText,
                    placeholderText = "Search for addon items...",
                    onClearClick = onClearClick,
                    onSearchTextChanged = onSearchTextChanged,
                )
            } else {
                if (addOnItems.isNotEmpty()) {
                    IconButton(
                        onClick = onClickSelectAll,
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
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpaceSmallMax, vertical = SpaceLarge),
                verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            ) {
                InfoText(text = "${if (selectedItems.isEmpty()) "All" else "${selectedItems.size}"} addon items will be exported.")

                PoposButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(EXPORT_ADDON_BTN),
                    enabled = addOnItems.isNotEmpty(),
                    text = EXPORT_ADDON_BTN_TEXT,
                    icon = PoposIcons.Upload,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                    ),
                    onClick = onClickExport,
                )
            }
        },
        onBackClick = if (showSearchBar) onClickCloseSearch else onBackClick,
        fabPosition = FabPosition.End,
        floatingActionButton = {
            ScrollToTop(
                visible = !lazyGridState.isScrollingUp(),
                onClick = {
                    scope.launch {
                        lazyGridState.animateScrollToItem(index = 0)
                    }
                },
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onClickDeselect,
            ) {
                Icon(
                    imageVector = PoposIcons.Close,
                    contentDescription = "Deselect All",
                )
            }
        },
    ) { paddingValues ->
        AddOnExportScreenData(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            emptyText = emptyText,
            addOnItems = addOnItems,
            onClickToAddItem = onClickToAddItem,
            onSelectItem = onSelectItem,
            doesSelected = selectedItems::contains,
            lazyGridState = lazyGridState,
        )
    }
}

@Composable
private fun AddOnExportScreenData(
    modifier: Modifier = Modifier,
    emptyText: String = ADDON_NOT_AVAILABLE,
    addOnItems: ImmutableList<AddOnItem>,
    onClickToAddItem: () -> Unit,
    onSelectItem: (Int) -> Unit,
    doesSelected: (Int) -> Boolean,
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {
    if (addOnItems.isEmpty()) {
        ItemNotAvailable(
            text = emptyText,
            buttonText = AddOnTestTags.CREATE_NEW_ADD_ON,
            onClick = onClickToAddItem,
        )
    } else {
        TrackScrollJank(scrollableState = lazyGridState, stateName = "addon-export:list")

        LazyVerticalGrid(
            modifier = modifier
                .fillMaxSize(),
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
}

@DevicePreviews
@Composable
private fun AddOnExportScreenContentPreview(
    items: ImmutableList<AddOnItem> = AddOnPreviewData.addOnItemList.toImmutableList(),
) {
    PoposRoomTheme {
        AddOnExportScreenContent(
            modifier = Modifier,
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
        )
    }
}

@DevicePreviews
@Composable
private fun AddOnExportScreenEmptyDataPreview() {
    PoposRoomTheme {
        AddOnExportScreenData(
            addOnItems = persistentListOf(),
            onClickToAddItem = {},
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
            onClickToAddItem = {},
            onSelectItem = {},
            doesSelected = { false },
        )
    }
}
