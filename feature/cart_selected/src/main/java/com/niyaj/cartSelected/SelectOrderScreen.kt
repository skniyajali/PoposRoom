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

package com.niyaj.cartSelected

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewParameter
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
import com.niyaj.designsystem.components.PoposIconButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.AddOnItem
import com.niyaj.model.CartItem
import com.niyaj.model.CartOrder
import com.niyaj.model.EmployeeNameAndId
import com.niyaj.model.OrderType
import com.niyaj.print.OrderPrintViewModel
import com.niyaj.print.PrintEvent
import com.niyaj.ui.components.AnimatedTextDividerDashed
import com.niyaj.ui.components.BluetoothPermissionDialog
import com.niyaj.ui.components.CartAddOnItems
import com.niyaj.ui.components.CartDeliveryPartners
import com.niyaj.ui.components.CartItemProductDetailsSection
import com.niyaj.ui.components.CartItemTotalPriceSection
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.HandleBluetoothPermissionState
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.NoteText
import com.niyaj.ui.components.PoposChip
import com.niyaj.ui.components.StandardBottomSheetScaffold
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.AddOnPreviewData
import com.niyaj.ui.parameterProvider.CardOrderPreviewData
import com.niyaj.ui.parameterProvider.CartOrderPreviewParameter
import com.niyaj.ui.parameterProvider.CartPreviewParameterData
import com.niyaj.ui.utils.DevicePreviews
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
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.cartOrders.collectAsStateWithLifecycle()
    val selectedOrder by viewModel.selectedId.collectAsStateWithLifecycle()

    val orderDetails by viewModel.orderDetails.collectAsStateWithLifecycle()
    val addOnItems by viewModel.addOnItems.collectAsStateWithLifecycle()
    val deliveryPartners by viewModel.deliveryPartners.collectAsStateWithLifecycle()

    val printError by printViewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(key1 = printError) {
        printError?.let { event ->
            when (event) {
                is UiEvent.OnError -> {
                    snackbarHostState.showSnackbar(
                        message = event.errorMessage,
                        duration = SnackbarDuration.Short,
                    )
                }

                else -> {}
            }
        }
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
            SelectOrderScreenContent(
                modifier = Modifier,
                state = state,
                selectedOrder = selectedOrder,
                orderDetails = orderDetails,
                addOnItems = addOnItems,
                deliveryPartners = deliveryPartners,
                onSelectOrder = viewModel::selectCartOrder,
                onDeleteClick = viewModel::deleteCartOrder,
                onIncreaseQty = viewModel::increaseProductQuantity,
                onDecreaseQty = viewModel::decreaseProductQuantity,
                onUpdateAddOnItem = viewModel::updateCartAddOnItem,
                onUpdateDeliveryPartner = viewModel::updateDeliveryPartner,
                onPlaceOrder = viewModel::placeOrder,
                onPrintOrder = printOrder,
                onBackClick = navigator::navigateUp,
                onEditClick = onEditClick,
                onClickCreateOrder = {
                    navigator.navigate(Screens.ADD_EDIT_CART_ORDER_SCREEN)
                },
                snackbarHostState = snackbarHostState,
            )
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
}

