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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.cartorder.destinations.AddEditCartOrderScreenDestination
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_ITEM_TAG
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_NOTE
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_NOT_AVAILABLE
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_SCREEN_TITLE
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.CartOrderTestTags.CREATE_NEW_CART_ORDER
import com.niyaj.common.tags.CartOrderTestTags.DELETE_CART_ORDER_ITEM_MESSAGE
import com.niyaj.common.tags.CartOrderTestTags.DELETE_CART_ORDER_ITEM_TITLE
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.CartOrder
import com.niyaj.model.OrderStatus
import com.niyaj.model.OrderType
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.PoposPrimaryScaffold
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardChip
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.TextWithCount
import com.niyaj.ui.components.stickyHeader
import com.niyaj.ui.event.UiState
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
    val state = viewModel.cartOrders.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()

    val lazyGridState = rememberLazyGridState()

    val showFab = viewModel.totalItems.isNotEmpty()

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val selectedOrder = viewModel.selectedId.collectAsStateWithLifecycle().value

    val openDialog = remember { mutableStateOf(false) }

    var showMenu by remember { mutableStateOf(false) }

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

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else if (showSearchBar) {
            viewModel.closeSearchBar()
        } else {
            navigator.popBackStack()
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

    TrackScreenViewEvent(screenName = Screens.CART_ORDER_SCREEN)

    PoposPrimaryScaffold(
        currentRoute = Screens.CART_ORDER_SCREEN,
        title = if (selectedItems.isEmpty()) CART_ORDER_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navigator.navigate(AddEditCartOrderScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
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
                    navigator.navigate(AddEditCartOrderScreenDestination(selectedItems.first()))
                },
                onToggleMenu = { showMenu = !showMenu },
                onDismissDropdown = { showMenu = false },
                onDropItemClick = {
                    showMenu = false
                    viewModel.onClickViewAllOrder()
                },
                onSearchTextChanged = viewModel::searchTextChanged,
                onClearClick = viewModel::clearSearchText,
                onSearchClick = viewModel::openSearchBar,
                onSelectAllClick = viewModel::selectAllItems,
                onSelectOrderClick = viewModel::selectCartOrder,
                onSettingsClick = { /*TODO*/ },
                onClickViewDetails = {
                    onClickOrderDetails(selectedItems.first())
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
        when (state) {
            is UiState.Loading -> LoadingIndicator()

            is UiState.Empty -> {
                ItemNotAvailable(
                    text = if (searchText.isEmpty()) CART_ORDER_NOT_AVAILABLE else CART_ORDER_SEARCH_PLACEHOLDER,
                    buttonText = CREATE_NEW_CART_ORDER,
                    onClick = {
                        navigator.navigate(AddEditCartOrderScreenDestination())
                    },
                )
            }

            is UiState.Success -> {
                TrackScrollJank(
                    scrollableState = lazyGridState,
                    stateName = "All Cart Orders::List",
                )

                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(SpaceSmall),
                    columns = GridCells.Fixed(2),
                    state = lazyGridState,
                    horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
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
                                        viewModel.selectItem(it)
                                    } else {
                                        onClickOrderDetails(it)
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

    if (openDialog.value) {
        StandardDialog(
            title = DELETE_CART_ORDER_ITEM_TITLE,
            message = DELETE_CART_ORDER_ITEM_MESSAGE,
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

/**
 * [ScaffoldNavActions] for [CartOrderScreen]
 */
@Composable
fun CartOrderScaffoldNavActions(
    selectionCount: Int,
    showSearchIcon: Boolean,
    showSearchBar: Boolean,
    searchText: String,
    showMenu: Boolean,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onToggleMenu: () -> Unit,
    onDismissDropdown: () -> Unit,
    onDropItemClick: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSelectOrderClick: () -> Unit,
    onClickViewDetails: () -> Unit,
    onSelectAllClick: () -> Unit,
) = trace("CartOrderScaffoldNavActions") {
    ScaffoldNavActions(
        selectionCount = selectionCount,
        showSearchIcon = showSearchIcon,
        showBottomBarActions = false,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        onSelectAllClick = onSelectAllClick,
        showSearchBar = showSearchBar,
        searchText = searchText,
        onSearchTextChanged = onSearchTextChanged,
        onClearClick = onClearClick,
        onSearchClick = onSearchClick,
        showSettings = true,
        onSettingsClick = onSettingsClick,
        content = {
            Box {
                IconButton(
                    onClick = onToggleMenu,
                ) {
                    Icon(
                        imageVector = PoposIcons.MoreVert,
                        contentDescription = "View More Settings",
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = onDismissDropdown,
                ) {
                    DropdownMenuItem(
                        onClick = onDropItemClick,
                        text = {
                            Text(
                                text = "View All",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = PoposIcons.Visibility,
                                contentDescription = "View All",
                            )
                        },
                    )
                }
            }
        },
        preActionContent = {
            AnimatedVisibility(
                visible = selectionCount == 1,
            ) {
                IconButton(
                    onClick = onSelectOrderClick,
                ) {
                    Icon(
                        imageVector = PoposIcons.TaskAlt,
                        contentDescription = "Select Order",
                    )
                }
            }
        },
        postActionContent = {
            AnimatedVisibility(
                visible = selectionCount == 1,
            ) {
                IconButton(
                    onClick = onClickViewDetails,
                ) {
                    Icon(
                        imageVector = PoposIcons.OpenInNew,
                        contentDescription = "View Details",
                    )
                }
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CartOrderData(
    modifier: Modifier = Modifier,
    item: CartOrder,
    doesSelected: (Int) -> Boolean,
    orderSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
) = trace("CartOrderData") {
    val borderStroke = if (doesSelected(item.orderId)) border else null

    ElevatedCard(
        modifier = modifier
            .testTag(CART_ORDER_ITEM_TAG.plus(item.orderId))
            .padding(vertical = SpaceSmall)
            .then(
                borderStroke?.let {
                    Modifier.border(it, CardDefaults.elevatedShape)
                } ?: Modifier,
            )
            .clip(CardDefaults.elevatedShape)
            .combinedClickable(
                onClick = {
                    onClick(item.orderId)
                },
                onLongClick = {
                    onLongClick(item.orderId)
                },
            ),
        colors = CardDefaults.elevatedCardColors(),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularBox(
                modifier = Modifier.padding(SpaceSmall),
                icon = PoposIcons.Tag,
                doesSelected = doesSelected(item.orderId),
                showBorder = orderSelected(item.orderId),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        buildAnnotatedString {
                            if (item.orderType == OrderType.DineOut) {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.Red,
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                ) {
                                    append(item.address.shortName.uppercase())

                                    append(" - ")
                                }
                            }

                            append(item.orderId.toString())
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Text(
                        text = item.orderType.name,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }

                if (item.orderStatus == OrderStatus.PLACED) {
                    StandardChip(
                        text = item.orderStatus.name,
                        isClickable = false,
                    )
                }
            }
        }
    }
}
