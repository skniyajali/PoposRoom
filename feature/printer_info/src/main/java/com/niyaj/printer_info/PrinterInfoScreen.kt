package com.niyaj.printer_info

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.InsertLink
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.outlined.Edit
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
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_INFO_NOTES_FOUR
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_INFO_NOTES_ONE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_INFO_NOTES_THREE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_INFO_NOTES_TWO
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_NOT_AVAIlABLE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_SCREEN_TITLE
import com.niyaj.common.tags.PrinterInfoTestTags.UPDATE_PRINTER_INFO
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toSafeString
import com.niyaj.designsystem.theme.LightColor9
import com.niyaj.designsystem.theme.ProfilePictureSizeMedium
import com.niyaj.designsystem.theme.ProfilePictureSizeSmall
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.printer_info.destinations.UpdatePrinterInfoScreenDestination
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.NoteCard
import com.niyaj.ui.components.StandardChip
import com.niyaj.ui.components.StandardOutlinedChip
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.TwoGridText
import com.niyaj.ui.components.drawAnimatedBorder
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@RootNavGraph(start = true)
@Destination(
    route = Screens.PRINTER_INFO_SCREEN
)
fun PrinterInfoScreen(
    navController: NavController,
    viewModel: PrinterInfoViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<UpdatePrinterInfoScreenDestination, String>,
) {
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
    ) {}

    // This intent will open the enable bluetooth dialog
    val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

    val bluetoothManager = remember {
        context.getSystemService(BluetoothManager::class.java)
    }

    val bluetoothAdapter: BluetoothAdapter? = remember {
        bluetoothManager.adapter
    }

    LaunchedEffect(key1 = Unit) {
        if (bluetoothAdapter?.isEnabled == false) {
            enableBluetoothContract.launch(enableBluetoothIntent)
        }

        if (!bluetoothPermissions.allPermissionsGranted) {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
    }


    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val uiState = viewModel.info.collectAsStateWithLifecycle().value
    val printers = viewModel.printers.collectAsStateWithLifecycle().value

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        result.value
                    )
                }
            }
        }
    }

    StandardScaffold(
        navController = navController,
        snackbarHostState = snackbarHostState,
        title = PRINTER_SCREEN_TITLE,
        showBottomBar = false,
        navActions = {
            IconButton(
                onClick = {
                    navController.navigate(UpdatePrinterInfoScreenDestination())
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit Printer Information"
                )
            }
        },
        floatingActionButton = {},
        selectionCount = 0,
    ) {
        Crossfade(
            targetState = uiState,
            label = "Printer Information State"
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = PRINTER_NOT_AVAIlABLE,
                        buttonText = UPDATE_PRINTER_INFO,
                        icon = Icons.Default.Edit,
                        onClick = {
//                            navController.navigate(UpdatePrinterInfoDestination())
                        }
                    )
                }

                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpaceSmall),
                        state = lazyListState,
                        verticalArrangement = Arrangement.spacedBy(SpaceSmall)
                    ) {
                        item("Notes") {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(SpaceSmall)
                            ) {
                                Spacer(modifier = Modifier.height(SpaceSmall))

                                NoteCard(text = PRINTER_INFO_NOTES_ONE)

                                NoteCard(text = PRINTER_INFO_NOTES_TWO)

                                NoteCard(text = PRINTER_INFO_NOTES_THREE)

                                NoteCard(text = PRINTER_INFO_NOTES_FOUR)
                            }
                        }

                        item("Printers") {
                            Column(
                                modifier = Modifier
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
//                                            backgroundColor = LightColor7
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(SpaceSmall),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.Start,
                                                    verticalArrangement = Arrangement.spacedBy(
                                                        SpaceMini
                                                    )
                                                ) {
                                                    IconWithText(
                                                        text = data.name,
                                                        icon = Icons.AutoMirrored.Filled.Notes
                                                    )

                                                    IconWithText(
                                                        text = data.address,
                                                        icon = Icons.Default.InsertLink
                                                    )
                                                }

                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(
                                                        SpaceSmall
                                                    )
                                                ) {
                                                    StandardOutlinedChip(
                                                        text = "Test Print",
                                                        onClick = viewModel::testPrint
                                                    )

                                                    StandardChip(
                                                        text = if (data.connected) "Connected" else "Connect",
//                                                        icon = if (!data.connected) Icons.Default.BluetoothConnected else Icons.Default.BluetoothDisabled,
                                                        isPrimary = data.connected,
                                                        isClickable = !data.connected,
                                                        onClick = {
                                                            viewModel.connectPrinter(data.address)
                                                        }
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

                        item("Printer Information") {
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = SpaceSmall),
                                shape = RoundedCornerShape(SpaceSmall),
//                                backgroundColor = LightColor7
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(SpaceSmall),
                                    verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                                    horizontalAlignment = Alignment.CenterHorizontally
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
                                                durationMillis = 2000
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Print,
                                            contentDescription = "Printer Info",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .size(ProfilePictureSizeSmall)
                                                .align(Alignment.Center)
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
                                        textTwo = state.data.printerDpi.toString()
                                    )

                                    HorizontalDivider(modifier = Modifier.fillMaxWidth())

                                    TwoGridText(
                                        textOne = "Printer Width",
                                        textTwo = "${state.data.printerWidth} mm"
                                    )

                                    HorizontalDivider(modifier = Modifier.fillMaxWidth())

                                    TwoGridText(
                                        textOne = "Printer NBR Lines",
                                        textTwo = state.data.printerNbrLines.toString()
                                    )

                                    HorizontalDivider(modifier = Modifier.fillMaxWidth())

                                    TwoGridText(
                                        textOne = "Product Name Length",
                                        textTwo = state.data.productNameLength.toString()
                                    )

                                    HorizontalDivider(modifier = Modifier.fillMaxWidth())

                                    TwoGridText(
                                        textOne = "Product Report Limit",
                                        textTwo = state.data.productWiseReportLimit.toString()
                                    )

                                    HorizontalDivider(modifier = Modifier.fillMaxWidth())

                                    TwoGridText(
                                        textOne = "Address Report Limit",
                                        textTwo = state.data.addressWiseReportLimit.toString()
                                    )

                                    HorizontalDivider(modifier = Modifier.fillMaxWidth())

                                    TwoGridText(
                                        textOne = "Customer Report Limit",
                                        textTwo = state.data.customerWiseReportLimit.toString()
                                    )

                                    HorizontalDivider(modifier = Modifier.fillMaxWidth())

                                    TwoGridText(
                                        textOne = "Print QR Code",
                                        textTwo = state.data.printQRCode.toSafeString
                                    )

                                    HorizontalDivider(modifier = Modifier.fillMaxWidth())

                                    TwoGridText(
                                        textOne = "Print Restaurant Logo",
                                        textTwo = state.data.printResLogo.toSafeString
                                    )

                                    HorizontalDivider(modifier = Modifier.fillMaxWidth())

                                    TwoGridText(
                                        textOne = "Print Welcome Text",
                                        textTwo = state.data.printWelcomeText.toSafeString
                                    )

                                    HorizontalDivider(modifier = Modifier.fillMaxWidth())

                                    TwoGridText(
                                        textOne = "Last Updated",
                                        textTwo = (state.data.updatedAt
                                            ?: state.data.createdAt).toPrettyDate()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}