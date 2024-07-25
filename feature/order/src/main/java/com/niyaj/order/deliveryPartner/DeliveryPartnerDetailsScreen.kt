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
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.DeliveryReport
import com.niyaj.model.EmployeeNameAndId
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
import com.niyaj.ui.parameterProvider.CardOrderPreviewData
import com.niyaj.ui.parameterProvider.DeliveryPartnerPreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.rememberCaptureController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
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
    val snackbarHostState = remember { SnackbarHostState() }

    val reportState by viewModel.deliveryReports.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val showShareDialog by shareViewModel.showDialog.collectAsStateWithLifecycle()
    val partners by viewModel.partners.collectAsStateWithLifecycle()

    val selectedItems = viewModel.selectedItems.toList()

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

    DeliveryPartnerDetailsScreenContent(
        modifier = Modifier,
        selectedDate = selectedDate,
        reportState = reportState,
        selectedItems = selectedItems.toImmutableList(),
        partners = partners.toImmutableList(),
        onClickPrint = printDeliveryReport,
        onClickShare = shareViewModel::onShowDialog,
        onSelectDate = viewModel::selectDate,
        onSelectItem = viewModel::selectItem,
        onDeselectItems = viewModel::deselectItems,
        onClickSelectItems = viewModel::selectUnselectedOrders,
        onChangePartner = viewModel::onChangeDeliveryPartner,
        onBackClick = navigator::navigateUp,
        snackbarHostState = snackbarHostState,
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

@VisibleForTesting
@Composable
internal fun DeliveryPartnerDetailsScreenContent(
    modifier: Modifier = Modifier,
    selectedDate: String,
    reportState: PartnerReportState,
    selectedItems: ImmutableList<Int>,
    partners: ImmutableList<EmployeeNameAndId>,
    onClickPrint: () -> Unit,
    onClickShare: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateToHomeScreen: () -> Unit,
    onClickOrder: (Int) -> Unit,
    onSelectDate: (String) -> Unit,
    onSelectItem: (Int) -> Unit,
    onDeselectItems: () -> Unit,
    onClickSelectItems: () -> Unit,
    onChangePartner: (Int) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
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

    val onClickBack: () -> Unit = {
        if (selectedItems.isEmpty()) {
            onBackClick()
        } else {
            onDeselectItems()
        }
    }

    BackHandler { onClickBack() }

    StandardBottomSheetScaffold(
        modifier = modifier,
        title = if (selectedItems.isEmpty()) title else "${selectedItems.size} Selected",
        showBottomBar = true,
        bottomBar = {
            TotalDeliveryReportCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("totalOrderDetails")
                    .background(MaterialTheme.colorScheme.secondary),
                totalOrders = totalOrders,
                selectedDate = selectedDate.ifEmpty { System.currentTimeMillis().toString() },
                selectedCount = selectedItems.size,
                isInSelectionMode = selectedItems.isNotEmpty(),
                onClickSelectItems = onClickSelectItems,
                onChangePartner = onChangePartner,
                partners = partners,
                onClickPrint = onClickPrint,
                onClickShare = onClickShare,
                onChangeDate = dialogState::show,
                primaryBtnColor = MaterialTheme.colorScheme.secondary,
                secBtnColor = MaterialTheme.colorScheme.outline,
                color = MaterialTheme.colorScheme.secondary,
            )
        },
        onBackClick = onClickBack,
        snackbarHostState = snackbarHostState,
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
                        ) { deliveryReport ->
                            DeliveryReportCard(
                                order = deliveryReport,
                                isSelected = selectedItems::contains,
                                onSelectItem = onSelectItem,
                                onClickOrder = {
                                    if (selectedItems.isEmpty()) {
                                        onClickOrder(it)
                                    } else {
                                        onSelectItem(it)
                                    }
                                },
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun DeliveryReportCard(
    modifier: Modifier = Modifier,
    order: DeliveryReport,
    isSelected: (Int) -> Boolean,
    onClickOrder: (Int) -> Unit,
    onSelectItem: (Int) -> Unit,
    containerColor: Color = LightColor6,
) = trace("DeliveryReportCard") {
    ElevatedCard(
        modifier = modifier
            .combinedClickable(
                onLongClick = {
                    onSelectItem(order.orderId)
                },
                onClick = {
                    onClickOrder(order.orderId)
                },
            ),
        colors = CardDefaults.elevatedCardColors().copy(
            containerColor = containerColor,
        ),
        shape = RoundedCornerShape(SpaceMini),
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
                    doesSelected = isSelected(order.orderId),
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
        isSelected = { false },
        onClickOrder = {},
        onSelectItem = {},
    )
}

@DevicePreviews
@Composable
private fun DeliveryPartnerDetailsScreenLoadingState() {
    PoposRoomTheme {
        DeliveryPartnerDetailsScreenContent(
            modifier = Modifier,
            selectedDate = System.currentTimeMillis().toString(),
            reportState = PartnerReportState.Loading,
            partners = persistentListOf(),
            selectedItems = persistentListOf(),
            onClickPrint = {},
            onClickShare = {},
            onBackClick = {},
            onNavigateToHomeScreen = {},
            onClickOrder = {},
            onSelectDate = {},
            onSelectItem = {},
            onDeselectItems = {},
            onClickSelectItems = {},
            onChangePartner = {},
        )
    }
}

@DevicePreviews
@Composable
private fun DeliveryPartnerDetailsScreenEmptyState() {
    PoposRoomTheme {
        DeliveryPartnerDetailsScreenContent(
            modifier = Modifier,
            selectedDate = System.currentTimeMillis().toString(),
            reportState = PartnerReportState.Empty,
            selectedItems = persistentListOf(),
            partners = persistentListOf(),
            onClickPrint = {},
            onClickShare = {},
            onBackClick = {},
            onNavigateToHomeScreen = {},
            onClickOrder = {},
            onSelectDate = {},
            onSelectItem = {},
            onDeselectItems = {},
            onClickSelectItems = {},
            onChangePartner = {},
        )
    }
}

@DevicePreviews
@Composable
private fun DeliveryPartnerDetailsScreenSuccessState(
    orders: List<DeliveryReport> = DeliveryPartnerPreviewData.deliveryReports,
) {
    PoposRoomTheme {
        DeliveryPartnerDetailsScreenContent(
            modifier = Modifier,
            selectedDate = System.currentTimeMillis().toString(),
            reportState = PartnerReportState.Success(orders = orders),
            selectedItems = orders.filter { it.orderId % 2 == 0 }.map { it.orderId }
                .toImmutableList(),
            partners = CardOrderPreviewData.sampleEmployeeNameAndIds.toImmutableList(),
            onClickPrint = {},
            onClickShare = {},
            onBackClick = {},
            onNavigateToHomeScreen = {},
            onClickOrder = {},
            onSelectDate = {},
            onSelectItem = {},
            onDeselectItems = {},
            onClickSelectItems = {},
            onChangePartner = {},
        )
    }
}
