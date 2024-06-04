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

package com.niyaj.cart.dineOut

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.cart.CartViewModel
import com.niyaj.cart.DineOutEvent
import com.niyaj.cart.components.CartScreenContent
import com.niyaj.print.OrderPrintViewModel
import com.niyaj.print.PrintEvent
import com.niyaj.ui.utils.UiEvent

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DineOutScreen(
    onClickCreateOrder: () -> Unit,
    onClickEditOrder: (Int) -> Unit,
    onClickOrderDetails: (Int) -> Unit,
    onNavigateToOrderScreen: () -> Unit,
    viewModel: CartViewModel = hiltViewModel(),
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

    val uiState by viewModel.dineOutState.collectAsStateWithLifecycle()
    val selectedDineOutOrder = viewModel.selectedDineOutOrder.toList()
    val addOnItems by viewModel.addOnItems.collectAsStateWithLifecycle()
    val deliveryPartners by viewModel.deliveryPartners.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(null)

    LaunchedEffect(key1 = event) {
        event?.let { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.successMessage,
                        actionLabel = "View",
                        duration = SnackbarDuration.Short,
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        onNavigateToOrderScreen()
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

    CartScreenContent(
        modifier = Modifier,
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        selectedItems = selectedDineOutOrder,
        addOnItems = addOnItems,
        showPrintBtn = true,
        deliveryPartners = deliveryPartners,
        onClickCreateOrder = onClickCreateOrder,
        onClickEditOrder = onClickEditOrder,
        onClickOrderDetails = onClickOrderDetails,
        onClickSelectAll = {
            viewModel.onEvent(DineOutEvent.SelectAllDineOutCart)
        },
        onClickPlaceAllOrder = {
            viewModel.onEvent(DineOutEvent.PlaceAllDineOutCart)
        },
        onEvent = viewModel::onEvent,
        printOrder = printOrder,
        onClickPrintAllOrder = {
            printAllOrder(selectedDineOutOrder)
            viewModel.onEvent(DineOutEvent.PlaceAllDineOutCart)
        },
    )
}
