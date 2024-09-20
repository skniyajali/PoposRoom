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

package com.niyaj.feature.market.marketList

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FabPosition
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.MarketListTestTags.CREATE_NEW_LIST
import com.niyaj.common.tags.MarketListTestTags.DELETE_LIST_MESSAGE
import com.niyaj.common.tags.MarketListTestTags.DELETE_LIST_TITLE
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.MarketListTestTags.MARKET_LIST_NOT_AVAILABLE
import com.niyaj.common.tags.MarketListTestTags.MARKET_LIST_SCREEN_TITLE
import com.niyaj.common.utils.Constants.SEARCH_ITEM_NOT_FOUND
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.market.components.MarketListItemCard
import com.niyaj.feature.market.components.ShareableMarketList
import com.niyaj.feature.market.destinations.AddEditMarketListScreenDestination
import com.niyaj.feature.market.destinations.MarketItemSettingsScreenDestination
import com.niyaj.feature.market.destinations.MarketListItemScreenDestination
import com.niyaj.feature.market.destinations.MarketListItemsScreenDestination
import com.niyaj.model.MarketListWithTypes
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.PoposPrimaryScaffold
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.event.ShareViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.MarketListPreviewParameter
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens.MARKET_LIST_SCREEN
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.niyaj.ui.utils.rememberCaptureController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Composable
@Destination(route = MARKET_LIST_SCREEN)
fun MarketListScreen(
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<AddEditMarketListScreenDestination, String>,
    modifier: Modifier = Modifier,
    viewModel: MarketListViewModel = hiltViewModel(),
    shareViewModel: ShareViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val captureController = rememberCaptureController()

    val state = viewModel.items.collectAsStateWithLifecycle().value
    val items = viewModel.shareableItems.collectAsStateWithLifecycle().value
    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val showDialog = shareViewModel.showDialog.collectAsStateWithLifecycle().value
    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    val searchText = viewModel.searchText.value
    val selectedItems = viewModel.selectedItems.toList()

    val marketDate = remember {
        mutableLongStateOf(0)
    }

    MarketListScreenContent(
        uiState = state,
        selectedItems = selectedItems,
        showSearchBar = showSearchBar,
        searchText = searchText,
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
        doesExpanded = viewModel::doesExpanded,
        onClickExpand = viewModel::onClickExpand,
        onClickPrint = viewModel::printMarketList,
        snackbarState = snackbarState,
        modifier = modifier,
        onClickCreateNew = {
            navigator.navigate(AddEditMarketListScreenDestination())
        },
        onClickEdit = {
            navigator.navigate(AddEditMarketListScreenDestination(it))
        },
        onClickSettings = {
            navigator.navigate(MarketItemSettingsScreenDestination())
        },
        onClickShare = { ids, date ->
            scope.launch {
                async {
                    viewModel.getListItems(ids)
                }.await()

                marketDate.longValue = date
                shareViewModel.onShowDialog()
            }
        },
        onClickViewDetails = {
            navigator.navigate(MarketListItemsScreenDestination(it.toIntArray()))
        },
        onClickManageList = {
            navigator.navigate(MarketListItemScreenDestination(it))
        },
    )

    AnimatedVisibility(
        visible = showDialog,
    ) {
        if (items.isNotEmpty()) {
            ShareableMarketList(
                captureController = captureController,
                marketDate = marketDate.longValue,
                marketLists = items,
                onDismiss = shareViewModel::onDismissDialog,
                onClickShare = {
                    captureController.captureLongScreenshot()
                },
                onCaptured = { bitmap, error ->
                    bitmap?.let {
                        scope.launch {
                            val uri = shareViewModel.saveImage(it, context)
                            uri?.let {
                                shareViewModel.shareContent(context, "Share Image", uri)
                            }
                        }
                    }
                    error?.let {
                        Log.d("Capturable", "Error: ${it.message}\n${it.stackTrace.joinToString()}")
                    }
                },
            )
        }
    }

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
internal fun MarketListScreenContent(
    uiState: UiState<List<MarketListWithTypes>>,
    selectedItems: List<Int>,
    doesExpanded: (Int) -> Boolean,
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
    onClickExpand: (Int) -> Unit,
    onClickShare: (List<Int>, Long) -> Unit,
    onClickPrint: (List<Int>, Long) -> Unit,
    onClickViewDetails: (List<Int>) -> Unit,
    onClickManageList: (Int) -> Unit,
    modifier: Modifier = Modifier,
    snackbarState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {
    TrackScreenViewEvent(screenName = MARKET_LIST_SCREEN)

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
        currentRoute = MARKET_LIST_SCREEN,
        title = if (selectedItems.isEmpty()) MARKET_LIST_SCREEN_TITLE else "${selectedItems.size} Selected",
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
                fabText = CREATE_NEW_LIST,
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
            label = "MarketList::UiState",
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) MARKET_LIST_NOT_AVAILABLE else SEARCH_ITEM_NOT_FOUND,
                        buttonText = CREATE_NEW_LIST,
                        onClick = onClickCreateNew,
                    )
                }

                is UiState.Success -> {
                    TrackScrollJank(
                        scrollableState = lazyGridState,
                        stateName = "MarketList::State",
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(SpaceSmall),
                        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                    ) {
                        items(
                            items = state.data,
                            key = {
                                it.marketList.marketId
                            },
                        ) { items ->
                            MarketListItemCard(
                                items = items,
                                doesSelected = selectedItems::contains,
                                doesExpanded = doesExpanded,
                                onClick = {
                                    if (selectedItems.isEmpty()) {
                                        onClickExpand(it)
                                    } else {
                                        onClickSelectItem(it)
                                    }
                                },
                                onLongClick = onClickSelectItem,
                                onClickShare = { onClickShare(it, items.marketList.marketDate) },
                                onClickPrint = { onClickPrint(it, items.marketList.marketDate) },
                                onClickViewDetails = onClickViewDetails,
                                onClickManageList = onClickManageList,
                            )
                        }
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = openDialog.value,
    ) {
        StandardDialog(
            title = DELETE_LIST_TITLE,
            message = DELETE_LIST_MESSAGE,
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
    resultRecipient: ResultRecipient<AddEditMarketListScreenDestination, String>,
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
private fun MarketListScreenPreview(
    @PreviewParameter(MarketListPreviewParameter::class)
    uiState: UiState<List<MarketListWithTypes>>,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        MarketListScreenContent(
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
            doesExpanded = { true },
            onClickExpand = {},
            onClickShare = { _, _ -> },
            onClickPrint = { _, _ -> },
            onClickViewDetails = {},
            onClickManageList = {},
        )
    }
}
