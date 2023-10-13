package com.niyaj.cartorder

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.cartorder.destinations.AddEditCartOrderScreenDestination
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_ITEM_TAG
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_NOT_AVAIlABLE
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_SCREEN_TITLE
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.CartOrderTestTags.CREATE_NEW_CART_ORDER
import com.niyaj.common.tags.CartOrderTestTags.DELETE_CART_ORDER_ITEM_MESSAGE
import com.niyaj.common.tags.CartOrderTestTags.DELETE_CART_ORDER_ITEM_TITLE
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.CartOrder
import com.niyaj.model.OrderType
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination(
    route = Screens.CART_ORDER_SCREEN
)
@Composable
fun CartOrderScreen(
    navController: NavController,
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

    StandardScaffold(
        navController = navController,
        title = if (selectedItems.isEmpty()) CART_ORDER_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = lazyGridState.isScrolled,
                fabText = CREATE_NEW_CART_ORDER,
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navController.navigate(AddEditCartOrderScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
                        lazyGridState.animateScrollToItem(0)
                    }
                }
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
                    navController.navigate(AddEditCartOrderScreenDestination(selectedItems.first()))
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
                }
            )
        },
        fabPosition = if (lazyGridState.isScrolled) FabPosition.End else FabPosition.Center,
        selectionCount = selectedItems.size,
        showBackButton = showSearchBar,
        onDeselect = viewModel::deselectItems,
        onBackClick = viewModel::closeSearchBar,
        snackbarHostState = snackbarState,
    ) { _ ->
        when (state) {
            is UiState.Loading -> LoadingIndicator()
            is UiState.Empty -> {
                ItemNotAvailable(
                    text = if (searchText.isEmpty()) CART_ORDER_NOT_AVAIlABLE else CART_ORDER_SEARCH_PLACEHOLDER,
                    buttonText = CREATE_NEW_CART_ORDER,
                    onClick = {
                        navController.navigate(AddEditCartOrderScreenDestination())
                    }
                )
            }

            is UiState.Success -> {
                LazyVerticalGrid(
                    modifier = Modifier
                        .padding(SpaceSmall),
                    columns = GridCells.Fixed(2),
                    state = lazyGridState,
                ) {
                    items(
                        items = state.data,
                        key = {
                            it.orderId
                        }
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
                                }else {
                                    onClickOrderDetails(it)
                                }
                            },
                            onLongClick = viewModel::selectItem
                        )
                    }
                }
            }
        }
    }


    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
                viewModel.deselectItems()
            },
            title = {
                Text(text = DELETE_CART_ORDER_ITEM_TITLE)
            },
            text = {
                Text(
                    text = DELETE_CART_ORDER_ITEM_MESSAGE
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        viewModel.deleteItems()
                    },
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        viewModel.deselectItems()
                    },
                ) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(28.dp)
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
) {
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
                        imageVector = Icons.Default.MoreVert,
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
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "View All",
                            )
                        }
                    )
                }
            }
        },
        preActionContent = {
            AnimatedVisibility(
                visible = selectionCount == 1
            ) {
                IconButton(
                    onClick = onSelectOrderClick
                ) {
                    Icon(
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = "Select Order",
                    )
                }
            }
        },
        postActionContent = {
            AnimatedVisibility(
                visible = selectionCount == 1
            ) {
                IconButton(
                    onClick = onClickViewDetails
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "View Details",
                    )
                }
            }
        }
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
) {
    val borderStroke = if (doesSelected(item.orderId)) border else null

    ElevatedCard(
        modifier = modifier
            .testTag(CART_ORDER_ITEM_TAG.plus(item.orderId))
            .padding(SpaceSmall)
            .then(borderStroke?.let {
                Modifier.border(it, CardDefaults.elevatedShape)
            } ?: Modifier)
            .clip(CardDefaults.elevatedShape)
            .combinedClickable(
                onClick = {
                    onClick(item.orderId)
                },
                onLongClick = {
                    onLongClick(item.orderId)
                },
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularBox(
                icon = Icons.Default.Tag,
                doesSelected = doesSelected(item.orderId),
                showBorder = orderSelected(item.orderId)
            )

            Spacer(modifier = Modifier.width(SpaceSmall))

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    buildAnnotatedString {
                        if (item.orderType == OrderType.DineOut) {
                            withStyle(
                                style = SpanStyle(
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(item.address.shortName.uppercase())

                                append(" - ")
                            }
                        }

                        append(item.orderId.toString())
                    },
                    style = MaterialTheme.typography.labelMedium
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                Text(
                    text = item.orderType.name,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}