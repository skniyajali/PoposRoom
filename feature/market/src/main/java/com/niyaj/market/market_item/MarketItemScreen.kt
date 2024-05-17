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

package com.niyaj.market.market_item

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.MarketListTestTags.CREATE_NEW_ITEM
import com.niyaj.common.tags.MarketListTestTags.DELETE_ITEM_MESSAGE
import com.niyaj.common.tags.MarketListTestTags.DELETE_ITEM_TITLE
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NOT_AVAILABLE
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_SCREEN_TITLE
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_SEARCH_PLACEHOLDER
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.market.components.MarketItemCard
import com.niyaj.market.destinations.AddEditMarketItemScreenDestination
import com.niyaj.market.destinations.MarketItemSettingsScreenDestination
import com.niyaj.market.destinations.MarketListScreenDestination
import com.niyaj.market.destinations.MeasureUnitScreenDestination
import com.niyaj.model.MarketItem
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardScaffoldRoute
import com.niyaj.ui.components.TextWithCount
import com.niyaj.ui.components.stickyHeader
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens.MARKET_ITEM_SCREEN
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination(route = MARKET_ITEM_SCREEN)
@Composable
fun MarketItemScreen(
    navigator: DestinationsNavigator,
    viewModel: MarketItemViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditMarketItemScreenDestination, String>,
) {
    val lazyGridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }

    val state = viewModel.marketItems.collectAsStateWithLifecycle().value
    val selectedItems = viewModel.selectedItems.toList()
    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value
    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value
    val showFab = viewModel.totalItems.isNotEmpty()

    val openDialog = remember { mutableStateOf(false) }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                viewModel.deselectItems()
            }

            is NavResult.Value -> {
                viewModel.deselectItems()
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
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

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else if (showSearchBar) {
            viewModel.closeSearchBar()
        } else {
            navigator.navigateUp()
        }
    }

    TrackScreenViewEvent(screenName = MARKET_ITEM_SCREEN)

    StandardScaffoldRoute(
        currentRoute = MARKET_ITEM_SCREEN,
        title = if (selectedItems.isEmpty()) MARKET_ITEM_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = lazyGridState.isScrolled,
                fabText = CREATE_NEW_ITEM,
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navigator.navigate(AddEditMarketItemScreenDestination())
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
                showSettingsIcon = true,
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navigator.navigate(AddEditMarketItemScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    openDialog.value = true
                },
                onSettingsClick = {
                    navigator.navigate(MarketItemSettingsScreenDestination)
                },
                onSelectAllClick = viewModel::selectAllItems,
                onClearClick = viewModel::clearSearchText,
                onSearchClick = viewModel::openSearchBar,
                onSearchTextChanged = viewModel::searchTextChanged,
                content = {
                    IconButton(
                        onClick = {
                            navigator.navigate(MeasureUnitScreenDestination)
                        },
                    ) {
                        Icon(
                            imageVector = PoposIcons.MonitorWeight,
                            contentDescription = "Go to Measure Unit Screen",
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
    ) { _ ->
        Crossfade(
            targetState = state,
            label = "Item State",
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) MARKET_ITEM_NOT_AVAILABLE else Constants.SEARCH_ITEM_NOT_FOUND,
                        buttonText = CREATE_NEW_ITEM,
                        onClick = {
                            navigator.navigate(AddEditMarketItemScreenDestination())
                        },
                    )
                }

                is UiState.Success -> {
                    TrackScrollJank(
                        scrollableState = lazyGridState,
                        stateName = "MarketItem::State",
                    )

                    val groupedData = remember(state.data) {
                        state.data.groupBy { it.itemType }
                    }

                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(SpaceSmall),
                        columns = GridCells.Fixed(2),
                        state = lazyGridState,
                    ) {
                        item(span = { GridItemSpan(2) }) {
                            SettingsCard(
                                modifier = Modifier.padding(SpaceSmall),
                                title = "Create New List",
                                subtitle = "",
                                icon = PoposIcons.PostAdd,
                                onClick = {
                                    navigator.navigate(MarketListScreenDestination)
                                },
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                leadingColor = MaterialTheme.colorScheme.background,
                            )
                        }

                        groupedData.forEach { (type, items) ->
                            stickyHeader {
                                TextWithCount(
                                    modifier = Modifier
                                        .padding(horizontal = SpaceSmall)
                                        .background(
                                            MaterialTheme.colorScheme.tertiaryContainer,
                                            RoundedCornerShape(SpaceMini)
                                        ),
                                    text = type.typeName,
                                    count = items.size,
                                    leadingIcon = PoposIcons.Category,
                                )
                            }

                            items(
                                items = items,
                                key = { it.itemId },
                            ) { item: MarketItem ->
                                MarketItemCard(
                                    item = item,
                                    doesSelected = selectedItems::contains,
                                    onClick = {
                                        if (selectedItems.isNotEmpty()) {
                                            viewModel.selectItem(it)
                                        }
                                    },
                                    onLongClick = viewModel::selectItem,
                                )
                            }
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
            title = DELETE_ITEM_TITLE,
            message = DELETE_ITEM_MESSAGE,
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
