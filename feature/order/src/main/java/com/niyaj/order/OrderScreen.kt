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

package com.niyaj.order

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.common.tags.OrderTestTags.DELETE_ORDER
import com.niyaj.common.tags.OrderTestTags.DELETE_ORDER_MESSAGE
import com.niyaj.common.utils.findActivity
import com.niyaj.common.utils.isToday
import com.niyaj.common.utils.openAppSettings
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.designsystem.components.PoposOutlinedAssistChip
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.model.Order
import com.niyaj.order.components.OrderedItemLayout
import com.niyaj.order.components.ShareableOrderDetails
import com.niyaj.order.destinations.OrderDetailsScreenDestination
import com.niyaj.print.OrderPrintViewModel
import com.niyaj.print.PrintEvent
import com.niyaj.ui.components.BluetoothPermissionDialog
import com.niyaj.ui.components.HandleBluetoothPermissionState
import com.niyaj.ui.components.OrderTab
import com.niyaj.ui.components.OrderTabs
import com.niyaj.ui.components.OrderTabsContent
import com.niyaj.ui.components.StandardScaffoldWithOutDrawer
import com.niyaj.ui.event.ShareViewModel
import com.niyaj.ui.parameterProvider.OrderPreviewParameter
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.rememberCaptureController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

@RootNavGraph(start = true)
@Destination(route = Screens.ORDER_SCREEN)
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    navigator: DestinationsNavigator,
    onClickEditOrder: (Int) -> Unit,
    viewModel: OrderViewModel = hiltViewModel(),
    shareViewModel: ShareViewModel = hiltViewModel(),
    printViewModel: OrderPrintViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val captureController = rememberCaptureController()
    val snackbarHostState = remember { SnackbarHostState() }

    val dineInOrders by viewModel.dineInOrders.collectAsStateWithLifecycle()
    val dineOutOrders by viewModel.dineOutOrders.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

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
        if (bluetoothAdapter?.isEnabled == true) {
            // Bluetooth is on print the receipt
            printViewModel.onPrintEvent(PrintEvent.PrintOrder(it))
        } else {
            // Bluetooth is off, ask user to turn it on
            enableBluetoothContract.launch(enableBluetoothIntent)
            printViewModel.onPrintEvent(PrintEvent.PrintOrder(it))
        }
    }

    val printDeliveryReport: () -> Unit = {
        if (bluetoothAdapter?.isEnabled == true) {
            // Bluetooth is on print the receipt
            printViewModel.onPrintEvent(PrintEvent.PrintDeliveryReport(selectedDate))
        } else {
            // Bluetooth is off, ask user to turn it on
            enableBluetoothContract.launch(enableBluetoothIntent)
            printViewModel.onPrintEvent(PrintEvent.PrintDeliveryReport(selectedDate))
        }
    }

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value

    val showShareDialog = shareViewModel.showDialog.collectAsStateWithLifecycle().value

    val bluetoothPermissionsState =
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

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.successMessage,
                        actionLabel = "View",
                        duration = SnackbarDuration.Short,
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        navigator.navigate(Screens.CART_SCREEN)
                    }
                }

                is UiEvent.OnError -> {
                    snackbarHostState.showSnackbar(
                        message = event.errorMessage,
                        duration = SnackbarDuration.Short,
                    )
                }
            }
        }
    }

    LaunchedEffect(key1 = true) {
        printViewModel.eventFlow.collectLatest { event ->
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

    BackHandler(true) {
        if (showSearchBar) {
            viewModel.closeSearchBar()
        } else {
            navigator.popBackStack()
        }
    }

    TrackScreenViewEvent(screenName = Screens.ORDER_SCREEN)

    HandleBluetoothPermissionState(
        multiplePermissionsState = bluetoothPermissionsState,
        onSuccessful = {
            OrderScreenContent(
                modifier = Modifier,
                dineInOrders = dineInOrders,
                dineOutOrders = dineOutOrders,
                selectedDate = selectedDate,
                snackbarHostState = snackbarHostState,
                showSearchBar = showSearchBar,
                searchText = viewModel.searchText.value,
                onOpenSearchBar = viewModel::openSearchBar,
                onSearchTextChanged = viewModel::searchTextChanged,
                onClearClick = viewModel::clearSearchText,
                onBackClick = {
                    if (showSearchBar) {
                        viewModel.closeSearchBar()
                    } else {
                        navigator.popBackStack()
                    }
                },
                onClickPrintDeliveryReport = {

                },
                onClickPrintOrder = printOrder,
                onOrderEvent = viewModel::onOrderEvent,
                onNavigateToHomeScreen = {
                    navigator.navigate(Screens.HOME_SCREEN)
                },
                onClickOrderDetails = {
                    navigator.navigate(OrderDetailsScreenDestination(it))
                },
                onClickEditOrder = onClickEditOrder,
                onClickShareOrder = {
                    shareViewModel.onShowDialog()
                    viewModel.onOrderEvent(OrderEvent.GetShareableDetails(it))
                },
            )

        },
        onError = { shouldShowRationale ->
            BluetoothPermissionDialog(
                onClickRequestPermission = {
                    bluetoothPermissionsState.launchMultiplePermissionRequest()
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
        visible = showShareDialog,
    ) {
        val orderDetails = viewModel.orderDetails.collectAsStateWithLifecycle().value
        val charges = viewModel.charges.collectAsStateWithLifecycle().value

        ShareableOrderDetails(
            captureController = captureController,
            orderDetails = orderDetails,
            charges = charges,
            onDismiss = shareViewModel::onDismissDialog,
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
            onClickPrintOrder = {
                shareViewModel.onDismissDialog()
                printViewModel.onPrintEvent(PrintEvent.PrintOrder(it))
            },
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun OrderScreenContent(
    modifier: Modifier = Modifier,
    dineInOrders: OrderState,
    dineOutOrders: OrderState,
    selectedDate: String,
    showSearchBar: Boolean,
    searchText: String,
    onOpenSearchBar: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    onBackClick: () -> Unit,
    onClickPrintDeliveryReport: () -> Unit,
    onClickPrintOrder: (Int) -> Unit,
    onOrderEvent: (OrderEvent) -> Unit,
    onNavigateToHomeScreen: () -> Unit,
    onClickOrderDetails: (Int) -> Unit,
    onClickEditOrder: (Int) -> Unit,
    onClickShareOrder: (Int) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val dialogState = rememberMaterialDialogState()
    val deleteOrderState = rememberMaterialDialogState()

    var deletableOrder by remember { mutableIntStateOf(0) }

    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { 2 },
    )

    val showSearchIcon = if (pagerState.currentPage == 0) {
        if (dineOutOrders is OrderState.Success) dineOutOrders.data.isNotEmpty() else false
    } else {
        if (dineInOrders is OrderState.Success) dineInOrders.data.isNotEmpty() else false
    }

    StandardScaffoldWithOutDrawer(
        modifier = modifier,
        title = if (selectedDate.isNotEmpty() && !selectedDate.isToday) "" else "Orders",
        showSearchBar = showSearchBar,
        showSearchIcon = showSearchIcon,
        searchText = searchText,
        searchPlaceholderText = "Search for orders...",
        openSearchBar = onOpenSearchBar,
        onSearchTextChanged = onSearchTextChanged,
        onClearClick = onClearClick,
        onBackClick = onBackClick,
        snackbarHostState = snackbarHostState,
        navActions = {
            if (selectedDate.isNotEmpty() && !selectedDate.isToday) {
                PoposOutlinedAssistChip(
                    text = selectedDate.toPrettyDate(),
                    icon = PoposIcons.CalenderMonth,
                    onClick = {
                        dialogState.show()
                    },
                    trailingIcon = PoposIcons.ArrowDown,
                )
            } else {
                IconButton(
                    onClick = {
                        dialogState.show()
                    },
                ) {
                    Icon(
                        imageVector = PoposIcons.Today,
                        contentDescription = "Choose Date",
                    )
                }
            }

            if (showSearchIcon) {
                if (pagerState.currentPage == 0) {
                    IconButton(
                        onClick = onClickPrintDeliveryReport,
                    ) {
                        Icon(
                            imageVector = PoposIcons.DeliveryDining,
                            contentDescription = "Print Delivery Reports",
                        )
                    }
                }
            }
        },
    ) {
        val showDineOutBadge = if (dineOutOrders is OrderState.Success)
            dineOutOrders.data.isNotEmpty() else false

        val showDineInBadge = if (dineInOrders is OrderState.Success)
            dineInOrders.data.isNotEmpty() else false

        val tabs = listOf(
            OrderTab.DineOutOrder {
                OrderedItemLayout(
                    orderState = dineOutOrders,
                    showSearchBar = showSearchBar,
                    onClickPrintOrder = onClickPrintOrder,
                    onClickDelete = {
                        deleteOrderState.show()
                        deletableOrder = it
                    },
                    onMarkedAsProcessing = {
                        onOrderEvent(OrderEvent.MarkedAsProcessing(it))
                    },
                    onNavigateToHomeScreen = onNavigateToHomeScreen,
                    onClickOrderDetails = onClickOrderDetails,
                    onClickEditOrder = onClickEditOrder,
                    onClickShareOrder = onClickShareOrder,
                )
            },

            OrderTab.DineInOrder {
                OrderedItemLayout(
                    orderState = dineInOrders,
                    showSearchBar = showSearchBar,
                    onClickPrintOrder = onClickPrintOrder,
                    onClickDelete = {
                        deleteOrderState.show()
                        deletableOrder = it
                    },
                    onMarkedAsProcessing = {
                        onOrderEvent(OrderEvent.MarkedAsProcessing(it))
                    },
                    onNavigateToHomeScreen = onNavigateToHomeScreen,
                    onClickOrderDetails = onClickOrderDetails,
                    onClickEditOrder = onClickEditOrder,
                    onClickShareOrder = onClickShareOrder,
                )
            },
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            OrderTabs(
                tabs = listOf(
                    OrderTab.DineOutOrder(showDineOutBadge),
                    OrderTab.DineInOrder(showDineInBadge),
                ),
                pagerState = pagerState,
            )
            OrderTabsContent(tabs = tabs, pagerState = pagerState)
        }
    }

    MaterialDialog(
        dialogState = deleteOrderState,
        buttons = {
            positiveButton(
                text = "Delete",
                onClick = {
                    onOrderEvent(OrderEvent.DeleteOrder(deletableOrder))
                    deletableOrder = 0
                },
            )
            negativeButton(
                text = "Cancel",
                onClick = {
                    deleteOrderState.hide()
                    deletableOrder = 0
                },
            )
        },
    ) {
        title(text = DELETE_ORDER)
        message(text = DELETE_ORDER_MESSAGE)
    }

    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        },
    ) {
        datepicker(
            allowedDateValidator = { date ->
                date <= LocalDate.now()
            },
        ) { date ->
            onOrderEvent(OrderEvent.SelectDate(date.toMilliSecond))
        }
    }
}


@DevicePreviews
@Composable
private fun OrderScreenContentLoadingPreview() {
    OrderScreenContent(
        dineInOrders = OrderState.Loading,
        dineOutOrders = OrderState.Loading,
        selectedDate = "",
        showSearchBar = false,
        searchText = "",
        onOpenSearchBar = {},
        onSearchTextChanged = {},
        onClearClick = {},
        onBackClick = {},
        onClickPrintDeliveryReport = {},
        onClickPrintOrder = {},
        onOrderEvent = {},
        onNavigateToHomeScreen = {},
        onClickOrderDetails = {},
        onClickEditOrder = {},
        onClickShareOrder = {},
    )
}

@DevicePreviews
@Composable
private fun OrderScreenContentEmptyPreview() {
    OrderScreenContent(
        dineInOrders = OrderState.Empty,
        dineOutOrders = OrderState.Empty,
        selectedDate = "",
        showSearchBar = false,
        searchText = "",
        onOpenSearchBar = {},
        onSearchTextChanged = {},
        onClearClick = {},
        onBackClick = {},
        onClickPrintDeliveryReport = {},
        onClickPrintOrder = {},
        onOrderEvent = {},
        onNavigateToHomeScreen = {},
        onClickOrderDetails = {},
        onClickEditOrder = {},
        onClickShareOrder = {},
    )
}

@DevicePreviews
@Composable
private fun OrderScreenContentSuccessPreview(
    @PreviewParameter(OrderPreviewParameter::class)
    orders: Pair<List<Order>, List<Order>>,
) {
    OrderScreenContent(
        dineInOrders = OrderState.Success(orders.first),
        dineOutOrders = OrderState.Success(orders.second),
        selectedDate = "",
        showSearchBar = false,
        searchText = "",
        onOpenSearchBar = {},
        onSearchTextChanged = {},
        onClearClick = {},
        onBackClick = {},
        onClickPrintDeliveryReport = {},
        onClickPrintOrder = {},
        onOrderEvent = {},
        onNavigateToHomeScreen = {},
        onClickOrderDetails = {},
        onClickEditOrder = {},
        onClickShareOrder = {},
    )
}