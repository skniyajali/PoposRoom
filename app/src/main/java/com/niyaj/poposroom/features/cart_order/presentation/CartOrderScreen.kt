package com.niyaj.poposroom.features.cart_order.presentation

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
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FabPosition
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
import com.niyaj.poposroom.features.cart_order.domain.model.CartOrder
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderTestTags.CART_ORDER_ITEM_TAG
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderTestTags.CART_ORDER_NOT_AVAIlABLE
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderTestTags.CART_ORDER_SCREEN_TITLE
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderTestTags.CART_ORDER_SEARCH_PLACEHOLDER
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderTestTags.CREATE_NEW_CART_ORDER
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderTestTags.DELETE_CART_ORDER_ITEM_MESSAGE
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderTestTags.DELETE_CART_ORDER_ITEM_TITLE
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderType
import com.niyaj.poposroom.features.common.components.CircularBox
import com.niyaj.poposroom.features.common.components.ItemNotAvailable
import com.niyaj.poposroom.features.common.components.LoadingIndicator
import com.niyaj.poposroom.features.common.components.ScaffoldNavActions
import com.niyaj.poposroom.features.common.components.StandardFAB
import com.niyaj.poposroom.features.common.components.StandardScaffold
import com.niyaj.poposroom.features.common.event.UiState
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.common.utils.isScrolled
import com.niyaj.poposroom.features.destinations.AddEditCartOrderScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Destination
@Composable
fun CartOrderScreen(
    navController: NavController,
    viewModel: CartOrderViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditCartOrderScreenDestination, String>
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
            when(event) {
                is UiEvent.IsLoading -> {}
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
        when(result) {
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
        snackbarHostState = snackbarState,
        title = if (selectedItems.isEmpty()) CART_ORDER_SCREEN_TITLE else "${selectedItems.size} Selected",
        selectionCount = selectedItems.size,
        showBackButton = showSearchBar,
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
        fabPosition = if (lazyGridState.isScrolled) FabPosition.End else FabPosition.Center,
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
                onDismissDropdown = { showMenu = false  },
                onDropItemClick = {
                    showMenu = false
                },
                onSearchTextChanged = viewModel::searchTextChanged,
                onClearClick = viewModel::clearSearchText,
                onSearchClick = viewModel::openSearchBar,
                onSelectAllClick = viewModel::selectAllItems,
                onSelectOrderClick = viewModel::selectCartOrder,
                onSettingsClick = { /*TODO*/ },
                onClickViewDetails = { /*TODO*/ }
            )
        },
        onEditClick = {
            navController.navigate(AddEditCartOrderScreenDestination(selectedItems.first()))
        },
        onDeleteClick = {
            openDialog.value = true
        },
        onDeselect = viewModel::deselectItems,
        onSelectAllClick = viewModel::selectAllItems,
        onBackClick = viewModel::closeSearchBar,
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
                            it.cartOrderId
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
    showSearchBar : Boolean,
    searchText : String,
    showMenu: Boolean,
    onDeleteClick : () -> Unit,
    onEditClick : () -> Unit,
    onToggleMenu: () -> Unit,
    onDismissDropdown: () -> Unit,
    onDropItemClick : () -> Unit,
    onSearchTextChanged : (String) -> Unit,
    onClearClick : () -> Unit,
    onSearchClick : () -> Unit,
    onSettingsClick : () -> Unit,
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
                        onClick = onDropItemClick
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "View All",
                            )
                            Spacer(modifier = Modifier.width(SpaceSmall))
                            Text(
                                text = "View All",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
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
                        imageVector = Icons.Default.OpenInNew,
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
    val borderStroke = if (doesSelected(item.cartOrderId)) border else null

    ElevatedCard(
        modifier = modifier
            .testTag(CART_ORDER_ITEM_TAG.plus(item.cartOrderId))
            .padding(SpaceSmall)
            .then(borderStroke?.let {
                Modifier.border(it, CardDefaults.elevatedShape)
            } ?: Modifier)
            .clip(CardDefaults.elevatedShape)
            .combinedClickable(
                onClick = {
                    onClick(item.cartOrderId)
                },
                onLongClick = {
                    onLongClick(item.cartOrderId)
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
                doesSelected = doesSelected(item.cartOrderId),
                showBorder = orderSelected(item.cartOrderId)
            )

            Spacer(modifier = Modifier.width(SpaceSmall))

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    buildAnnotatedString {
                        if (item.orderType == CartOrderType.DineOut) {
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

                        append(item.cartOrderId.toString())
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