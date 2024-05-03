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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.common.tags.OrderTestTags.DELETE_ORDER
import com.niyaj.common.tags.OrderTestTags.DELETE_ORDER_MESSAGE
import com.niyaj.common.utils.findActivity
import com.niyaj.common.utils.openAppSettings
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.model.OrderType
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
import com.niyaj.ui.components.StandardOutlinedAssistChip
import com.niyaj.ui.components.StandardScaffoldWithBottomNavigation
import com.niyaj.ui.event.ShareViewModel
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
@OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)
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

    val dialogState = rememberMaterialDialogState()
    val deleteOrderState = rememberMaterialDialogState()

    var deletableOrder by remember { mutableIntStateOf(0) }

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
            navigator.navigateUp()
        }
    }

    TrackScreenViewEvent(screenName = Screens.ORDER_SCREEN)

    HandleBluetoothPermissionState(
        multiplePermissionsState = bluetoothPermissionsState,
        onSuccessful = {
            val pagerState = rememberPagerState(
                initialPage = 0,
                initialPageOffsetFraction = 0f,
                pageCount = { 2 },
            )

            val orders = viewModel.cartOrders.collectAsStateWithLifecycle().value.orders
            val isLoading: Boolean =
                viewModel.cartOrders.collectAsStateWithLifecycle().value.isLoading

            val dineInOrders by remember(orders) {
                derivedStateOf {
                    orders.filter { order ->
                        order.orderType == OrderType.DineIn
                    }
                }
            }

            val dineOutOrders by remember(orders) {
                derivedStateOf {
                    orders.filter { order ->
                        order.orderType == OrderType.DineOut
                    }
                }
            }

            val selectedDate = viewModel.selectedDate.collectAsStateWithLifecycle().value
            val showIcon =
                if (pagerState.currentPage == 1) dineInOrders.isNotEmpty() else dineOutOrders.isNotEmpty()


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

            StandardScaffoldWithBottomNavigation(
                currentRoute = Screens.ORDER_SCREEN,
                title = if (selectedDate.isEmpty()) "Orders" else "",
                snackbarHostState = snackbarHostState,
                showBackButton = true,
                showBottomBar = !showSearchBar,
                showSearchIcon = showIcon,
                openSearchBar = viewModel::openSearchBar,
                closeSearchBar = viewModel::closeSearchBar,
                showSearchBar = showSearchBar,
                searchText = viewModel.searchText.value,
                searchPlaceholderText = "Search for orders...",
                onClearClick = viewModel::clearSearchText,
                onSearchTextChanged = viewModel::searchTextChanged,
                onNavigateToScreen = navigator::navigate,
                onBackClick = {
                    if (showSearchBar) {
                        viewModel.closeSearchBar()
                    } else {
                        navigator.navigateUp()
                    }
                },
                navActions = {
                    if (selectedDate.isNotEmpty()) {
                        StandardOutlinedAssistChip(
                            text = selectedDate.toPrettyDate(),
                            icon = PoposIcons.CalenderMonth,
                            onClick = {
                                dialogState.show()
                            },
                            trailingIcon = PoposIcons.ArrowDown,
                        )
                    } else {
                        IconButton(
                            onClick = { dialogState.show() },
                        ) {
                            Icon(
                                imageVector = PoposIcons.Today,
                                contentDescription = "Choose Date",
                            )
                        }
                    }

                    if (showIcon) {
                        if (pagerState.currentPage == 0) {
                            IconButton(
                                onClick = {
                                    printDeliveryReport()
                                },
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
                val tabs = listOf(
                    OrderTab.DineOutOrder {
                        OrderedItemLayout(
                            orders = dineOutOrders,
                            isLoading = isLoading,
                            showSearchBar = showSearchBar,
                            onClickPrintOrder = printOrder,
                            onClickDelete = {
                                deleteOrderState.show()
                                deletableOrder = it
                            },
                            onMarkedAsProcessing = {
                                viewModel.onOrderEvent(OrderEvent.MarkedAsProcessing(it))
                            },
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

                    OrderTab.DineInOrder(dineInOrders.isNotEmpty()) {
                        OrderedItemLayout(
                            orders = dineInOrders,
                            isLoading = isLoading,
                            showSearchBar = showSearchBar,
                            onClickPrintOrder = printOrder,
                            onClickDelete = {
                                deleteOrderState.show()
                                deletableOrder = it
                            },
                            onMarkedAsProcessing = {
                                viewModel.onOrderEvent(OrderEvent.MarkedAsProcessing(it))
                            },
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
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top,
                ) {
                    OrderTabs(
                        tabs = listOf(
                            OrderTab.DineOutOrder(dineOutOrders.isNotEmpty()),
                            OrderTab.DineInOrder(dineInOrders.isNotEmpty()),
                        ),
                        pagerState = pagerState,
                    )
                    OrderTabsContent(tabs = tabs, pagerState = pagerState)
                }
            }
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

    MaterialDialog(
        dialogState = deleteOrderState,
        buttons = {
            positiveButton(
                text = "Delete",
                onClick = {
                    viewModel.onOrderEvent(OrderEvent.DeleteOrder(deletableOrder))
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
            viewModel.onOrderEvent(OrderEvent.SelectDate(date.toMilliSecond))
        }
    }

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