@VisibleForTesting
@Composable
internal fun SelectOrderScreenContent(
    modifier: Modifier = Modifier,
    state: UiState<List<CartOrder>>,
    selectedOrder: Int,
    orderDetails: SelectedOrderDetails,
    addOnItems: List<AddOnItem>,
    deliveryPartners: List<EmployeeNameAndId>,
    onSelectOrder: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
    onIncreaseQty: (orderId: Int, productId: Int) -> Unit,
    onDecreaseQty: (orderId: Int, productId: Int) -> Unit,
    onUpdateAddOnItem: (orderId: Int, itemId: Int) -> Unit,
    onUpdateDeliveryPartner: (orderId: Int, partnerId: Int) -> Unit,
    onPlaceOrder: (orderId: Int) -> Unit,
    onPrintOrder: (orderId: Int) -> Unit,
    onBackClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onClickCreateOrder: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    lazyListState: LazyListState = rememberLazyListState(),
) {
    var selectedId by remember {
        mutableIntStateOf(0)
    }
    val openDialog = remember { mutableStateOf(false) }

    StandardBottomSheetScaffold(
        modifier = modifier,
        title = SELECTED_SCREEN_TITLE,
        showBottomBar = false,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
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
                        onClick = onClickCreateOrder,
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
                                    deliveryPartners = deliveryPartners,
                                    onDeleteClick = {
                                        selectedId = it
                                        openDialog.value = true
                                    },
                                    onEditClick = onEditClick,
                                    onIncreaseQty = onIncreaseQty,
                                    onDecreaseQty = onDecreaseQty,
                                    onUpdateAddOnItem = onUpdateAddOnItem,
                                    onUpdateDeliveryPartner = onUpdateDeliveryPartner,
                                    onPlaceOrder = onPlaceOrder,
                                    onPrintOrder = onPrintOrder,
                                )
                            } else {
                                CartOrderData(
                                    cartOrder = item,
                                    doesSelected = {
                                        selectedOrder == it
                                    },
                                    onSelectOrder = onSelectOrder,
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

    AnimatedVisibility(
        visible = openDialog.value,
    ) {
        StandardDialog(
            title = CartOrderTestTags.DELETE_CART_ORDER_ITEM_TITLE,
            message = CartOrderTestTags.DELETE_CART_ORDER_ITEM_MESSAGE,
            onConfirm = {
                openDialog.value = false
                onDeleteClick(selectedId)
            },
            onDismiss = {
                openDialog.value = false
                selectedId = 0
            },
        )
    }
}

@Composable
private fun SelectedCartOrderData(
    modifier: Modifier = Modifier,
    cartOrder: CartOrder,
    orderDetails: SelectedOrderDetails,
    addOnItems: List<AddOnItem>,
    deliveryPartners: List<EmployeeNameAndId>,
    onDeleteClick: (Int) -> Unit,
    onEditClick: (Int) -> Unit,
    onIncreaseQty: (orderId: Int, productId: Int) -> Unit,
    onDecreaseQty: (orderId: Int, productId: Int) -> Unit,
    onUpdateAddOnItem: (orderId: Int, itemId: Int) -> Unit,
    onUpdateDeliveryPartner: (orderId: Int, partnerId: Int) -> Unit,
    onPlaceOrder: (orderId: Int) -> Unit,
    onPrintOrder: (orderId: Int) -> Unit,
    showDeliveryPartner: Boolean = deliveryPartners.isNotEmpty() &&
        cartOrder.orderType == OrderType.DineOut,
) = trace("CartOrders") {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = Color.Transparent,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CartOrderData(
                modifier = modifier,
                cartOrder = cartOrder,
                doesSelected = { true },
                onSelectOrder = {},
                onDeleteClick = onDeleteClick,
                onEditClick = onEditClick,
            )

            when (orderDetails) {
                is SelectedOrderDetails.Loading -> CircularProgressIndicator()

                is SelectedOrderDetails.Empty -> {
                    NoteText(
                        text = "You have not added any products yet.",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = SpaceSmall),
                    )
                }

                is SelectedOrderDetails.Success -> {
                    OutlinedCard(
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = Color.Transparent,
                        ),
                    ) {
                        CartItemProductDetailsSection(
                            cartProducts = orderDetails.cartItem.cartProducts,
                            decreaseQuantity = {
                                onDecreaseQty(orderDetails.cartItem.orderId, it)
                            },
                            increaseQuantity = {
                                onIncreaseQty(orderDetails.cartItem.orderId, it)
                            },
                        )

                        AnimatedVisibility(
                            visible = showDeliveryPartner,
                            enter = fadeIn(tween(500)),
                            exit = fadeOut(tween(600)),
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Spacer(modifier = Modifier.height(SpaceSmall))

                                AnimatedTextDividerDashed(text = "Delivery Partner")

                                Spacer(modifier = Modifier.height(SpaceSmall))

                                CartDeliveryPartners(
                                    partners = deliveryPartners,
                                    doesSelected = {
                                        it == orderDetails.cartItem.deliveryPartnerId
                                    },
                                    onClick = {
                                        val newId =
                                            if (it == orderDetails.cartItem.deliveryPartnerId) 0 else it
                                        onUpdateDeliveryPartner(
                                            orderDetails.cartItem.orderId,
                                            newId,
                                        )
                                    },
                                    backgroundColor = Color.Transparent,
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = addOnItems.isNotEmpty(),
                            enter = fadeIn(tween(500)),
                            exit = fadeOut(tween(600)),
                        ) {
                            val addOnSelectedColor =
                                if (orderDetails.cartItem.orderType == OrderType.DineIn) {
                                    MaterialTheme.colorScheme.secondary
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Spacer(modifier = Modifier.height(SpaceSmall))

                                AnimatedTextDividerDashed(text = "AddOn Items")

                                Spacer(modifier = Modifier.height(SpaceSmall))

                                CartAddOnItems(
                                    addOnItems = addOnItems,
                                    selectedAddOnItem = orderDetails.cartItem.addOnItems,
                                    selectedColor = addOnSelectedColor,
                                    onClick = {
                                        onUpdateAddOnItem(orderDetails.cartItem.orderId, it)
                                    },
                                    backgroundColor = Color.Transparent,
                                )

                                Spacer(modifier = Modifier.height(SpaceSmall))
                            }
                        }

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
private fun CartOrderData(
    modifier: Modifier = Modifier,
    cartOrder: CartOrder,
    doesSelected: (Int) -> Boolean,
    onSelectOrder: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
    onEditClick: (Int) -> Unit,
    containerColor: Color = containerColorFor(cartOrder.orderType),
    contentColor: Color = contentColorFor(cartOrder.orderType),
    icon: ImageVector = getIconFor(cartOrder.orderType),
) = trace("CartOrders") {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .weight(2f),
            shape = RoundedCornerShape(SpaceMini),
            onClick = {
                onSelectOrder(cartOrder.orderId)
            },
            colors = CardDefaults.cardColors().copy(
                containerColor = containerColor,
            ),
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier
                        .weight(2f)
                        .padding(horizontal = SpaceSmall),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
                ) {
                    CircularBox(
                        icon = PoposIcons.Tag,
                        doesSelected = doesSelected(cartOrder.orderId),
                        size = 30.dp,
                    )

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
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                PoposChip(
                    text = cartOrder.orderType.name,
                    icon = icon,
                    containerColor = contentColor,
                    shape = RoundedCornerShape(topStart = 2.dp, bottomStart = 2.dp),
                )
            }
        }

        PoposIconButton(
            icon = PoposIcons.Edit,
            onClick = { onEditClick(cartOrder.orderId) },
            containerColor = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(SpaceMini),
        )

        PoposIconButton(
            icon = PoposIcons.Delete,
            onClick = { onDeleteClick(cartOrder.orderId) },
            containerColor = MaterialTheme.colorScheme.secondary,
            shape = RoundedCornerShape(SpaceMini),
        )
    }
}

@Composable
private fun getIconFor(orderType: OrderType): ImageVector {
    return if (orderType == OrderType.DineIn) PoposIcons.DinnerDining else PoposIcons.DeliveryDining
}

@Composable
private fun containerColorFor(orderType: OrderType): Color {
    return if (orderType == OrderType.DineIn) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }
}

