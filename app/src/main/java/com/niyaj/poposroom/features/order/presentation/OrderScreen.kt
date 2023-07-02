package com.niyaj.poposroom.features.order.presentation

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.poposroom.features.cart.presentation.components.CartTabItem
import com.niyaj.poposroom.features.cart_order.domain.utils.OrderType
import com.niyaj.poposroom.features.common.components.NAV_SEARCH_BTN
import com.niyaj.poposroom.features.common.components.StandardOutlinedAssistChip
import com.niyaj.poposroom.features.common.components.StandardScaffoldNew
import com.niyaj.poposroom.features.common.components.StandardSearchBar
import com.niyaj.poposroom.features.common.components.Tabs
import com.niyaj.poposroom.features.common.components.TabsContent
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.common.utils.toMilliSecond
import com.niyaj.poposroom.features.common.utils.toPrettyDate
import com.niyaj.poposroom.features.destinations.CartScreenDestination
import com.niyaj.poposroom.features.order.domain.utils.OrderTestTags.DELETE_ORDER
import com.niyaj.poposroom.features.order.domain.utils.OrderTestTags.DELETE_ORDER_MESSAGE
import com.niyaj.poposroom.features.order.presentation.components.OrderedItemLayout
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)
@Destination
@Composable
fun OrderScreen(
    navController: NavController,
    viewModel: OrderViewModel = hiltViewModel(),
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { 2 }
    )

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val bluetoothPermissions =
        // Checks if the device has Android 12 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                )
            )
        } else {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                )
            )
        }

    val enableBluetoothContract = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            Timber.d("bluetoothLauncher", "Success")
        } else {
            Timber.w("bluetoothLauncher", "Failed")
        }
    }

    // This intent will open the enable bluetooth dialog
    val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

    val bluetoothManager = remember {
        context.getSystemService(BluetoothManager::class.java)
    }

    val bluetoothAdapter: BluetoothAdapter? = remember {
        bluetoothManager.adapter
    }

    val printDeliveryReport: () -> Unit = {
        if (bluetoothPermissions.allPermissionsGranted) {
            if (bluetoothAdapter?.isEnabled == true) {
                // Bluetooth is on print the receipt
                viewModel.onOrderEvent(OrderEvent.PrintDeliveryReport)
            } else {
                // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
                viewModel.onOrderEvent(OrderEvent.PrintDeliveryReport)
            }
        } else {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
    }

    val printOrder: (Int) -> Unit = {
        if (bluetoothPermissions.allPermissionsGranted) {
            if (bluetoothAdapter?.isEnabled == true) {
                // Bluetooth is on print the receipt
//                printViewModel.onPrintEvent(PrintEvent.PrintOrder(it))
            } else {
                // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
//                printViewModel.onPrintEvent(PrintEvent.PrintOrder(it))
            }
        } else {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
    }

    val dialogState = rememberMaterialDialogState()
    val deleteOrderState = rememberMaterialDialogState()

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value

    val orders = viewModel.cartOrders.collectAsStateWithLifecycle().value.orders
    val isLoading: Boolean = viewModel.cartOrders.collectAsStateWithLifecycle().value.isLoading

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

    var deletableOrder by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = true){
        viewModel.eventFlow.collectLatest { event ->
            when(event){
                is UiEvent.OnSuccess -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.successMessage,
                        actionLabel = "View",
                        duration = SnackbarDuration.Short
                    )
                    if(result == SnackbarResult.ActionPerformed){
                        navController.navigate(CartScreenDestination())
                    }
                }

                is UiEvent.OnError -> {
                    snackbarHostState.showSnackbar(
                        message = event.errorMessage,
                        duration = SnackbarDuration.Short
                    )
                }

                is UiEvent.IsLoading -> {}
            }
        }
    }

    BackHandler(true) {
        if (showSearchBar){
            viewModel.closeSearchBar()
        } else{
            navController.navigateUp()
        }
    }

    StandardScaffoldNew(
        navController = navController,
        snackbarHostState = snackbarHostState,
        showBackButton = showSearchBar,
        showBottomBar = true,
        onBackClick = viewModel::closeSearchBar,
        title = "Orders",
        navActions = {
            if(showSearchBar){
                StandardSearchBar(
                    searchText = viewModel.searchText.value,
                    placeholderText = "Search for orders...",
                    onSearchTextChanged = viewModel::searchTextChanged,
                    onClearClick = viewModel::clearSearchText,
                )
            }
            else {
                val showIcon = if(pagerState.currentPage == 1) dineInOrders.isNotEmpty() else dineOutOrders.isNotEmpty()

                if (selectedDate.isNotEmpty()) {
                    StandardOutlinedAssistChip(
                        text = selectedDate.toPrettyDate(),
                        icon = Icons.Default.CalendarMonth,
                        onClick = {
                            dialogState.show()
                        },
                        trailingIcon = Icons.Default.ArrowDropDown,
                        borderColor = MaterialTheme.colorScheme.outline,
                    )
                }else{
                    IconButton(
                        onClick = { dialogState.show() }
                    ) {
                        Icon(imageVector = Icons.Default.Today, contentDescription = "Choose Date")
                    }
                }

                if (showIcon){
                    IconButton(
                        onClick = viewModel::openSearchBar
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = NAV_SEARCH_BTN,
                        )
                    }

                    if(pagerState.currentPage == 0){
                        IconButton(
                            onClick = {
                                printDeliveryReport()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeliveryDining,
                                contentDescription = "Print Delivery Reports",
                            )
                        }
                    }
                }
            }
        }
    ){
        MaterialDialog(
            dialogState = deleteOrderState,
            buttons = {
                positiveButton(
                    text = "Delete",
                    onClick = {
                        viewModel.onOrderEvent(OrderEvent.DeleteOrder(deletableOrder))
                        deletableOrder = 0
                    }
                )
                negativeButton(
                    text = "Cancel",
                    onClick = {
                        deleteOrderState.hide()
                        deletableOrder = 0
                    },
                )
            }
        ) {
            title(text = DELETE_ORDER)
            message(text = DELETE_ORDER_MESSAGE)
        }

        MaterialDialog(
            dialogState = dialogState,
            buttons = {
                positiveButton("Ok")
                negativeButton("Cancel")
            }
        ) {
            datepicker(
                allowedDateValidator = { date ->
                    date <= LocalDate.now()
                }
            ) { date ->
                viewModel.onOrderEvent(OrderEvent.SelectDate(date.toMilliSecond))
            }
        }

        val tabs = listOf(
            CartTabItem.DineOutItem {
                OrderedItemLayout(
                    navController = navController,
                    orders = dineOutOrders,
                    isLoading = isLoading,
                    showSearchBar = showSearchBar,
                    onClickPrintOrder = {
                        printOrder(it)
                    },
                    onClickDelete = {
                        deleteOrderState.show()
                        deletableOrder = it
                    },
                    onMarkedAsProcessing = {
                        viewModel.onOrderEvent(OrderEvent.MarkedAsProcessing(it))
                    }
                )
            },
            CartTabItem.DineInItem {
                OrderedItemLayout(
                    navController = navController,
                    orders = dineInOrders,
                    isLoading = isLoading,
                    showSearchBar = showSearchBar,
                    onClickPrintOrder = {
                        printOrder(it)
                    },
                    onClickDelete = {
                        deleteOrderState.show()
                        deletableOrder = it
                    },
                    onMarkedAsProcessing = {
                        viewModel.onOrderEvent(OrderEvent.MarkedAsProcessing(it))
                    }
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            Tabs(tabs = tabs, pagerState = pagerState)
            TabsContent(tabs = tabs, pagerState = pagerState)
        }
    }
}