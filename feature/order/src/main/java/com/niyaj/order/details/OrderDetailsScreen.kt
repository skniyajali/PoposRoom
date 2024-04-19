package com.niyaj.order.details

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.OrderType
import com.niyaj.order.components.AddressDetails
import com.niyaj.order.components.CartItemDetails
import com.niyaj.order.components.CartOrderDetails
import com.niyaj.order.components.CustomerDetails
import com.niyaj.order.components.ShareableOrderDetails
import com.niyaj.print.OrderPrintViewModel
import com.niyaj.print.PrintEvent
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.isScrollingUp
import com.niyaj.ui.utils.rememberCaptureController
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Destination(route = Screens.ORDER_DETAILS_SCREEN)
@Composable
fun OrderDetailsScreen(
    orderId: Int,
    navController: NavController,
    onClickCustomer: (customerId: Int) -> Unit,
    onClickAddress: (addressId: Int) -> Unit,
    viewModel: OrderDetailsViewModel = hiltViewModel(),
    printViewModel: OrderPrintViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val captureController = rememberCaptureController()
    val lazyListState = rememberLazyListState()

    val state = viewModel.orderDetails.collectAsStateWithLifecycle().value
    val charges = viewModel.charges.collectAsStateWithLifecycle().value

    val showShareDialog = viewModel.showDialog.collectAsStateWithLifecycle().value

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
                // This intent will open the enable bluetooth dialog
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

                // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
                printViewModel.onPrintEvent(PrintEvent.PrintOrder(it))
            }
        } else {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
    }

    var cartOrderExpended by remember {
        mutableStateOf(true)
    }

    var customerExpended by remember {
        mutableStateOf(false)
    }

    var addressExpended by remember {
        mutableStateOf(false)
    }

    var cartExpended by remember {
        mutableStateOf(true)
    }

    TrackScreenViewEvent(screenName = Screens.ORDER_DETAILS_SCREEN)

    StandardScaffoldNew(
        navController = navController,
        title = "Order Details",
        showBackButton = true,
        showBottomBar = false,
        navActions = {
            IconButton(
                onClick = viewModel::onShowDialog
            ) {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = "Share Order Details"
                )
            }

            IconButton(
                onClick = {
                    printOrder(orderId)
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Print,
                    contentDescription = "Print Order Details"
                )
            }
        },
        showFab = lazyListState.isScrollingUp(),
        fabPosition = FabPosition.EndOverlay,
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::onShowDialog,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share List")
            }
        }
    ) {
        Crossfade(
            targetState = state,
            label = "Order Details.."
        ) { newState ->
            when (newState) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(text = "Order Details not available")
                }

                is UiState.Success -> {
                    TrackScrollJank(
                        scrollableState = lazyListState,
                        stateName = "Order Details::List"
                    )

                    val orderDetails = newState.data

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(SpaceSmall),
                        state = lazyListState,
                    ) {
                        item("Order Details") {
                            CartOrderDetails(
                                cartOrder = orderDetails.cartOrder,
                                doesExpanded = cartOrderExpended,
                                onExpandChanged = {
                                    cartOrderExpended = !cartOrderExpended
                                }
                            )
                        }

                        item("Customer Details") {
                            if (orderDetails.cartOrder.customer.customerId != 0) {
                                CustomerDetails(
                                    customer = orderDetails.cartOrder.customer,
                                    doesExpanded = customerExpended,
                                    onExpandChanged = {
                                        customerExpended = !customerExpended
                                    },
                                    onClickViewDetails = onClickCustomer
                                )
                            }
                        }

                        item("Address Details") {
                            if (orderDetails.cartOrder.address.addressId != 0) {
                                AddressDetails(
                                    address = orderDetails.cartOrder.address,
                                    doesExpanded = addressExpended,
                                    onExpandChanged = {
                                        addressExpended = !addressExpended
                                    },
                                    onClickViewDetails = onClickAddress
                                )
                            }
                        }

                        item("Order Products") {
                            if (orderDetails.cartProducts.isNotEmpty()) {
                                CartItemDetails(
                                    orderType = orderDetails.cartOrder.orderType,
                                    doesChargesIncluded = orderDetails.cartOrder.doesChargesIncluded,
                                    addOnItems = orderDetails.addOnItems,
                                    cartProducts = orderDetails.cartProducts,
                                    charges = charges,
                                    additionalCharges = orderDetails.charges,
                                    orderPrice = orderDetails.orderPrice,
                                    doesExpanded = cartExpended,
                                    onExpandChanged = {
                                        cartExpended = !cartExpended
                                    }
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = showShareDialog
                    ) {
                        ShareableOrderDetails(
                            captureController = captureController,
                            orderDetails = orderDetails,
                            charges = if (orderDetails.cartOrder.orderType == OrderType.DineOut) {
                                charges.filterNot { !it.isApplicable }
                            } else emptyList(),
                            onDismiss = viewModel::onDismissDialog,
                            onClickShare = {
                                captureController.captureLongScreenshot()
                            },
                            onCaptured = { bitmap, error ->
                                bitmap?.let {
                                    scope.launch {
                                        val uri = viewModel.saveImage(it, context)
                                        uri?.let {
                                            viewModel.shareContent(context, "Share Image", uri)
                                        }
                                    }
                                }
                                error?.let {
                                    Log.d(
                                        "Capturable",
                                        "Error: ${it.message}\n${it.stackTrace.joinToString()}"
                                    )
                                }
                            },
                            onClickPrintOrder = {
                                viewModel.onDismissDialog()

                                // Todo:: Add Printing functionalities
                            }
                        )
                    }
                }
            }
        }
    }
}