@Composable
private fun contentColorFor(orderType: OrderType): Color {
    return if (orderType == OrderType.DineIn) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.primary
    }
}

// ===========================PREVIEWS===================

@DevicePreviews
@Composable
private fun SelectOrderScreenContentPreview(
    @PreviewParameter(CartOrderPreviewParameter::class)
    state: UiState<List<CartOrder>>,
    modifier: Modifier = Modifier,
    cartItem: CartItem = CartPreviewParameterData.dineOutCartItems.first(),
    orderDetails: SelectedOrderDetails = SelectedOrderDetails.Success(cartItem),
    addOnItems: List<AddOnItem> = AddOnPreviewData.addOnItemList.take(5),
    deliveryPartners: List<EmployeeNameAndId> = CardOrderPreviewData.sampleEmployeeNameAndIds.take(5),
) {
    PoposRoomTheme {
        SelectOrderScreenContent(
            modifier = modifier,
            state = state,
            selectedOrder = 2,
            orderDetails = orderDetails,
            addOnItems = addOnItems,
            deliveryPartners = deliveryPartners,
            onSelectOrder = {},
            onDeleteClick = {},
            onIncreaseQty = { _, _ -> },
            onDecreaseQty = { _, _ -> },
            onUpdateAddOnItem = { _, _ -> },
            onUpdateDeliveryPartner = { _, _ -> },
            onPlaceOrder = {},
            onPrintOrder = {},
            onBackClick = {},
            onEditClick = {},
            onClickCreateOrder = {},
        )
    }
}

