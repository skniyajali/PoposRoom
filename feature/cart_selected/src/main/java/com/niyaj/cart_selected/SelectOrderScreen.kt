package com.niyaj.cart_selected

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.CartOrderTestTags
import com.niyaj.common.tags.SelectedTestTag.SELECTED_SCREEN_NOTE
import com.niyaj.common.tags.SelectedTestTag.SELECTED_SCREEN_TITLE
import com.niyaj.common.utils.toTimeSpan
import com.niyaj.designsystem.components.PoposTextButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.CartOrder
import com.niyaj.model.OrderType
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardBottomSheetScaffold
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import kotlinx.coroutines.flow.collectLatest

@RootNavGraph(start = true)
@Destination(
    route = Screens.SELECT_ORDER_SCREEN,
    style = DestinationStyleBottomSheet::class,
)
@Composable
fun SelectOrderScreen(
    navController: NavController,
    onEditClick: (Int) -> Unit,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: SelectedViewModel = hiltViewModel(),
) {
    val lazyListState = rememberLazyListState()

    val openDialog = remember { mutableStateOf(false) }

    val state = viewModel.cartOrders.collectAsStateWithLifecycle().value
    val selectedOrder = viewModel.selectedId.collectAsStateWithLifecycle().value

    var selectedId by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { data ->
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

    TrackScreenViewEvent(screenName = Screens.SELECT_ORDER_SCREEN)

    StandardBottomSheetScaffold(
        title = SELECTED_SCREEN_TITLE,
        onBackClick = {
            navController.navigateUp()
        },
        showBottomBar = true,
        bottomBar = {
            StandardButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                enabled = true,
                text = CartOrderTestTags.CREATE_NEW_CART_ORDER,
                icon = PoposIcons.Add,
                onClick = {
                    navController.navigate(Screens.ADD_EDIT_CART_ORDER_SCREEN)
                },
            )
        },
    ) {
        Crossfade(
            targetState = state,
            label = "Select Cart Order",
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = CartOrderTestTags.CART_ORDER_NOT_AVAILABLE,
                        buttonText = CartOrderTestTags.CREATE_NEW_CART_ORDER,
                        onClick = {
                            navController.navigate(Screens.ADD_EDIT_CART_ORDER_SCREEN)
                        },
                    )
                }

                is UiState.Success -> {
                    TrackScrollJank(
                        scrollableState = lazyListState,
                        stateName = "Cart Orders::List",
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpaceMedium),
                        state = lazyListState,
                    ) {
                        item("Note") {
                            ListItem(
                                modifier = Modifier
                                    .height(48.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(SpaceMini)),
                                headlineContent = {
                                    Text(
                                        text = SELECTED_SCREEN_NOTE,
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

                        items(
                            items = state.data,
                            key = {
                                it.orderId
                            },
                        ) { item: CartOrder ->
                            SelectedData(
                                cartOrder = item,
                                doesSelected = {
                                    selectedOrder == it
                                },
                                onSelectOrder = viewModel::selectCartOrder,
                                onDeleteClick = {
                                    selectedId = it
                                    openDialog.value = true
                                },
                                onEditClick = onEditClick,
                            )

                            Spacer(modifier = Modifier.height(SpaceMedium))
                        }
                    }
                }
            }
        }
    }


    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = CartOrderTestTags.DELETE_CART_ORDER_ITEM_TITLE)
            },
            text = {
                Text(
                    text = CartOrderTestTags.DELETE_CART_ORDER_ITEM_MESSAGE,
                )
            },
            confirmButton = {
                PoposTextButton(
                    text = "Delete",
                    onClick = {
                        openDialog.value = false
                        viewModel.deleteCartOrder(selectedId)
                    },
                )
            },
            dismissButton = {
                PoposTextButton(
                    text = "Cancel",
                    onClick = {
                        openDialog.value = false
                        selectedId = 0
                    },
                )
            },
            shape = RoundedCornerShape(28.dp),
        )
    }
}

@Composable
fun SelectedData(
    cartOrder: CartOrder,
    doesSelected: (Int) -> Boolean,
    onSelectOrder: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
    onEditClick: (Int) -> Unit,
) = trace("CartOrders") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ListItem(
            modifier = Modifier
                .fillMaxSize()
                .weight(2f)
                .clip(RoundedCornerShape(SpaceMini))
                .clickable {
                    onSelectOrder(cartOrder.orderId)
                },
            headlineContent = {
                Text(
                    buildAnnotatedString {
                        if (cartOrder.orderType == OrderType.DineOut) {
                            withStyle(
                                style = SpanStyle(
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold,
                                ),
                            ) {
                                append(cartOrder.address.shortName.uppercase())

                                append(" - ")
                            }
                        }

                        append(cartOrder.orderId.toString())
                    },
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            leadingContent = {
                CircularBox(
                    icon = PoposIcons.Tags,
                    doesSelected = doesSelected(cartOrder.orderId),
                    size = 30.dp,
                )
            },
            trailingContent = {
                Text(
                    text = cartOrder.createdAt.toTimeSpan,
                    style = MaterialTheme.typography.labelSmall,
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        )

        Spacer(modifier = Modifier.width(SpaceSmall))

        IconButton(
            onClick = {
                onEditClick(cartOrder.orderId)
            },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(SpaceMini)),
        ) {
            Icon(
                contentDescription = "Edit",
                imageVector = PoposIcons.Edit,
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }

        Spacer(modifier = Modifier.width(SpaceSmall))

        IconButton(
            onClick = {
                onDeleteClick(cartOrder.orderId)
            },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(SpaceMini)),
        ) {
            Icon(
                contentDescription = "Delete cart order",
                imageVector = PoposIcons.Delete,
                tint = MaterialTheme.colorScheme.onSecondary,
            )
        }
    }
}