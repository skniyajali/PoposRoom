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

package com.niyaj.feature.market.marketItem

import androidx.activity.compose.BackHandler
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.MarketListTestTags.CREATE_NEW_ITEM
import com.niyaj.common.tags.MarketListTestTags.DELETE_ITEM_MESSAGE
import com.niyaj.common.tags.MarketListTestTags.DELETE_ITEM_TITLE
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NOT_AVAILABLE
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_SCREEN_TITLE
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_SEARCH_PLACEHOLDER
import com.niyaj.common.utils.Constants.SEARCH_ITEM_NOT_FOUND
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.feature.market.components.MarketItemCardList
import com.niyaj.feature.market.destinations.AddEditMarketItemScreenDestination
import com.niyaj.feature.market.destinations.MarketItemSettingsScreenDestination
import com.niyaj.feature.market.destinations.MarketListScreenDestination
import com.niyaj.model.MarketItem
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.PoposPrimaryScaffold
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.MarketItemPreviewParameter
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens.MARKET_ITEM_SCREEN
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination(route = MARKET_ITEM_SCREEN)
@Composable
fun MarketItemScreen(
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<AddEditMarketItemScreenDestination, String>,
    modifier: Modifier = Modifier,
    viewModel: MarketItemViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }

    val state by viewModel.marketItems.collectAsStateWithLifecycle()
    val showSearchBar by viewModel.showSearchBar.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val selectedItems = viewModel.selectedItems.toList()
    val searchText = viewModel.searchText.value

    MarketItemScreenContent(
        uiState = state,
        selectedItems = selectedItems,
        showSearchBar = showSearchBar,
        searchText = searchText,
        modifier = modifier,
        onClickSearchIcon = viewModel::openSearchBar,
        onSearchTextChanged = viewModel::searchTextChanged,
        onClickClear = viewModel::clearSearchText,
        onCloseSearchBar = viewModel::closeSearchBar,
        onClickSelectItem = viewModel::selectItem,
        onClickSelectAll = viewModel::selectAllItems,
        onClickDeselect = viewModel::deselectItems,
        onClickDelete = viewModel::deleteItems,
        onClickBack = navigator::popBackStack,
        onNavigateToScreen = navigator::navigate,
        onClickCreateNew = {
            navigator.navigate(AddEditMarketItemScreenDestination())
        },
        onClickEdit = {
            navigator.navigate(AddEditMarketItemScreenDestination(it))
        },
        onClickSettings = {
            navigator.navigate(MarketItemSettingsScreenDestination())
        },
        onNavigateToListScreen = {
            navigator.navigate(MarketListScreenDestination)
        },
        snackbarState = snackbarState,
    )

    HandleResultRecipients(
        resultRecipient = resultRecipient,
        event = event,
        onDeselectItems = viewModel::deselectItems,
        coroutineScope = scope,
        snackbarHostState = snackbarState,
    )
}

