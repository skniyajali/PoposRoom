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

package com.niyaj.market.marketType

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import com.niyaj.common.tags.MarketTypeTags
import com.niyaj.common.tags.MarketTypeTags.CREATE_NEW_TYPE
import com.niyaj.common.tags.MarketTypeTags.MARKET_TYPE_NOT_AVAILABLE
import com.niyaj.common.tags.MarketTypeTags.MARKET_TYPE_SCREEN_TITLE
import com.niyaj.common.tags.MarketTypeTags.MARKET_TYPE_SEARCH_PLACEHOLDER
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.market.components.MarketTypeCard
import com.niyaj.market.destinations.AddEditMarketItemScreenDestination
import com.niyaj.market.destinations.AddEditMarketTypeScreenDestination
import com.niyaj.market.destinations.ExportMarketTypeScreenDestination
import com.niyaj.market.destinations.ImportMarketTypeScreenDestination
import com.niyaj.market.destinations.MarketTypeSettingsScreenDestination
import com.niyaj.market.destinations.MeasureUnitScreenDestination
import com.niyaj.model.MarketType
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.PoposPrimaryScaffold
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens.MARKET_TYPE_SCREEN
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@Destination(route = MARKET_TYPE_SCREEN)
@Composable
fun MarketTypeScreen(
    navigator: DestinationsNavigator,
    viewModel: MarketTypeViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditMarketTypeScreenDestination, String>,
    exportRecipient: ResultRecipient<ExportMarketTypeScreenDestination, String>,
    importRecipient: ResultRecipient<ImportMarketTypeScreenDestination, String>,
) {
    val lazyGridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }

    val state = viewModel.marketTypes.collectAsStateWithLifecycle().value
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

    exportRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    importRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
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
            navigator.popBackStack()
        }
    }

    TrackScreenViewEvent(screenName = MARKET_TYPE_SCREEN)

    PoposPrimaryScaffold(
        currentRoute = MARKET_TYPE_SCREEN,
        title = if (selectedItems.isEmpty()) MARKET_TYPE_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navigator.navigate(AddEditMarketTypeScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
                        lazyGridState.animateScrollToItem(0)
                    }
                },
                showScrollToTop = lazyGridState.isScrolled,
                fabText = CREATE_NEW_TYPE,
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = MARKET_TYPE_SEARCH_PLACEHOLDER,
                showSettingsIcon = true,
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navigator.navigate(AddEditMarketTypeScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    openDialog.value = true
                },
                onSettingsClick = {
                    navigator.navigate(MarketTypeSettingsScreenDestination)
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
                        text = if (searchText.isEmpty()) MARKET_TYPE_NOT_AVAILABLE else Constants.SEARCH_ITEM_NOT_FOUND,
                        buttonText = CREATE_NEW_TYPE,
                        onClick = {
                            navigator.navigate(AddEditMarketTypeScreenDestination())
                        },
                    )
                }

                is UiState.Success -> {
                    TrackScrollJank(
                        scrollableState = lazyGridState,
                        stateName = "MarketTypes::State",
                    )

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
                                title = "Create New Item",
                                subtitle = "",
                                icon = PoposIcons.Dns,
                                onClick = {
                                    navigator.navigate(AddEditMarketItemScreenDestination())
                                },
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                leadingColor = MaterialTheme.colorScheme.background,
                            )
                        }

                        items(
                            items = state.data,
                            key = { it.typeId },
                        ) { item: MarketType ->
                            MarketTypeCard(
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

    AnimatedVisibility(
        visible = openDialog.value,
    ) {
        StandardDialog(
            title = MarketTypeTags.DELETE_ITEM_TITLE,
            message = MarketTypeTags.DELETE_ITEM_MESSAGE,
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
