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

package com.niyaj.order.deliveryPartner

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.util.trace
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.TotalDeliveryPartnerOrder
import com.niyaj.model.TotalOrders
import com.niyaj.order.components.DeliveryPartnerCard
import com.niyaj.order.components.ShareableDeliveryPartner
import com.niyaj.order.components.TotalDeliveryReportCard
import com.niyaj.order.destinations.DeliveryPartnerDetailsScreenDestination
import com.niyaj.print.OrderPrintViewModel
import com.niyaj.print.PrintEvent
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.NoteCard
import com.niyaj.ui.components.StandardBottomSheetScaffold
import com.niyaj.ui.event.ShareViewModel
import com.niyaj.ui.parameterProvider.DeliveryPartnerPreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.Screens.DELIVERY_REPORT_SCREEN
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.rememberCaptureController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch
import java.time.LocalDate

@Destination(route = DELIVERY_REPORT_SCREEN)
@Composable
fun DeliveryPartnerScreen(
    navigator: DestinationsNavigator,
    viewModel: DeliveryPartnerViewModel = hiltViewModel(),
    printViewModel: OrderPrintViewModel = hiltViewModel(),
    shareViewModel: ShareViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val captureController = rememberCaptureController()
    val snackbarHostState = remember { SnackbarHostState() }

    val partnerState by viewModel.allOrders.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

    val showShareDialog by shareViewModel.showDialog.collectAsStateWithLifecycle()

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

    fun printDeliveryReport(partnerId: Int? = null) {
        if (bluetoothAdapter?.isEnabled == true) {
            // Bluetooth is on print the receipt
            printViewModel.onPrintEvent(PrintEvent.PrintDeliveryReport(selectedDate, partnerId))
        } else {
            // Bluetooth is off, ask user to turn it on
            enableBluetoothContract.launch(enableBluetoothIntent)
            printViewModel.onPrintEvent(PrintEvent.PrintDeliveryReport(selectedDate, partnerId))
        }
    }

    LaunchedEffect(true) {
        printViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(event.errorMessage)
                    }
                }

                is UiEvent.OnSuccess -> {}
            }
        }
    }

    DeliveryPartnerScreenContent(
        partnerState = partnerState,
        selectedDate = selectedDate.ifEmpty { System.currentTimeMillis().toString() },
        onSelectDate = viewModel::selectDate,
        onClickShare = shareViewModel::onShowDialog,
        onBackClick = navigator::navigateUp,
        snackbarHostState = snackbarHostState,
        onClickPrintAll = {
            printDeliveryReport()
        },
        onClickPrint = {
            printDeliveryReport(it)
        },
        onClickViewDetails = {
            navigator.navigate(DeliveryPartnerDetailsScreenDestination(it))
        },
        onNavigateToHomeScreen = {
            navigator.navigate(Screens.HOME_SCREEN)
        },
    )

    AnimatedVisibility(
        visible = showShareDialog,
    ) {
        ShareableDeliveryPartner(
            modifier = Modifier,
            captureController = captureController,
            selectedDate = selectedDate,
            partnerState = partnerState,
            onDismiss = shareViewModel::onDismissDialog,
            onClickPrintOrder = {
                printDeliveryReport()
            },
            onClickShare = captureController::captureLongScreenshot,
            onCaptured = { bitmap, error ->
                bitmap?.let {
                    scope.launch {
                        val uri = shareViewModel.saveImage(it, context)
                        uri?.let {
                            shareViewModel.shareContent(
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
        )
    }
}

@VisibleForTesting
@Composable
internal fun DeliveryPartnerScreenContent(
    modifier: Modifier = Modifier,
    selectedDate: String,
    partnerState: PartnerState,
    onBackClick: () -> Unit,
    onSelectDate: (String) -> Unit,
    onClickPrintAll: () -> Unit,
    onClickShare: () -> Unit,
    onClickPrint: (Int) -> Unit,
    onClickViewDetails: (Int) -> Unit,
    onNavigateToHomeScreen: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) = trace("DeliveryReportScreenContent") {
    TrackScreenViewEvent(screenName = DELIVERY_REPORT_SCREEN)

    val lazyListState = rememberLazyListState()
    val dialogState = rememberMaterialDialogState()
    val totalOrders = if (partnerState is PartnerState.Success) {
        remember {
            TotalOrders(
                totalOrders = partnerState.orders.size.toLong(),
                totalAmount = partnerState.orders.sumOf { it.totalAmount },
            )
        }
    } else {
        TotalOrders()
    }

    StandardBottomSheetScaffold(
        modifier = modifier,
        title = "Delivery Reports",
        showBottomBar = true,
        bottomBar = {
            TotalDeliveryReportCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("totalOrderDetails"),
                totalOrders = totalOrders,
                selectedDate = selectedDate,
                onClickPrint = onClickPrintAll,
                onClickShare = onClickShare,
                onChangeDate = dialogState::show,
            )
        },
        onBackClick = onBackClick,
        snackbarHostState = snackbarHostState,
    ) {
        Crossfade(
            targetState = partnerState,
            label = "DeliveryReportState",
        ) { state ->
            when (state) {
                is PartnerState.Loading -> LoadingIndicator()

                is PartnerState.Empty -> {
                    ItemNotAvailable(
                        text = "Seems like, you have not place any order yet, click below to create new.",
                        buttonText = "Create New Order",
                        onClick = onNavigateToHomeScreen,
                    )
                }

                is PartnerState.Success -> {
                    TrackScrollJank(
                        scrollableState = lazyListState,
                        stateName = "DeliveryReportScreen",
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = SpaceMedium,
                            vertical = SpaceSmall,
                        ),
                        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                        state = lazyListState,
                    ) {
                        item {
                            NoteCard(
                                icon = PoposIcons.Info,
                                text = "Click an item to view details",
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                            )
                        }

                        items(
                            items = state.orders,
                            key = {
                                it.partnerId
                            },
                        ) {
                            DeliveryPartnerCard(
                                order = it,
                                onClickPrint = onClickPrint,
                                onClickViewDetails = onClickViewDetails,
                            )
                        }
                    }
                }
            }
        }
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
            onSelectDate(date.toMilliSecond)
        }
    }
}

@DevicePreviews
@Composable
private fun DeliveryPartnerScreenContentLoadingPreview(
    modifier: Modifier = Modifier,
) {
    DeliveryPartnerScreenContent(
        modifier = modifier,
        selectedDate = System.currentTimeMillis().toString(),
        partnerState = PartnerState.Loading,
        onBackClick = {},
        onSelectDate = {},
        onClickPrintAll = {},
        onClickShare = {},
        onClickPrint = {},
        onClickViewDetails = {},
        onNavigateToHomeScreen = {},
    )
}

@DevicePreviews
@Composable
private fun DeliveryPartnerScreenContentEmptyPreview(
    modifier: Modifier = Modifier,
) {
    DeliveryPartnerScreenContent(
        modifier = modifier,
        selectedDate = System.currentTimeMillis().toString(),
        partnerState = PartnerState.Empty,
        onBackClick = {},
        onSelectDate = {},
        onClickPrintAll = {},
        onClickShare = {},
        onClickPrint = {},
        onClickViewDetails = {},
        onNavigateToHomeScreen = {},
    )
}

@DevicePreviews
@Composable
private fun DeliveryPartnerScreenContentSuccessPreview(
    modifier: Modifier = Modifier,
    orders: List<TotalDeliveryPartnerOrder> = DeliveryPartnerPreviewData.partnerOrders,
) {
    DeliveryPartnerScreenContent(
        modifier = modifier,
        selectedDate = System.currentTimeMillis().toString(),
        partnerState = PartnerState.Success(orders = orders),
        onBackClick = {},
        onSelectDate = {},
        onClickPrintAll = {},
        onClickShare = {},
        onClickPrint = {},
        onClickViewDetails = {},
        onNavigateToHomeScreen = {},
    )
}
