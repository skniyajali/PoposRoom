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

package com.niyaj.printerInfo

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_INFO_NOTES_FOUR
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_INFO_NOTES_ONE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_INFO_NOTES_THREE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_INFO_NOTES_TWO
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_NOT_AVAILABLE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_SCREEN_TITLE
import com.niyaj.common.tags.PrinterInfoTestTags.UPDATE_PRINTER_INFO
import com.niyaj.common.utils.findActivity
import com.niyaj.common.utils.openAppSettings
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toSafeString
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor9
import com.niyaj.designsystem.theme.ProfilePictureSizeMedium
import com.niyaj.designsystem.theme.ProfilePictureSizeSmall
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.utils.drawAnimatedBorder
import com.niyaj.model.BluetoothDeviceState
import com.niyaj.model.Printer
import com.niyaj.printerInfo.destinations.UpdatePrinterInfoScreenDestination
import com.niyaj.ui.components.BluetoothPermissionDialog
import com.niyaj.ui.components.HandleBluetoothPermissionState
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.InfoText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.PoposPrimaryScaffold
import com.niyaj.ui.components.StandardChip
import com.niyaj.ui.components.StandardOutlinedChip
import com.niyaj.ui.components.TwoGridText
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@RootNavGraph(start = true)
@Suppress("LongMethod")
@Destination(route = Screens.PRINTER_INFO_SCREEN)
fun PrinterInfoScreen(
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<UpdatePrinterInfoScreenDestination, String>,
    modifier: Modifier = Modifier,
    viewModel: PrinterInfoViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val enableBluetoothContract = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) {}

    val bluetoothManager = remember {
        context.getSystemService(BluetoothManager::class.java)
    }

    val bluetoothAdapter: BluetoothAdapter? = remember {
        bluetoothManager.adapter
    }

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

    LaunchedEffect(key1 = bluetoothPermissionsState, key2 = true) {
        if (bluetoothPermissionsState.allPermissionsGranted) {
            if (bluetoothAdapter?.isEnabled == false) {
                // This intent will open the enable bluetooth dialog
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

                enableBluetoothContract.launch(enableBluetoothIntent)
            }
        }
    }

    val uiState = viewModel.info.collectAsStateWithLifecycle().value
    val printers = viewModel.printers.collectAsStateWithLifecycle().value

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    BackHandler {
        navigator.popBackStack()
    }

    LaunchedEffect(key1 = event) {
        event?.let {
            when (event) {
                is UiEvent.OnError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(event.errorMessage)
                    }
                }

                is UiEvent.OnSuccess -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(event.successMessage)
                    }
                }
            }
        }
    }

    TrackScreenViewEvent(screenName = Screens.PRINTER_INFO_SCREEN)

    HandleBluetoothPermissionState(
        multiplePermissionsState = bluetoothPermissionsState,
        onSuccessful = {
            PrinterInfoScreenContent(
                uiState = uiState,
                printers = printers,
                printTestData = viewModel::printTestData,
                connectBluetoothPrinter = viewModel::connectBluetoothPrinter,
                onClickToUpdate = {
                    navigator.navigate(UpdatePrinterInfoScreenDestination())
                },
                onBackClick = navigator::popBackStack,
                onNavigateScreen = navigator::navigate,
                modifier = modifier,
                lazyListState = lazyListState,
            )
        },
        onError = { shouldShowRationale ->
            BluetoothPermissionDialog(
                shouldShowRationale = shouldShowRationale,
                onClickRequestPermission = {
                    bluetoothPermissionsState.launchMultiplePermissionRequest()
                },
                onClickOpenSettings = {
                    context.findActivity().openAppSettings()
                },
                onDismissRequest = {
                    navigator.navigateUp()
                },
            )
        },
    )
}

