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

package com.niyaj.order.details

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.common.utils.findActivity
import com.niyaj.common.utils.openAppSettings
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Charges
import com.niyaj.model.EmployeeNameAndId
import com.niyaj.model.OrderDetails
import com.niyaj.order.components.AddressDetails
import com.niyaj.order.components.CartItemDetails
import com.niyaj.order.components.CartOrderDetails
import com.niyaj.order.components.CustomerDetails
import com.niyaj.order.components.ShareableOrderDetails
import com.niyaj.print.OrderPrintViewModel
import com.niyaj.print.PrintEvent
import com.niyaj.ui.components.BluetoothPermissionDialog
import com.niyaj.ui.components.HandleBluetoothPermissionState
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import com.niyaj.ui.utils.rememberCaptureController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Destination(route = Screens.ORDER_DETAILS_SCREEN)
@Composable
fun OrderDetailsScreen(
    orderId: Int,
    navigator: DestinationsNavigator,
    onClickCustomer: (customerId: Int) -> Unit,
    onClickAddress: (addressId: Int) -> Unit,
    viewModel: OrderDetailsViewModel = hiltViewModel(),
    printViewModel: OrderPrintViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val captureController = rememberCaptureController()

    val state by viewModel.orderDetails.collectAsStateWithLifecycle()
    val charges by viewModel.charges.collectAsStateWithLifecycle()
    val deliveryPartners by viewModel.deliveryPartners.collectAsStateWithLifecycle()
    val showShareDialog by viewModel.showDialog.collectAsStateWithLifecycle()

    val event by viewModel.eventFlow.collectAsStateWithLifecycle(null)
    val printError by printViewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

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

    val bluetoothManager = remember {
        context.getSystemService(BluetoothManager::class.java)
    }

    val bluetoothAdapter: BluetoothAdapter? = remember {
        bluetoothManager.adapter
    }

    val printOrder: () -> Unit = {
        if (bluetoothPermissions.allPermissionsGranted) {
            if (bluetoothAdapter?.isEnabled == true) {
                // Bluetooth is on print the receipt
                printViewModel.onPrintEvent(PrintEvent.PrintOrder(orderId))
            } else {
                // This intent will open the enable bluetooth dialog
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

                // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
                printViewModel.onPrintEvent(PrintEvent.PrintOrder(orderId))
            }
        } else {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
    }

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

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
                is UiEvent.OnError -> {
                    snackbarHostState.showSnackbar(
                        message = data.errorMessage,
                        duration = SnackbarDuration.Short,
                    )
                }

                is UiEvent.OnSuccess -> {
                    snackbarHostState.showSnackbar(
                        message = data.successMessage,
                        duration = SnackbarDuration.Short,
                    )
                }
            }
        }
    }

    TrackScreenViewEvent(screenName = Screens.ORDER_DETAILS_SCREEN)

    HandleBluetoothPermissionState(
        multiplePermissionsState = bluetoothPermissions,
        onSuccessful = {
            OrderDetailsContent(
                modifier = Modifier,
                state = state,
                charges = charges,
                partners = deliveryPartners,
                onBackClick = navigator::navigateUp,
                onClickShare = viewModel::onShowDialog,
                onClickPrint = printOrder,
                onChangePartner = viewModel::updateDeliveryPartner,
                onClickCustomer = onClickCustomer,
                onClickAddress = onClickAddress,
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

    AnimatedVisibility(
        visible = showShareDialog,
    ) {
        if (state is UiState.Success) {
            val orderDetails = state as UiState.Success<OrderDetails>

            ShareableOrderDetails(
                captureController = captureController,
                orderDetails = orderDetails,
                charges = charges,
                onDismiss = viewModel::onDismissDialog,
                onClickShare = captureController::captureLongScreenshot,
                onCaptured = { bitmap, error ->
                    bitmap?.let {
                        scope.launch {
                            val uri = viewModel.saveImage(it, context)
                            uri?.let {
                                viewModel.shareContent(
                                    context,
                                    "Share Image",
                                    uri,
                                )
                            }
                        }
                    }
                    error?.let {
                        Log.d(
                            "Capturable",
                            "Error: ${it.message}\n${it.stackTrace.joinToString()}",
                        )
                    }
                },
                onClickPrintOrder = {
                    viewModel.onDismissDialog()
                    printOrder()
                },
            )
        }
    }
}

@VisibleForTesting
@Composable
internal fun OrderDetailsContent(
    modifier: Modifier = Modifier,
    state: UiState<OrderDetails>,
    charges: List<Charges>,
    partners: List<EmployeeNameAndId>,
    onBackClick: () -> Unit,
    onClickShare: () -> Unit,
    onClickPrint: () -> Unit,
    onChangePartner: (Int) -> Unit,
    onClickCustomer: (customerId: Int) -> Unit,
    onClickAddress: (addressId: Int) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val lazyListState = rememberLazyListState()

    var cartOrderExpended by remember { mutableStateOf(true) }
    var customerExpended by remember { mutableStateOf(false) }
    var addressExpended by remember { mutableStateOf(false) }
    var cartExpended by remember { mutableStateOf(true) }

    PoposSecondaryScaffold(
        modifier = modifier,
        title = "Order Details",
        showBackButton = true,
        showBottomBar = false,
        navActions = {
            IconButton(
                onClick = onClickShare,
            ) {
                Icon(
                    imageVector = PoposIcons.Share,
                    contentDescription = "Share Order Details",
                )
            }

            IconButton(
                onClick = {
                    onClickPrint()
                },
            ) {
                Icon(
                    imageVector = PoposIcons.Print,
                    contentDescription = "Print Order Details",
                )
            }
        },
        showFab = lazyListState.isScrollingUp(),
        fabPosition = FabPosition.EndOverlay,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onClickShare,
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(imageVector = PoposIcons.Share, contentDescription = "Share List")
            }
        },
        onBackClick = onBackClick,
        snackbarHostState = snackbarHostState,
    ) {
        Crossfade(
            targetState = state,
            label = "Order Details..",
        ) { newState ->
            when (newState) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(text = "Order Details not available")
                }

                is UiState.Success -> {
                    TrackScrollJank(
                        scrollableState = lazyListState,
                        stateName = "Order Details::List",
                    )

                    val orderDetails = newState.data

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        contentPadding = PaddingValues(SpaceSmall),
                        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                        state = lazyListState,
                    ) {
                        item("Order Details") {
                            CartOrderDetails(
                                cartOrder = orderDetails.cartOrder,
                                deliveryPartner = orderDetails.deliveryPartner,
                                partners = partners,
                                doesExpanded = cartOrderExpended,
                                onExpandChanged = {
                                    cartOrderExpended = !cartOrderExpended
                                },
                                onChangePartner = onChangePartner,
                            )
                        }

                        if (orderDetails.cartOrder.customer.customerId != 0) {
                            item("Customer Details") {
                                CustomerDetails(
                                    customer = orderDetails.cartOrder.customer,
                                    doesExpanded = customerExpended,
                                    onExpandChanged = {
                                        customerExpended = !customerExpended
                                    },
                                    onClickViewDetails = onClickCustomer,
                                )
                            }
                        }

                        if (orderDetails.cartOrder.address.addressId != 0) {
                            item("Address Details") {
                                AddressDetails(
                                    address = orderDetails.cartOrder.address,
                                    doesExpanded = addressExpended,
                                    onExpandChanged = {
                                        addressExpended = !addressExpended
                                    },
                                    onClickViewDetails = onClickAddress,
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
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
