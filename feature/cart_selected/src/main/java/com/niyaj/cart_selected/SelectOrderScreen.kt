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

package com.niyaj.cart_selected

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.common.tags.CartOrderTestTags
import com.niyaj.common.tags.SelectedTestTag.SELECTED_SCREEN_NOTE
import com.niyaj.common.tags.SelectedTestTag.SELECTED_SCREEN_TITLE
import com.niyaj.common.utils.findActivity
import com.niyaj.common.utils.openAppSettings
import com.niyaj.common.utils.toTimeSpan
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.AddOnItem
import com.niyaj.model.CartOrder
import com.niyaj.model.OrderType
import com.niyaj.print.OrderPrintViewModel
import com.niyaj.print.PrintEvent
import com.niyaj.ui.components.BluetoothPermissionDialog
import com.niyaj.ui.components.CartAddOnItems
import com.niyaj.ui.components.CartItemProductDetailsSection
import com.niyaj.ui.components.CartItemTotalPriceSection
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.HandleBluetoothPermissionState
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardBottomSheetScaffold
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalPermissionsApi::class)
@RootNavGraph(start = true)
@Destination(
    route = Screens.SELECT_ORDER_SCREEN,
    style = DestinationStyleBottomSheet::class,
)
@Composable
fun SelectOrderScreen(
    navigator: DestinationsNavigator,
    onEditClick: (Int) -> Unit,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: SelectedViewModel = hiltViewModel(),
    printViewModel: OrderPrintViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val lazyListState = rememberLazyListState()

    val openDialog = remember { mutableStateOf(false) }

    val state = viewModel.cartOrders.collectAsStateWithLifecycle().value
    val selectedOrder = viewModel.selectedId.collectAsStateWithLifecycle().value

    val orderDetails by viewModel.orderDetails.collectAsStateWithLifecycle()
    val addOnItems by viewModel.addOnItems.collectAsStateWithLifecycle()

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

    val context = LocalContext.current

    val printError = printViewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    LaunchedEffect(key1 = printError) {
        printError?.let {
            when (printError) {
                is UiEvent.OnError -> {
                    snackbarHostState.showSnackbar(
                        message = printError.errorMessage,
                        duration = SnackbarDuration.Short,
                    )
                }

                else -> {}
            }
        }
    }

    val bluetoothPermissions =
        // Checks if the device has Android 12 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                ),
            )
        } else {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                ),
            )
        }

    val enableBluetoothContract = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) {}

    // This intent will open the enable bluetooth dialog
    val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

    val bluetoothManager = remember {
        context.getSystemService(BluetoothManager::class.java)
    }

    val bluetoothAdapter: BluetoothAdapter? = remember {
        bluetoothManager.adapter
    }

    val printOrder: (Int) -> Unit = {
        if (bluetoothPermissions.allPermissionsGranted) {
            if (bluetoothAdapter?.isEnabled == true) {
                // Bluetooth is on print the receipt
                printViewModel.onPrintEvent(PrintEvent.PrintOrder(it))
            } else {
                // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
                printViewModel.onPrintEvent(PrintEvent.PrintOrder(it))
            }
        } else {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
    }

    TrackScreenViewEvent(screenName = Screens.SELECT_ORDER_SCREEN)

    HandleBluetoothPermissionState(
        multiplePermissionsState = bluetoothPermissions,
        onSuccessful = {
            StandardBottomSheetScaffold(
                title = SELECTED_SCREEN_TITLE,
                showBottomBar = false,
                snackbarHostState = snackbarHostState,
                onBackClick = {
                    navigator.navigate(Screens.HOME_SCREEN)
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
                                    navigator.navigate(Screens.ADD_EDIT_CART_ORDER_SCREEN)
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
                                    if (item.orderId == selectedOrder) {
                                        SelectedCartOrderData(
                                            cartOrder = item,
                                            orderDetails = orderDetails,
                                            addOnItems = addOnItems,
                                            onDeleteClick = {
                                                selectedId = it
                                                openDialog.value = true
                                            },
                                            onEditClick = onEditClick,
                                            onIncreaseQty = viewModel::increaseProductQuantity,
                                            onDecreaseQty = viewModel::decreaseProductQuantity,
                                            onUpdateAddOnItem = viewModel::updateCartAddOnItem,
                                            onPlaceOrder = viewModel::placeOrder,
                                            onPrintOrder = printOrder,
                                        )
                                    } else {
                                        CartOrderData(
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
                                    }

                                    Spacer(modifier = Modifier.height(SpaceMedium))
                                }
                            }
                        }
                    }
                }
            }
        },
        onError = { shouldShowRationale ->
            BluetoothPermissionDialog(
                onClickRequestPermission = {
                    bluetoothPermissions.launchMultiplePermissionRequest()
                },
                onDismissRequest = navigator::navigateUp,
                shouldShowRationale = shouldShowRationale,
                onClickOpenSettings = {
                    context.findActivity().openAppSettings()
                },
            )
        },
    )

    AnimatedVisibility(
        visible = openDialog.value
    ) {
        StandardDialog(
            title = CartOrderTestTags.DELETE_CART_ORDER_ITEM_TITLE,
            message = CartOrderTestTags.DELETE_CART_ORDER_ITEM_MESSAGE,
            onConfirm = {
                openDialog.value = false
                viewModel.deleteCartOrder(selectedId)
            },
            onDismiss = {
                openDialog.value = false
                selectedId = 0
            },
        )
    }
}

