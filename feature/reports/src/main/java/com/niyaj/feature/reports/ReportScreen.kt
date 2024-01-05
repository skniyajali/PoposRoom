package com.niyaj.feature.reports

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.common.utils.calculateStartOfDayTime
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.reports.components.AddressWiseReport
import com.niyaj.feature.reports.components.CategoryWiseReport
import com.niyaj.feature.reports.components.CustomerWiseReport
import com.niyaj.feature.reports.components.ProductWiseReport
import com.niyaj.feature.reports.components.ReportBarData
import com.niyaj.feature.reports.components.TotalReports
import com.niyaj.feature.reports.destinations.ViewLastSevenDaysReportsDestination
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardOutlinedAssistChip
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Report Screen
 * @author Sk Niyaj Ali
 */
@OptIn(ExperimentalPermissionsApi::class)
@RootNavGraph(start = true)
@Destination(route = Screens.REPORT_SCREEN)
@Composable
fun ReportScreen(
    navController: NavController,
    onClickAddress: (Int) -> Unit,
    onClickCustomer: (Int) -> Unit,
    onClickProduct: (Int) -> Unit,
    reportsViewModel: ReportsViewModel = hiltViewModel(),
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        ?: Screens.REPORT_SCREEN

    val context = LocalContext.current

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

    val printReport: () -> Unit = {
        if (bluetoothPermissions.allPermissionsGranted) {
            if (bluetoothAdapter?.isEnabled == true) {
                // Bluetooth is on print the receipt
                reportsViewModel.onReportEvent(ReportsEvent.PrintReport)
            } else {
                // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
                reportsViewModel.onReportEvent(ReportsEvent.PrintReport)
            }
        } else {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
    }

    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val dialogState = rememberMaterialDialogState()

    val report = reportsViewModel.reportState.collectAsStateWithLifecycle().value.report

    val reportBarState = reportsViewModel.reportsBarData.collectAsStateWithLifecycle().value

    val productState = reportsViewModel.productWiseData.collectAsStateWithLifecycle().value

    val selectedDate = reportsViewModel.selectedDate.collectAsStateWithLifecycle().value
    val lastSevenStartDate = calculateStartOfDayTime(days = "-8")

    val categoryState = reportsViewModel.categoryWiseData.collectAsStateWithLifecycle().value

    val addressState = reportsViewModel.addressWiseData.collectAsStateWithLifecycle().value

    val customerState = reportsViewModel.customerWiseData.collectAsStateWithLifecycle().value

    val selectedCategory = reportsViewModel.selectedCategory.collectAsStateWithLifecycle().value

    var categoryWiseRepExpanded by remember { mutableStateOf(false) }

    var productWiseRepExpanded by remember { mutableStateOf(false) }

    var customerWiseRepExpanded by remember { mutableStateOf(false) }

    var addressWiseRepExpanded by remember { mutableStateOf(false) }

    var selectedBarData by remember {
        mutableStateOf("")
    }

    var selectedProductData by remember {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = selectedDate) {
        selectedBarData = ""
        selectedProductData = ""
    }
    
    TrackScreenViewEvent(screenName = Screens.REPORT_SCREEN)

    StandardScaffoldNew(
        currentRoute = currentRoute,
        showBottomBar = lazyListState.isScrollingUp(),
        showBackButton = true,
        title = "Reports",
        navActions = {
            if (selectedDate.isNotEmpty() && selectedDate != LocalDate.now().toString()) {
                StandardOutlinedAssistChip(
                    text = selectedDate.toPrettyDate(),
                    icon = Icons.Default.ArrowDropDown,
                    onClick = {
                        dialogState.show()
                    }
                )
                Spacer(modifier = Modifier.width(SpaceMini))
            } else {
                IconButton(
                    onClick = { dialogState.show() }
                ) {
                    Icon(imageVector = Icons.Default.Today, contentDescription = "Choose Date")
                }
            }

            IconButton(
                onClick = printReport,
            ) {
                Icon(imageVector = Icons.Default.Print, contentDescription = "Print Reports")
            }

        },
        fabPosition = FabPosition.End,
        floatingActionButton = {
            ScrollToTop(
                visible = !lazyListState.isScrollingUp(),
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                }
            )
        },
        onBackClick = { navController.navigateUp() },
        onNavigateToDestination = {
            navController.navigate(it)
        }
    ) {
        TrackScrollJank(scrollableState = lazyListState, stateName = "Reports::List")

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .padding(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceMedium)
        ) {
            item("reportBoxData") {
                Spacer(modifier = Modifier.height(SpaceMini))

                TotalReports(
                    report = report,
                    onOrderClick = {
                        navController.navigate(Screens.ORDER_SCREEN)
                    },
                    onExpensesClick = {
                        navController.navigate(Screens.EXPENSES_SCREEN)
                    },
                    onRefreshReport = {
                        reportsViewModel.onReportEvent(ReportsEvent.RefreshReport)
                    }
                )
            }

            item("reportBarData") {
                ReportBarData(
                    reportBarState = reportBarState,
                    selectedBarData = selectedBarData,
                    onBarClick = {
                        selectedBarData = it
                    },
                    onClickViewMore = {
                        navController.navigate(ViewLastSevenDaysReportsDestination())
                    }
                )
            }

            item("categoryWiseReport") {
                CategoryWiseReport(
                    categoryState = categoryState,
                    reportExpanded = categoryWiseRepExpanded,
                    selectedCategory = selectedCategory,
                    onCategoryExpandChanged = {
                        reportsViewModel.onReportEvent(ReportsEvent.OnSelectCategory(it))
                    },
                    onExpandChanged = {
                        categoryWiseRepExpanded = !categoryWiseRepExpanded
                    },
                    onClickOrderType = {
                        reportsViewModel.onReportEvent(ReportsEvent.OnChangeCategoryOrderType(it))
                    },
                    onProductClick = onClickProduct
                )
            }

            item("productWiseReport") {
                ProductWiseReport(
                    productState = productState,
                    productRepExpanded = productWiseRepExpanded,
                    selectedProduct = selectedProductData,
                    onExpandChanged = {
                        productWiseRepExpanded = !productWiseRepExpanded
                    },
                    onClickOrderType = {
                        reportsViewModel.onReportEvent(ReportsEvent.OnChangeOrderType(it))
                    },
                    onBarClick = {
                        selectedProductData = it
                    }
                )
            }

            item("addressWiseReport") {
                AddressWiseReport(
                    addressState = addressState,
                    addressWiseRepExpanded = addressWiseRepExpanded,
                    onExpandChanged = {
                        addressWiseRepExpanded = !addressWiseRepExpanded
                    },
                    onAddressClick = onClickAddress
                )
            }

            item("customerWiseReport") {
                CustomerWiseReport(
                    customerState = customerState,
                    customerWiseRepExpanded = customerWiseRepExpanded,
                    onExpandChanged = {
                        customerWiseRepExpanded = !customerWiseRepExpanded
                    },
                    onCustomerClick = onClickCustomer
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
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
                (date.toMilliSecond >= lastSevenStartDate) && (date <= LocalDate.now())
            }
        ) { date ->
            reportsViewModel.onReportEvent(ReportsEvent.SelectDate(date.toString()))
        }
    }
}