@VisibleForTesting
@Composable
internal fun MarketItemScreenContent(
    uiState: UiState<List<MarketItem>>,
    selectedItems: List<Int>,
    showSearchBar: Boolean,
    searchText: String,
    onClickSearchIcon: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    onClickClear: () -> Unit,
    onCloseSearchBar: () -> Unit,
    onClickSelectItem: (Int) -> Unit,
    onClickSelectAll: () -> Unit,
    onClickDeselect: () -> Unit,
    onClickDelete: () -> Unit,
    onClickBack: () -> Unit,
    onNavigateToScreen: (String) -> Unit,
    onClickCreateNew: () -> Unit,
    onClickEdit: (Int) -> Unit,
    onClickSettings: () -> Unit,
    onNavigateToListScreen: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {
    TrackScreenViewEvent(screenName = MARKET_ITEM_SCREEN)

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            onClickDeselect()
        } else if (showSearchBar) {
            onCloseSearchBar()
        } else {
            onClickBack()
        }
    }

    val showFab = uiState is UiState.Success
    val openDialog = remember { mutableStateOf(false) }

    PoposPrimaryScaffold(
        currentRoute = MARKET_ITEM_SCREEN,
        title = if (selectedItems.isEmpty()) MARKET_ITEM_SCREEN_TITLE else "${selectedItems.size} Selected",
        selectionCount = selectedItems.size,
        floatingActionButton = {
            StandardFAB(
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = onClickCreateNew,
                onClickScroll = {
                    scope.launch {
                        lazyGridState.animateScrollToItem(0)
                    }
                },
                showScrollToTop = lazyGridState.isScrolled,
                fabText = CREATE_NEW_ITEM,
            )
        },
        navActions = {
            ScaffoldNavActions(
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                searchText = searchText,
                onEditClick = {
                    onClickEdit(selectedItems.first())
                },
                onDeleteClick = {
                    openDialog.value = true
                },
                onSelectAllClick = onClickSelectAll,
                onClearClick = onClickClear,
                onSearchIconClick = onClickSearchIcon,
                onSearchTextChanged = onSearchTextChanged,
                showSearchBar = showSearchBar,
                showSettingsIcon = true,
                onSettingsClick = onClickSettings,
                placeholderText = MARKET_ITEM_SEARCH_PLACEHOLDER,
            )
        },
        onBackClick = if (showSearchBar) onCloseSearchBar else onClickBack,
        onNavigateToScreen = onNavigateToScreen,
        modifier = modifier,
        fabPosition = if (lazyGridState.isScrolled) FabPosition.End else FabPosition.Center,
        showBackButton = showSearchBar,
        onDeselect = onClickDeselect,
        snackbarHostState = snackbarState,
    ) {
        Crossfade(
            targetState = uiState,
            label = "MarketItem::UiState",
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) MARKET_ITEM_NOT_AVAILABLE else SEARCH_ITEM_NOT_FOUND,
                        buttonText = CREATE_NEW_ITEM,
                        onClick = onClickCreateNew,
                    )
                }

                is UiState.Success -> {
                    MarketItemCardList(
                        items = state.data.toImmutableList(),
                        isInSelectionMode = selectedItems.isNotEmpty(),
                        doesSelected = selectedItems::contains,
                        onSelectItem = onClickSelectItem,
                        modifier = Modifier,
                        showSettingsCard = true,
                        onClickCard = onNavigateToListScreen,
                        lazyGridState = lazyGridState,
                    )
                }
            }
        }
    }

    AnimatedVisibility(
        visible = openDialog.value,
    ) {
        StandardDialog(
            title = DELETE_ITEM_TITLE,
            message = DELETE_ITEM_MESSAGE,
            onConfirm = {
                openDialog.value = false
                onClickDelete()
            },
            onDismiss = {
                openDialog.value = false
                onClickDeselect()
            },
        )
    }
}

@Composable
private fun HandleResultRecipients(
    resultRecipient: ResultRecipient<AddEditMarketItemScreenDestination, String>,
    event: UiEvent?,
    onDeselectItems: () -> Unit,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                onDeselectItems()
            }

            is NavResult.Value -> {
                onDeselectItems()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
                is UiEvent.OnError -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(data.errorMessage)
                    }
                }

                is UiEvent.OnSuccess -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(data.successMessage)
                    }
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun MarketItemScreenPreview(
    @PreviewParameter(MarketItemPreviewParameter::class)
    uiState: UiState<List<MarketItem>>,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        MarketItemScreenContent(
            modifier = modifier,
            uiState = uiState,
            selectedItems = listOf(),
            showSearchBar = false,
            searchText = "",
            onClickSearchIcon = {},
            onSearchTextChanged = {},
            onClickClear = {},
            onCloseSearchBar = {},
            onClickSelectItem = {},
            onClickSelectAll = {},
            onClickDeselect = {},
            onClickDelete = {},
            onClickBack = {},
            onNavigateToScreen = {},
            onClickCreateNew = {},
            onClickEdit = {},
            onClickSettings = {},
            onNavigateToListScreen = {},
        )
    }
}
