/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.market.market_list

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.MarketListTestTags
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.MarketListTestTags.MARKET_LIST_SCREEN_TITLE
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.market.components.MarketListItemCard
import com.niyaj.market.components.ShareableMarketList
import com.niyaj.market.destinations.AddEditMarketListScreenDestination
import com.niyaj.market.destinations.MarketItemScreenDestination
import com.niyaj.market.destinations.MarketListItemScreenDestination
import com.niyaj.market.destinations.MarketListItemsScreenDestination
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardScaffoldRoute
import com.niyaj.ui.event.ShareViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens.MARKET_LIST_SCREEN
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.niyaj.ui.utils.rememberCaptureController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Composable
@Destination(route = MARKET_LIST_SCREEN)
fun MarketListScreen(
    navigator: DestinationsNavigator,
    viewModel: MarketListViewModel = hiltViewModel(),
    shareViewModel: ShareViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditMarketListScreenDestination, String>,
) {
    val scope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val snackbarState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val captureController = rememberCaptureController()

    val state = viewModel.items.collectAsStateWithLifecycle().value
    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    val showDialog = shareViewModel.showDialog.collectAsStateWithLifecycle().value

    val searchText = viewModel.searchText.value
    val selectedItems = viewModel.selectedItems.toList()
    val showFab = viewModel.totalItems.isNotEmpty()

    val openDialog = remember { mutableStateOf(false) }

    val items = viewModel.shareableItems.collectAsStateWithLifecycle().value
    val marketDate = remember {
        mutableLongStateOf(0)
    }

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
                is UiEvent.OnError -> {
                    scope.launch {
                        snackbarState.showSnackbar(data.errorMessage)
                    }
                }

                is UiEvent.OnSuccess -> {
                    scope.launch {
                        snackbarState.showSnackbar(data.successMessage)
                    }
                }
            }
        }
    }

    resultRecipient.onNavResult {
        when (it) {
            is NavResult.Canceled -> {
                viewModel.deselectItems()
            }

            is NavResult.Value -> {
                viewModel.deselectItems()

                scope.launch {
                    snackbarState.showSnackbar(it.value)
                }
            }
        }
    }

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else if (showSearchBar) {
            viewModel.closeSearchBar()
        } else {
            navigator.navigateUp()
        }
    }

    TrackScreenViewEvent(screenName = MARKET_LIST_SCREEN)

    StandardScaffoldRoute(
        currentRoute = MARKET_LIST_SCREEN,
        title = if (selectedItems.isEmpty()) MARKET_LIST_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = lazyGridState.isScrolled,
                fabText = MarketListTestTags.CREATE_NEW_LIST,
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navigator.navigate(AddEditMarketListScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
                        lazyGridState.animateScrollToItem(0)
                    }
                },
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = MARKET_ITEM_SEARCH_PLACEHOLDER,
                showSettingsIcon = false,
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navigator.navigate(AddEditMarketListScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    openDialog.value = true
                },
                onSettingsClick = {
//                    navigator.navigate(AddEditMarketListScreenDestination())
                },
                onSelectAllClick = viewModel::selectAllItems,
                onClearClick = viewModel::clearSearchText,
                onSearchClick = viewModel::openSearchBar,
                onSearchTextChanged = viewModel::searchTextChanged,
                content = {
                    IconButton(
                        onClick = {
                            navigator.navigate(MarketItemScreenDestination)
                        },
                    ) {
                        Icon(
                            imageVector = PoposIcons.Kitchen,
                            contentDescription = "Market Items",
                        )
                    }
                },
            )
        },
        fabPosition = if (lazyGridState.isScrolled) FabPosition.End else FabPosition.Center,
        selectionCount = selectedItems.size,
        showBackButton = showSearchBar,
        onDeselect = viewModel::deselectItems,
        onBackClick = viewModel::closeSearchBar,
        snackbarHostState = snackbarState,
        onNavigateToScreen = navigator::navigate,
    ) {
        Crossfade(
            targetState = state,
            label = "Item State",
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) MarketListTestTags.MARKET_LIST_NOT_AVAILABLE else Constants.SEARCH_ITEM_NOT_FOUND,
                        buttonText = MarketListTestTags.CREATE_NEW_LIST,
                        onClick = {
                            navigator.navigate(AddEditMarketListScreenDestination())
                        },
                    )
                }

                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(SpaceSmall),
                    ) {
                        items(
                            items = state.data,
                            key = {
                                it.marketList.marketId
                            },
                        ) { items ->
                            MarketListItemCard(
                                items = items,
                                doesSelected = viewModel::doesSelected,
                                doesExpanded = viewModel::doesExpanded,
                                onClick = {
                                    if (selectedItems.isEmpty()) {
                                        viewModel.onClickExpand(it)
                                    } else {
                                        viewModel.selectItem(it)
                                    }
                                },
                                onLongClick = viewModel::selectItem,
                                onClickShare = {
                                    scope.launch {
                                        async {
                                            viewModel.getListItems(it)
                                        }.await()

                                        marketDate.longValue = items.marketList.marketDate

                                        shareViewModel.onShowDialog()
                                    }
                                },
                                onClickPrint = {
                                    viewModel.printMarketList(it, items.marketList.marketDate)
                                },
                                onClickViewDetails = {
                                    navigator.navigate(
                                        MarketListItemsScreenDestination(
                                            it.toIntArray(),
                                        ),
                                    )
                                },
                                onClickManageList = { listTypeId ->
                                    navigator.navigate(
                                        MarketListItemScreenDestination(listTypeId),
                                    )
                                },
                            )

                            Spacer(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }


    AnimatedVisibility(
        visible = showDialog,
    ) {
        if (items.isNotEmpty()) {
            ShareableMarketList(
                captureController = captureController,
                marketDate = marketDate.longValue,
                onDismiss = shareViewModel::onDismissDialog,
                marketLists = items,
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


    AnimatedVisibility(
        visible = openDialog.value,
    ) {
        StandardDialog(
            title = MarketListTestTags.DELETE_LIST_TITLE,
            message = MarketListTestTags.DELETE_LIST_MESSAGE,
            onConfirm = {
                openDialog.value = false
                viewModel.deleteItems()
            },
            onDismiss = {
                openDialog.value = false
                viewModel.deselectItems()
            },
        )
    }
}