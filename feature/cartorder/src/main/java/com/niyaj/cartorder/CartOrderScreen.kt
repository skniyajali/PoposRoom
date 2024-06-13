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

package com.niyaj.cartorder

import androidx.activity.compose.BackHandler
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.cartorder.components.CartOrderData
import com.niyaj.cartorder.components.CartOrderScaffoldNavActions
import com.niyaj.cartorder.destinations.AddEditCartOrderScreenDestination
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_NOTE
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_NOT_AVAILABLE
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_SCREEN_TITLE
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.CartOrderTestTags.CREATE_NEW_CART_ORDER
import com.niyaj.common.tags.CartOrderTestTags.DELETE_CART_ORDER_ITEM_MESSAGE
import com.niyaj.common.tags.CartOrderTestTags.DELETE_CART_ORDER_ITEM_TITLE
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.CartOrder
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.PoposPrimaryScaffold
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.TextWithCount
import com.niyaj.ui.components.stickyHeader
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.CartOrderStatePreviewParameter
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination(route = Screens.CART_ORDER_SCREEN)
@Composable
fun CartOrderScreen(
    navigator: DestinationsNavigator,
    onClickOrderDetails: (Int) -> Unit,
    resultRecipient: ResultRecipient<AddEditCartOrderScreenDestination, String>,
    viewModel: CartOrderViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }

    val state by viewModel.cartOrders.collectAsStateWithLifecycle()
    val selectedOrder by viewModel.selectedId.collectAsStateWithLifecycle()
    val showSearchBar by viewModel.showSearchBar.collectAsStateWithLifecycle()

    val selectedItems = viewModel.selectedItems.toList()
    val searchText = viewModel.searchText.value

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.OnError -> {
                    scope.launch {
                        snackbarState.showSnackbar(event.errorMessage)
                    }

                    viewModel.deselectItems()
                }

                is UiEvent.OnSuccess -> {
                    scope.launch {
                        snackbarState.showSnackbar(event.successMessage)
                    }

                    viewModel.deselectItems()
                }
            }
        }
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                viewModel.deselectItems()
            }

            is NavResult.Value -> {
                scope.launch {
                    viewModel.deselectItems()
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    CartOrderScreenContent(
        modifier = Modifier,
        uiState = state,
        selectedItems = selectedItems,
        selectedOrder = selectedOrder,
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
        onClickViewAll = viewModel::onClickViewAllOrder,
        onClickSelectOrder = viewModel::selectCartOrder,
        onClickBack = navigator::popBackStack,
        onNavigateToScreen = navigator::navigate,
        onClickCreateNew = {
            navigator.navigate(AddEditCartOrderScreenDestination())
        },
        onClickEdit = {
            navigator.navigate(AddEditCartOrderScreenDestination(it))
        },
        onClickSettings = {},
        onNavigateToDetails = onClickOrderDetails,
        snackbarHostState = snackbarState,
    )
}

@VisibleForTesting
@Composable
internal fun CartOrderScreenContent(
    modifier: Modifier = Modifier,
    uiState: UiState<Map<String, List<CartOrder>>>,
    selectedItems: List<Int>,
    selectedOrder: Int,
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
    onClickViewAll: () -> Unit,
    onClickSelectOrder: (Int) -> Unit,
    onClickBack: () -> Unit,
    onNavigateToScreen: (String) -> Unit,
    onClickCreateNew: () -> Unit,
    onClickEdit: (Int) -> Unit,
    onClickSettings: () -> Unit,
    onNavigateToDetails: (Int) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {
    BackHandler {
        if (selectedItems.isNotEmpty()) {
            onClickDeselect()
        } else if (showSearchBar) {
            onCloseSearchBar()
        } else {
            onClickBack()
        }
    }

    TrackScreenViewEvent(screenName = Screens.CART_ORDER_SCREEN)

    val showFab = uiState is UiState.Success
    val title = if (selectedItems.isEmpty()) CART_ORDER_SCREEN_TITLE else "${selectedItems.size} Selected"

    val openDialog = remember { mutableStateOf(false) }
    var showMenu by rememberSaveable { mutableStateOf(false) }

    PoposPrimaryScaffold(
        modifier = modifier,
        currentRoute = Screens.CART_ORDER_SCREEN,
        title = title,
        floatingActionButton = {
            StandardFAB(
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = onClickCreateNew,
                onClickScroll = {
                    coroutineScope.launch {
                        lazyGridState.animateScrollToItem(0)
                    }
                },
                showScrollToTop = lazyGridState.isScrolled,
                fabText = CREATE_NEW_CART_ORDER,
            )
        },
        navActions = {
            CartOrderScaffoldNavActions(
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                showMenu = showMenu,
                onDeleteClick = {
                    openDialog.value = true
                },
                onEditClick = {
                    onClickEdit(selectedItems.first())
                },
                onToggleMenu = { showMenu = !showMenu },
                onDismissDropdown = { showMenu = false },
                onClickViewAll = {
                    showMenu = false
                    onClickViewAll()
                },
                onSearchTextChanged = onSearchTextChanged,
                onClearClick = onClickClear,
                onSearchClick = onClickSearchIcon,
                onSelectAllClick = onClickSelectAll,
                onSelectOrderClick = {
                    onClickSelectOrder(selectedItems.first())
                },
                onSettingsClick = onClickSettings,
                onClickViewDetails = {
                    onNavigateToDetails(selectedItems.first())
                },
            )
        },
        fabPosition = if (lazyGridState.isScrolled) FabPosition.End else FabPosition.Center,
        selectionCount = selectedItems.size,
        showBackButton = showSearchBar,
        onDeselect = onClickDeselect,
        onBackClick = if (showSearchBar) onCloseSearchBar else onClickBack,
        snackbarHostState = snackbarHostState,
        onNavigateToScreen = onNavigateToScreen,
    ) {
        Crossfade(
            targetState = uiState,
            label = "CartOrder::State",
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) CART_ORDER_NOT_AVAILABLE else CART_ORDER_SEARCH_PLACEHOLDER,
                        buttonText = CREATE_NEW_CART_ORDER,
                        onClick = onClickCreateNew,
                    )
                }

                is UiState.Success -> {
                    TrackScrollJank(
                        scrollableState = lazyGridState,
                        stateName = "All Cart Orders::List",
                    )

                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize(),
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(SpaceSmall),
                        state = lazyGridState,
                        horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
                        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                    ) {
                        item("Note", span = { GridItemSpan(2) }) {
                            ListItem(
                                modifier = Modifier
                                    .height(48.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(SpaceMini)),
                                headlineContent = {
                                    Text(
                                        text = CART_ORDER_NOTE,
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                },
                                leadingContent = {
                                    Icon(
                                        imageVector = PoposIcons.Info,
                                        contentDescription = "info",
                                    )
                                },
                                colors = ListItemDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                ),
                            )

                            Spacer(modifier = Modifier.height(SpaceMedium))
                        }

                        state.data.forEach { (date, orders) ->
                            stickyHeader {
                                TextWithCount(
                                    modifier = Modifier
                                        .background(
                                            if (lazyGridState.isScrollingUp()) {
                                                MaterialTheme.colorScheme.background
                                            } else {
                                                Color.Transparent
                                            },
                                        )
                                        .clip(
                                            RoundedCornerShape(if (lazyGridState.isScrollingUp()) 4.dp else 0.dp),
                                        ),
                                    text = date,
                                    count = orders.count(),
                                    leadingIcon = PoposIcons.CalenderMonth,
                                    onClick = {},
                                )
                            }

                            items(
                                items = orders,
                                key = {
                                    it.orderId
                                },
                            ) { cartOrder ->
                                CartOrderData(
                                    item = cartOrder,
                                    orderSelected = {
                                        selectedOrder == it
                                    },
                                    doesSelected = {
                                        selectedItems.contains(it)
                                    },
                                    onClick = {
                                        if (selectedItems.isNotEmpty()) {
                                            onClickSelectItem(it)
                                        } else {
                                            onNavigateToDetails(it)
                                        }
                                    },
                                    onLongClick = onClickSelectItem,
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
            title = DELETE_CART_ORDER_ITEM_TITLE,
            message = DELETE_CART_ORDER_ITEM_MESSAGE,
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

@DevicePreviews
@Composable
private fun CartOrderScreenContentPreview(
    @PreviewParameter(CartOrderStatePreviewParameter::class)
    uiState: UiState<Map<String, List<CartOrder>>>,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        CartOrderScreenContent(
            modifier = Modifier,
            uiState = uiState,
            selectedItems = listOf(),
            selectedOrder = 0,
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
            onClickViewAll = {},
            onClickSelectOrder = {},
            onClickBack = {},
            onNavigateToScreen = {},
            onClickCreateNew = {},
            onClickEdit = {},
            onClickSettings = {},
            onNavigateToDetails = {},
        )
    }
}
