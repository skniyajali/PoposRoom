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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.DeliveryReport
import com.niyaj.model.TotalOrders
import com.niyaj.order.components.ShareablePartnerDetails
import com.niyaj.order.components.TotalDeliveryReportCard
import com.niyaj.order.destinations.OrderDetailsScreenDestination
import com.niyaj.print.OrderPrintViewModel
import com.niyaj.print.PrintEvent
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.ItemNotAvailableHalf
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardBottomSheetScaffold
import com.niyaj.ui.event.ShareViewModel
import com.niyaj.ui.parameterProvider.DeliveryReportPreviewParameter
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.rememberCaptureController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Date

@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun DeliveryPartnerDetailsScreen(
    partnerId: Int,
    navigator: DestinationsNavigator,
    viewModel: DeliveryPartnerViewModel = hiltViewModel(),
    printViewModel: OrderPrintViewModel = hiltViewModel(),
    shareViewModel: ShareViewModel = hiltViewModel(),
) = trace("DeliveryPartnerDetailsScreen") {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val captureController = rememberCaptureController()

    val reportState by viewModel.deliveryReports.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val showShareDialog by shareViewModel.showDialog.collectAsStateWithLifecycle()

    TrackScreenViewEvent("DeliveryPartnerDetailsScreen/$partnerId")

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

    val printDeliveryReport: () -> Unit = {
        if (bluetoothAdapter?.isEnabled == true) {
            // Bluetooth is on print the receipt
            printViewModel.onPrintEvent(PrintEvent.PrintDeliveryReport(selectedDate, partnerId))
        } else {
            // Bluetooth is off, ask user to turn it on
            enableBluetoothContract.launch(enableBluetoothIntent)
            printViewModel.onPrintEvent(PrintEvent.PrintDeliveryReport(selectedDate, partnerId))
        }
    }

    DeliveryPartnerDetailsScreenContent(
        modifier = Modifier,
        selectedDate = selectedDate,
        reportState = reportState,
        onClickPrint = printDeliveryReport,
        onClickShare = shareViewModel::onShowDialog,
        onSelectDate = viewModel::selectDate,
        onBackClick = navigator::navigateUp,
        onNavigateToHomeScreen = {
            navigator.navigate(Screens.HOME_SCREEN)
        },
        onClickOrder = {
            navigator.navigate(OrderDetailsScreenDestination(it))
        },
    )

    AnimatedVisibility(
        visible = showShareDialog,
    ) {
        ShareablePartnerDetails(
            modifier = Modifier,
            captureController = captureController,
            selectedDate = selectedDate,
            reportState = reportState,
            onDismiss = shareViewModel::onDismissDialog,
            onClickPrintOrder = printDeliveryReport,
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

@Composable
private fun DeliveryPartnerDetailsScreenContent(
    modifier: Modifier = Modifier,
    selectedDate: String,
    reportState: PartnerReportState,
    onClickPrint: () -> Unit,
    onClickShare: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateToHomeScreen: () -> Unit,
    onClickOrder: (Int) -> Unit,
    onSelectDate: (String) -> Unit,
) = trace("DeliveryPartnerDetailsScreenContent") {
    val lazyListState = rememberLazyListState()
    val dialogState = rememberMaterialDialogState()
    val totalOrders = if (reportState is PartnerReportState.Success) {
        remember {
            TotalOrders(
                totalOrders = reportState.orders.size.toLong(),
                totalAmount = reportState.orders.sumOf { it.orderPrice },
            )
        }
    } else {
        TotalOrders()
    }
    val title = if (reportState is PartnerReportState.Success) {
        remember {
            reportState.orders.first().partnerName?.let {
                "$it Orders"
            } ?: "Unmanaged orders"
        }
    } else {
        "Partner Reports"
    }

    StandardBottomSheetScaffold(
        modifier = modifier,
        title = title,
        showBottomBar = true,
        bottomBar = {
            TotalDeliveryReportCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("totalOrderDetails")
                    .background(MaterialTheme.colorScheme.secondary),
                totalOrders = totalOrders,
                selectedDate = selectedDate.ifEmpty { System.currentTimeMillis().toString() },
                onClickPrint = onClickPrint,
                onClickShare = onClickShare,
                onChangeDate = dialogState::show,
                primaryBtnColor = MaterialTheme.colorScheme.secondary,
                secBtnColor = MaterialTheme.colorScheme.outline,
                color = MaterialTheme.colorScheme.secondary,
            )
        },
        onBackClick = onBackClick,
    ) {
        Crossfade(
            targetState = reportState,
            label = "ReportState",
        ) { state ->
            when (state) {
                is PartnerReportState.Loading -> LoadingIndicator()

                is PartnerReportState.Empty -> {
                    ItemNotAvailableHalf(
                        text = "Seems like, delivery partner not delivered any order yet, click below to create new.",
                        buttonText = "Create New Order",
                        onClick = onNavigateToHomeScreen,
                    )
                }

                is PartnerReportState.Success -> {
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
                        items(
                            items = state.orders,
                            key = {
                                it.orderId
                            },
                        ) {
                            DeliveryReportCard(
                                order = it,
                                onClickOrder = onClickOrder,
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

@Composable
internal fun DeliveryReportCard(
    modifier: Modifier = Modifier,
    order: DeliveryReport,
    onClickOrder: (Int) -> Unit,
    containerColor: Color = LightColor6,
) = trace("DeliveryReportCard") {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors().copy(
            containerColor = containerColor,
        ),
        shape = RoundedCornerShape(SpaceMini),
        onClick = {
            onClickOrder(order.orderId)
        },
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
            ) {
                CircularBox(
                    icon = PoposIcons.Tag,
                    doesSelected = false,
                    backgroundColor = MaterialTheme.colorScheme.background,
                    modifier = Modifier,
                )

                Text(
                    text = order.orderId.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier,
                )
            }

            Text(
                text = order.customerAddress,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier,
            )

            Text(
                text = order.customerPhone,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier,
            )

            Text(
                text = order.orderPrice.toRupee,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                modifier = Modifier,
            )

            Icon(
                imageVector = PoposIcons.ArrowRight,
                contentDescription = "ViewDetails",
                modifier = Modifier,
            )
        }
    }
}

@DevicePreviews
@Composable
private fun DeliveryReportCardPreview(
    modifier: Modifier = Modifier,
) {
    DeliveryReportCard(
        modifier = modifier,
        order = DeliveryReport(
            orderId = 8911,
            customerAddress = "eros",
            customerPhone = "(964) 177-5350",
            orderPrice = 8601,
            partnerId = 9182,
            partnerName = null,
            orderDate = Date(),
        ),
        onClickOrder = {},
    )
}

@DevicePreviews
@Composable
private fun DeliveryPartnerDetailsScreenLoadingState() {
    DeliveryPartnerDetailsScreenContent(
        modifier = Modifier,
        selectedDate = System.currentTimeMillis().toString(),
        reportState = PartnerReportState.Loading,
        onClickPrint = {},
        onClickShare = {},
        onBackClick = {},
        onNavigateToHomeScreen = {},
        onClickOrder = {},
        onSelectDate = {},
    )
}

@DevicePreviews
@Composable
private fun DeliveryPartnerDetailsScreenEmptyState() {
    DeliveryPartnerDetailsScreenContent(
        modifier = Modifier,
        selectedDate = System.currentTimeMillis().toString(),
        reportState = PartnerReportState.Empty,
        onClickPrint = {},
        onClickShare = {},
        onBackClick = {},
        onNavigateToHomeScreen = {},
        onClickOrder = {},
        onSelectDate = {},
    )
}

@DevicePreviews
@Composable
private fun DeliveryPartnerDetailsScreenSuccessState(
    @PreviewParameter(DeliveryReportPreviewParameter::class)
    orders: List<DeliveryReport>,
) {
    DeliveryPartnerDetailsScreenContent(
        modifier = Modifier,
        selectedDate = System.currentTimeMillis().toString(),
        reportState = PartnerReportState.Success(orders = orders),
        onClickPrint = {},
        onClickShare = {},
        onBackClick = {},
        onNavigateToHomeScreen = {},
        onClickOrder = {},
        onSelectDate = {},
    )
}