@Composable
fun PrinterInfoScreenContent(
    uiState: UiState<Printer>,
    printers: List<BluetoothDeviceState>,
    printTestData: () -> Unit,
    connectBluetoothPrinter: (String) -> Unit,
    onClickToUpdate: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    lazyListState: LazyListState = rememberLazyListState(),
) {
    PoposPrimaryScaffold(
        modifier = modifier,
        currentRoute = Screens.PRINTER_INFO_SCREEN,
        title = PRINTER_SCREEN_TITLE,
        selectionCount = 0,
        floatingActionButton = {},
        navActions = {
            IconButton(
                onClick = onClickToUpdate,
            ) {
                Icon(
                    imageVector = PoposIcons.Edit,
                    contentDescription = "Edit Printer Information",
                )
            }
        },
        onBackClick = onBackClick,
        onNavigateToScreen = onNavigateScreen,
        showBottomBar = false,
        snackbarHostState = snackbarHostState,
    ) {
        Crossfade(
            targetState = uiState,
            label = "Printer Information State",
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = PRINTER_NOT_AVAILABLE,
                        buttonText = UPDATE_PRINTER_INFO,
                        icon = PoposIcons.Edit,
                        onClick = {},
                    )
                }

                is UiState.Success -> {
                    TrackScrollJank(
                        scrollableState = lazyListState,
                        stateName = "Printer Info::State",
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(SpaceSmall),
                        state = lazyListState,
                        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                    ) {
                        item("Notes") {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                            ) {
                                Spacer(modifier = Modifier.height(SpaceSmall))

                                InfoText(text = PRINTER_INFO_NOTES_ONE)

                                InfoText(text = PRINTER_INFO_NOTES_TWO)

                                InfoText(text = PRINTER_INFO_NOTES_THREE)

                                InfoText(text = PRINTER_INFO_NOTES_FOUR)
                            }
                        }

                        item("Printers") {
                            PrinterList(
                                printers = printers,
                                connectBluetoothPrinter = connectBluetoothPrinter,
                                printTestData = printTestData,
                            )
                        }

                        item("Printer Information") {
                            PrinterInfoData(printer = state.data)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrinterList(
    printers: List<BluetoothDeviceState>,
    printTestData: () -> Unit,
    connectBluetoothPrinter: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
    ) {
        Text(
            text = "Printers",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )

        if (printers.isNotEmpty()) {
            printers.forEach { data ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(SpaceMini),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpaceSmall),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(
                                SpaceMini,
                            ),
                        ) {
                            IconWithText(
                                text = data.name,
                                icon = PoposIcons.StickyNote2,
                            )

                            IconWithText(
                                text = data.address,
                                icon = PoposIcons.Link,
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                SpaceSmall,
                            ),
                        ) {
                            StandardOutlinedChip(
                                text = "Test Print",
                                onClick = printTestData,
                            )

                            StandardChip(
                                text = if (data.connected) "Connected" else "Connect",
                                icon = if (data.connected) {
                                    PoposIcons.BluetoothConnected
                                } else {
                                    PoposIcons.BluetoothDisabled
                                },
                                isPrimary = data.connected,
                                isClickable = !data.connected,
                                onClick = {
                                    connectBluetoothPrinter(data.address)
                                },
                            )
                        }
                    }
                }
            }
        } else {
            Text(
                text = "Bluetooth printer is not available on this device",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun PrinterInfoData(
    printer: Printer,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = SpaceSmall),
        shape = RoundedCornerShape(SpaceSmall),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(SpaceSmall))

            Box(
                modifier = Modifier
                    .size(ProfilePictureSizeMedium)
                    .background(LightColor9, CircleShape)
                    .clip(CircleShape)
                    .drawAnimatedBorder(
                        1.dp,
                        CircleShape,
                        durationMillis = 2000,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = PoposIcons.Print,
                    contentDescription = "Printer Info",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(ProfilePictureSizeSmall)
                        .align(Alignment.Center),
                )
            }

            Text(
                text = "Printer Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Printer DPI",
                textTwo = printer.printerDpi.toString(),
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Printer Width",
                textTwo = "${printer.printerWidth} mm",
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Printer NBR Lines",
                textTwo = printer.printerNbrLines.toString(),
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Product Name Length",
                textTwo = printer.productNameLength.toString(),
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Product Report Limit",
                textTwo = printer.productWiseReportLimit.toString(),
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Address Report Limit",
                textTwo = printer.addressWiseReportLimit.toString(),
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Customer Report Limit",
                textTwo = printer.customerWiseReportLimit.toString(),
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Print QR Code",
                textTwo = printer.printQRCode.toSafeString,
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Print Restaurant Logo",
                textTwo = printer.printResLogo.toSafeString,
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Print Welcome Text",
                textTwo = printer.printWelcomeText.toSafeString,
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            TwoGridText(
                textOne = "Last Updated",
                textTwo = (printer.updatedAt ?: printer.createdAt).toPrettyDate(),
            )
        }
    }
}