@Composable
fun SelectedCartOrderData(
    modifier: Modifier = Modifier,
    cartOrder: CartOrder,
    orderDetails: SelectedOrderDetails,
    addOnItems: List<AddOnItem>,
    onDeleteClick: (Int) -> Unit,
    onEditClick: (Int) -> Unit,
    onIncreaseQty: (orderId: Int, productId: Int) -> Unit,
    onDecreaseQty: (orderId: Int, productId: Int) -> Unit,
    onUpdateAddOnItem: (orderId: Int, itemId: Int) -> Unit,
    onPlaceOrder: (orderId: Int) -> Unit,
    onPrintOrder: (orderId: Int) -> Unit,
) = trace("CartOrders") {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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
                        .clip(RoundedCornerShape(SpaceMini)),
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
                            icon = PoposIcons.Tag,
                            doesSelected = true,
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
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(SpaceMini),
                        ),
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
                        .background(
                            MaterialTheme.colorScheme.secondary,
                            RoundedCornerShape(SpaceMini),
                        ),
                ) {
                    Icon(
                        contentDescription = "Delete cart order",
                        imageVector = PoposIcons.Delete,
                        tint = MaterialTheme.colorScheme.onSecondary,
                    )
                }
            }

            when (orderDetails) {
                is SelectedOrderDetails.Loading -> CircularProgressIndicator()

                is SelectedOrderDetails.Empty -> {
                    Text(
                        text = "You have not added any products yet.",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }

                is SelectedOrderDetails.Success -> {
                    OutlinedCard {
                        CartItemProductDetailsSection(
                            cartProducts = orderDetails.cartItem.cartProducts,
                            decreaseQuantity = {
                                onDecreaseQty(orderDetails.cartItem.orderId, it)
                            },
                            increaseQuantity = {
                                onIncreaseQty(orderDetails.cartItem.orderId, it)
                            },
                        )

                        CartAddOnItems(
                            addOnItems = addOnItems,
                            selectedAddOnItem = orderDetails.cartItem.addOnItems,
                            onClick = {
                                onUpdateAddOnItem(orderDetails.cartItem.orderId, it)
                            },
                        )

                        CartItemTotalPriceSection(
                            itemCount = orderDetails.cartItem.cartProducts.size,
                            totalPrice = orderDetails.cartItem.orderPrice,
                            orderType = orderDetails.cartItem.orderType,
                            showPrintBtn = orderDetails.cartItem.orderType != OrderType.DineIn,
                            onClickPlaceOrder = {
                                onPlaceOrder(orderDetails.cartItem.orderId)
                            },
                            onClickPrintOrder = {
                                onPrintOrder(orderDetails.cartItem.orderId)
                                onPlaceOrder(orderDetails.cartItem.orderId)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartOrderData(
    modifier: Modifier = Modifier,
    cartOrder: CartOrder,
    doesSelected: (Int) -> Boolean,
    onSelectOrder: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
    onEditClick: (Int) -> Unit,
) = trace("CartOrders") {
    Row(
        modifier = modifier
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
                    icon = PoposIcons.Tag,
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
                .background(
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(SpaceMini),
                ),
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
                .background(
                    MaterialTheme.colorScheme.secondary,
                    RoundedCornerShape(SpaceMini),
                ),
        ) {
            Icon(
                contentDescription = "Delete cart order",
                imageVector = PoposIcons.Delete,
                tint = MaterialTheme.colorScheme.onSecondary,
            )
        }
    }
}