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

package com.niyaj.address.settings

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.address.components.AddressData
import com.niyaj.address.destinations.AddEditAddressScreenDestination
import com.niyaj.common.tags.AddressTestTags.ADDRESS_ITEM_TAG
import com.niyaj.common.tags.AddressTestTags.ADDRESS_LIST
import com.niyaj.common.tags.AddressTestTags.ADDRESS_NOT_AVAILABLE
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.AddressTestTags.CREATE_NEW_ADDRESS
import com.niyaj.common.tags.AddressTestTags.EXPORT_ADDRESS_FILE_NAME
import com.niyaj.common.tags.AddressTestTags.EXPORT_ADDRESS_TITLE
import com.niyaj.common.utils.Constants
import com.niyaj.common.utils.createExportNote
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.domain.utils.ImportExport
import com.niyaj.model.Address
import com.niyaj.ui.components.ExportScaffold
import com.niyaj.ui.parameterProvider.AddressPreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens.ADDRESS_EXPORT_SCREEN
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Destination(route = ADDRESS_EXPORT_SCREEN)
@Composable
fun AddressExportScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: AddressSettingsViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    val addresses by viewModel.addresses.collectAsStateWithLifecycle()
    val exportedItems by viewModel.exportedItems.collectAsStateWithLifecycle()
    val showSearchBar by viewModel.showSearchBar.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val searchText = viewModel.searchText.value
    val selectedItems = viewModel.selectedItems.toList()

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

    val context = LocalContext.current

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
                        resultBackNavigator.navigateBack("Unable to export addresses.")
                    }
                }
            }
        }

    AddressExportScreenContent(
        modifier = Modifier,
        items = addresses.toImmutableList(),
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
                    fileName = EXPORT_ADDRESS_FILE_NAME,
                )
                exportLauncher.launch(result)
                viewModel.onEvent(AddressSettingsEvent.GetExportedItems)
            }
        },
        onBackClick = navigator::navigateUp,
        onClickToAddItem = {
            navigator.navigate(AddEditAddressScreenDestination())
        },
    )
}

@VisibleForTesting
@Composable
internal fun AddressExportScreenContent(
    modifier: Modifier = Modifier,
    items: ImmutableList<Address>,
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
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {
    TrackScreenViewEvent(screenName = ADDRESS_EXPORT_SCREEN)

    val text = if (searchText.isEmpty()) ADDRESS_NOT_AVAILABLE else Constants.SEARCH_ITEM_NOT_FOUND
    val title =
        if (selectedItems.isEmpty()) EXPORT_ADDRESS_TITLE else "${selectedItems.size} Selected"

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
        exportNote = createExportNote(selectedItems, items.size, "address"),
        searchPlaceholder = ADDRESS_SEARCH_PLACEHOLDER,
        exportButtonText = EXPORT_ADDRESS_TITLE,
        emptyButtonText = CREATE_NEW_ADDRESS,
        emptyText = text,
        showBottomBar = items.isNotEmpty(),
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
        AddressExportScreenData(
            modifier = modifier
                .fillMaxSize(),
            items = items,
            onSelectItem = onSelectItem,
            doesSelected = selectedItems::contains,
            lazyGridState = lazyGridState,
        )
    }
}

@Composable
private fun AddressExportScreenData(
    modifier: Modifier = Modifier,
    items: ImmutableList<Address>,
    onSelectItem: (Int) -> Unit,
    doesSelected: (Int) -> Boolean,
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {
    TrackScrollJank(scrollableState = lazyGridState, stateName = "Exported Address::List")

    LazyVerticalGrid(
        modifier = modifier
            .testTag(ADDRESS_LIST)
            .fillMaxSize(),
        contentPadding = PaddingValues(SpaceSmall),
        columns = GridCells.Fixed(2),
        state = lazyGridState,
    ) {
        items(
            items = items,
            key = {
                it.addressName.plus(it.addressId)
            },
        ) { address ->
            AddressData(
                modifier = Modifier.testTag(ADDRESS_ITEM_TAG.plus(address.addressId)),
                item = address,
                doesSelected = doesSelected,
                onClick = onSelectItem,
                onLongClick = onSelectItem,
            )
        }
    }
}

@DevicePreviews
@Composable
private fun AddressExportScreenContentPreview(
    items: ImmutableList<Address> = AddressPreviewData.addressList.toImmutableList(),
) {
    PoposRoomTheme {
        AddressExportScreenContent(
            modifier = Modifier,
            items = items,
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
private fun AddressExportScreenEmptyDataPreview() {
    PoposRoomTheme {
        AddressExportScreenData(
            items = persistentListOf(),
            onSelectItem = {},
            doesSelected = { false },
        )
    }
}

@DevicePreviews
@Composable
private fun AddressExportScreenDataPreview(
    items: ImmutableList<Address> = AddressPreviewData.addressList.toImmutableList(),
) {
    PoposRoomTheme {
        AddressExportScreenData(
            items = items,
            onSelectItem = {},
            doesSelected = { false },
        )
    }
}
