package com.niyaj.cart.dine_out

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.cart.components.CartFooterPlaceOrder
import com.niyaj.cart.components.CartItems
import com.niyaj.core.ui.R
import com.niyaj.print.OrderPrintViewModel
import com.niyaj.print.PrintEvent
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DineOutScreen(
    navController: NavController,
    onClickEditOrder: (Int) -> Unit,
    onClickOrderDetails: (Int) -> Unit,
    onNavigateToOrderScreen: () -> Unit,
    viewModel: DineOutViewModel = hiltViewModel(),
    printViewModel: OrderPrintViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }

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

    val printAllOrder: (List<Int>) -> Unit = {
        if (bluetoothPermissions.allPermissionsGranted) {
            if (bluetoothAdapter?.isEnabled == true) {
                // Bluetooth is on print the receipt
                printViewModel.onPrintEvent(PrintEvent.PrintOrders(it))
            } else {
                // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
                printViewModel.onPrintEvent(PrintEvent.PrintOrders(it))
            }
        } else {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
    }

    val listState = rememberLazyListState()

    val dineOutOrders = viewModel.state.collectAsStateWithLifecycle().value.items
    val isLoading = viewModel.state.collectAsStateWithLifecycle().value.isLoading

    val countTotalDineOutItems = dineOutOrders.size
    val selectedDineOutOrder = viewModel.selectedItems.toList()
    val countSelectedDineOutItem = selectedDineOutOrder.size

    val addOnItems = viewModel.addOnItems.collectAsStateWithLifecycle().value

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.successMessage,
                        actionLabel = "View",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        onNavigateToOrderScreen()
                    }
                }

                is UiEvent.OnError -> {
                    snackbarHostState.showSnackbar(
                        message = event.errorMessage,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    TrackScreenViewEvent(screenName = "DineOut Tab::Cart")

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = dineOutOrders.isNotEmpty() && listState.isScrollingUp(),
                label = "BottomBar",
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { fullHeight ->
                        fullHeight / 4
                    }
                ),
                exit = fadeOut() + slideOutVertically(
                    targetOffsetY = { fullHeight ->
                        fullHeight / 4
                    }
                )
            ) {
                CartFooterPlaceOrder(
                    countTotalItems = countTotalDineOutItems,
                    countSelectedItem = countSelectedDineOutItem,
                    showPrintBtn = true,
                    onClickSelectAll = {
                        viewModel.onEvent(DineOutEvent.SelectAllDineOutOrder)
                    },
                    onClickPlaceAllOrder = {
                        viewModel.onEvent(DineOutEvent.PlaceAllDineOutOrder)
                    },
                    onClickPrintAllOrder = {
                        viewModel.onEvent(DineOutEvent.PlaceAllDineOutOrder)
                        printAllOrder(selectedDineOutOrder)
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        if (isLoading) {
            LoadingIndicator()
        } else if (dineOutOrders.isEmpty()) {
            ItemNotAvailable(
                text = "DineOut orders are not available",
                buttonText = "Add Item To Cart",
                image = painterResource(R.drawable.emptycarttwo),
                onClick = {
                    navController.navigate(Screens.HOME_SCREEN)
                }
            )
        } else {
            TrackScrollJank(scrollableState = listState, stateName = "DineOut Orders::Cart")

            CartItems(
                listState = listState,
                cartItems = dineOutOrders,
                selectedCartItems = selectedDineOutOrder,
                addOnItems = addOnItems,
                showPrintBtn = true,
                onClickEditOrder = onClickEditOrder,
                onClickViewOrder = onClickOrderDetails,
                onSelectCartOrder = {
                    viewModel.onEvent(DineOutEvent.SelectDineOutOrder(it))
                },
                onClickDecreaseQty = { cartOrderId, productId ->
                    viewModel.onEvent(
                        DineOutEvent.DecreaseQuantity(cartOrderId, productId)
                    )
                },
                onClickIncreaseQty = { cartOrderId, productId ->
                    viewModel.onEvent(
                        DineOutEvent.IncreaseQuantity(cartOrderId, productId)
                    )
                },
                onClickAddOnItem = { addOnItemId, cartOrderId ->
                    viewModel.onEvent(
                        DineOutEvent.UpdateAddOnItemInCart(addOnItemId, cartOrderId)
                    )
                },
                onClickPlaceOrder = {
                    viewModel.onEvent(DineOutEvent.PlaceDineOutOrder(it))
                },
                onClickPrintOrder = {
                    printOrder(it)
                    viewModel.onEvent(DineOutEvent.PlaceDineOutOrder(it))
                },
            )
        }
    }
}