@DevicePreviews
@Composable
private fun SelectedCartOrderDataLoadingPreview(
    modifier: Modifier = Modifier,
    cartOrder: CartOrder = CardOrderPreviewData.orders.first(),
    orderDetails: SelectedOrderDetails = SelectedOrderDetails.Loading,
) {
    PoposRoomTheme {
        SelectedCartOrderData(
            modifier = modifier,
            cartOrder = cartOrder,
            orderDetails = orderDetails,
            addOnItems = listOf(),
            deliveryPartners = listOf(),
            onDeleteClick = {},
            onEditClick = {},
            onIncreaseQty = { _, _ -> },
            onDecreaseQty = { _, _ -> },
            onUpdateAddOnItem = { _, _ -> },
            onUpdateDeliveryPartner = { _, _ -> },
            onPlaceOrder = {},
            onPrintOrder = {},
        )
    }
}

@DevicePreviews
@Composable
private fun SelectedCartOrderDataEmptyPreview(
    modifier: Modifier = Modifier,
    cartOrder: CartOrder = CardOrderPreviewData.orders.last(),
    orderDetails: SelectedOrderDetails = SelectedOrderDetails.Empty,
) {
    PoposRoomTheme {
        SelectedCartOrderData(
            modifier = modifier,
            cartOrder = cartOrder,
            orderDetails = orderDetails,
            addOnItems = listOf(),
            deliveryPartners = listOf(),
            onDeleteClick = {},
            onEditClick = {},
            onIncreaseQty = { _, _ -> },
            onDecreaseQty = { _, _ -> },
            onUpdateAddOnItem = { _, _ -> },
            onUpdateDeliveryPartner = { _, _ -> },
            onPlaceOrder = {},
            onPrintOrder = {},
        )
    }
}

@DevicePreviews
@Composable
private fun SelectedDineInCartOrderDataPreview(
    modifier: Modifier = Modifier,
    cartOrder: CartOrder = CardOrderPreviewData.orders.first(),
    cartItem: CartItem = CartPreviewParameterData.dineInCartItems.first(),
    orderDetails: SelectedOrderDetails = SelectedOrderDetails.Success(cartItem),
    addOnItems: List<AddOnItem> = AddOnPreviewData.addOnItemList.take(5),
    deliveryPartners: List<EmployeeNameAndId> = CardOrderPreviewData.sampleEmployeeNameAndIds.take(5),
) {
    PoposRoomTheme {
        SelectedCartOrderData(
            modifier = modifier,
            cartOrder = cartOrder,
            orderDetails = orderDetails,
            addOnItems = addOnItems,
            deliveryPartners = deliveryPartners,
            onDeleteClick = {},
            onEditClick = {},
            onIncreaseQty = { _, _ -> },
            onDecreaseQty = { _, _ -> },
            onUpdateAddOnItem = { _, _ -> },
            onUpdateDeliveryPartner = { _, _ -> },
            onPlaceOrder = {},
            onPrintOrder = {},
        )
    }
}

@DevicePreviews
@Composable
private fun SelectedDineOutCartOrderDataPreview(
    modifier: Modifier = Modifier,
    cartOrder: CartOrder = CardOrderPreviewData.orders.last(),
    cartItem: CartItem = CartPreviewParameterData.dineInCartItems.first(),
    orderDetails: SelectedOrderDetails = SelectedOrderDetails.Success(cartItem),
    addOnItems: List<AddOnItem> = AddOnPreviewData.addOnItemList.take(5),
    deliveryPartners: List<EmployeeNameAndId> = CardOrderPreviewData.sampleEmployeeNameAndIds.take(5),
) {
    PoposRoomTheme {
        SelectedCartOrderData(
            modifier = modifier,
            cartOrder = cartOrder,
            orderDetails = orderDetails,
            addOnItems = addOnItems,
            deliveryPartners = deliveryPartners,
            onDeleteClick = {},
            onEditClick = {},
            onIncreaseQty = { _, _ -> },
            onDecreaseQty = { _, _ -> },
            onUpdateAddOnItem = { _, _ -> },
            onUpdateDeliveryPartner = { _, _ -> },
            onPlaceOrder = {},
            onPrintOrder = {},
        )
    }
}

@DevicePreviews
@Composable
private fun CartOrderDataPreview(
    modifier: Modifier = Modifier,
    cartOrder: CartOrder = CardOrderPreviewData.orders.last(),
) {
    PoposRoomTheme {
        CartOrderData(
            modifier = modifier,
            cartOrder = cartOrder,
            doesSelected = { true },
            onSelectOrder = {},
            onDeleteClick = {},
            onEditClick = {},
        )
    }
}
