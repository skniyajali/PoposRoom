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

package com.niyaj.feature.market.marketItem.settings

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.MarketListTestTags
import com.niyaj.common.tags.MarketListTestTags.EXPORT_MARKET_ITEM_FILE_NAME
import com.niyaj.common.tags.MarketListTestTags.EXPORT_MARKET_ITEM_TITLE
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NOT_AVAILABLE
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_SEARCH_PLACEHOLDER
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceLarge
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.domain.utils.ImportExport
import com.niyaj.feature.market.components.MarketItemCardList
import com.niyaj.feature.market.destinations.AddEditMarketItemScreenDestination
import com.niyaj.model.MarketItem
import com.niyaj.ui.components.InfoText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardSearchBar
import com.niyaj.ui.parameterProvider.MarketItemPreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.TrackScreenViewEvent
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

@Destination
@Composable
fun ExportMarketItemScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    modifier: Modifier = Modifier,
    viewModel: MarketItemSettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val items by viewModel.items.collectAsStateWithLifecycle()
    val exportedItems by viewModel.exportedItems.collectAsStateWithLifecycle()
    val showSearchBar by viewModel.showSearchBar.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val searchText = viewModel.searchText.value
    val selectedItems = viewModel.selectedItems.toList()
    val isLoading = remember { mutableStateOf(false) }

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
                    isLoading.value = true
                    val result = ImportExport.writeDataAsync(context, it, exportedItems)

                    if (result.isSuccess) {
                        resultBackNavigator.navigateBack("${exportedItems.size} Items has been exported.")
                    } else {
                        resultBackNavigator.navigateBack("Unable to export items.")
                    }
                }
            }
        }

    ExportMarketItemScreenContent(
        modifier = modifier,
        items = items.toImmutableList(),
        selectedItems = selectedItems.toImmutableList(),
        isLoading = isLoading.value,
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
                    fileName = EXPORT_MARKET_ITEM_FILE_NAME,
                )
                exportLauncher.launch(result)
                viewModel.onEvent(MarketItemSettingsEvent.GetExportedMarketItem)
            }
        },
        onBackClick = navigator::navigateUp,
        onClickToAddItem = {
            navigator.navigate(AddEditMarketItemScreenDestination())
        },
    )
}

@VisibleForTesting
@Composable
internal fun ExportMarketItemScreenContent(
    items: ImmutableList<MarketItem>,
    selectedItems: ImmutableList<Int>,
    isLoading: Boolean,
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
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    padding: PaddingValues = PaddingValues(SpaceSmallMax, 0.dp, SpaceSmallMax, SpaceLarge),
) {
    TrackScreenViewEvent(screenName = "ExportMarketItemScreen")

    val text =
        if (searchText.isEmpty()) MARKET_ITEM_NOT_AVAILABLE else Constants.SEARCH_ITEM_NOT_FOUND
    val title =
        if (selectedItems.isEmpty()) EXPORT_MARKET_ITEM_TITLE else "${selectedItems.size} Selected"

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
        title = title,
        onBackClick = if (showSearchBar) onClickCloseSearch else onBackClick,
        showBackButton = selectedItems.isEmpty() || showSearchBar,
        showBottomBar = items.isNotEmpty(),
        showSecondaryBottomBar = true,
        fabPosition = FabPosition.End,
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
        navActions = {
            if (showSearchBar) {
                StandardSearchBar(
                    searchText = searchText,
                    placeholderText = MARKET_ITEM_SEARCH_PLACEHOLDER,
                    onClearClick = onClearClick,
                    onSearchTextChanged = onSearchTextChanged,
                )
            } else {
                if (items.isNotEmpty()) {
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
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            ) {
                InfoText(
                    text = "${
                        if (selectedItems.isEmpty()) {
                            "All"
                        } else {
                            "${selectedItems.size}"
                        }
                    } market item will be exported.",
                )

                PoposButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(EXPORT_MARKET_ITEM_TITLE),
                    enabled = items.isNotEmpty(),
                    text = EXPORT_MARKET_ITEM_TITLE,
                    icon = PoposIcons.Upload,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                    ),
                    onClick = onClickExport,
                )
            }
        },
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(it),
        ) {
            if (items.isEmpty()) {
                ItemNotAvailable(
                    text = text,
                    buttonText = MarketListTestTags.CREATE_NEW_ITEM,
                    onClick = onClickToAddItem,
                )
            } else if (isLoading) {
                LoadingIndicator()
            } else {
                MarketItemCardList(
                    items = items,
                    isInSelectionMode = true,
                    doesSelected = selectedItems::contains,
                    onSelectItem = onSelectItem,
                    modifier = Modifier,
                    lazyGridState = lazyGridState,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun ExportMarketItemScreenContentEmptyPreview() {
    PoposRoomTheme {
        ExportMarketItemScreenContent(
            modifier = Modifier,
            items = persistentListOf(),
            selectedItems = persistentListOf(),
            isLoading = false,
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
private fun ExportMarketItemScreenContentPreview(
    items: ImmutableList<MarketItem> = MarketItemPreviewData.marketItems.toImmutableList(),
) {
    PoposRoomTheme {
        ExportMarketItemScreenContent(
            modifier = Modifier,
            items = items,
            selectedItems = persistentListOf(),
            isLoading = false,
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
