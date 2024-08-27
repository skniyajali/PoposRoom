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

package com.niyaj.feature.reports

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.niyaj.common.utils.calculateStartOfDayTime
import com.niyaj.common.utils.getStartTime
import com.niyaj.common.utils.isToday
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.designsystem.components.PoposOutlinedAssistChip
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.chart.horizontalbar.model.HorizontalBarData
import com.niyaj.feature.reports.components.AddressWiseReport
import com.niyaj.feature.reports.components.CategoryWiseReport
import com.niyaj.feature.reports.components.CustomerWiseReport
import com.niyaj.feature.reports.components.ExpenseWiseReport
import com.niyaj.feature.reports.components.ProductWiseReport
import com.niyaj.feature.reports.components.ProductWiseReportPreviewData
import com.niyaj.feature.reports.components.ReportBarData
import com.niyaj.feature.reports.components.TotalReports
import com.niyaj.feature.reports.destinations.ViewLastSevenDaysReportsDestination
import com.niyaj.model.AddressWiseReport
import com.niyaj.model.CategoryWiseReport
import com.niyaj.model.CustomerWiseReport
import com.niyaj.model.ExpensesReport
import com.niyaj.model.Reports
import com.niyaj.model.TotalExpenses
import com.niyaj.model.TotalOrders
import com.niyaj.ui.components.ItemNotFound
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.ReportsPreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.flow.collectLatest
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
    navigator: DestinationsNavigator,
    onClickAddress: (Int) -> Unit,
    onClickCustomer: (Int) -> Unit,
    onClickProduct: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReportsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
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

    val printReport: () -> Unit = {
        if (bluetoothPermissions.allPermissionsGranted) {
            if (bluetoothAdapter?.isEnabled == true) {
                // Bluetooth is on print the receipt
                viewModel.onReportEvent(ReportsEvent.PrintReport)
            } else {
                // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
                viewModel.onReportEvent(ReportsEvent.PrintReport)
            }
        } else {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
    }

    val reportState = viewModel.reportState.collectAsStateWithLifecycle().value
    val reportBarState = viewModel.reportsBarData.collectAsStateWithLifecycle().value
    val productState = viewModel.productWiseData.collectAsStateWithLifecycle().value
    val categoryState = viewModel.categoryWiseData.collectAsStateWithLifecycle().value
    val addressState = viewModel.addressWiseData.collectAsStateWithLifecycle().value
    val customerState = viewModel.customerWiseData.collectAsStateWithLifecycle().value
    val expensesState = viewModel.expensesReports.collectAsStateWithLifecycle().value

    val selectedDate = viewModel.selectedDate.collectAsStateWithLifecycle().value
    val productOrderType = viewModel.productOrderType.collectAsStateWithLifecycle().value
    val categoryOrderType = viewModel.categoryOrderType.collectAsStateWithLifecycle().value
    val selectedCategory = viewModel.selectedCategory.collectAsStateWithLifecycle().value
    val totalCustomerReports = viewModel.totalCustomerReports.collectAsStateWithLifecycle().value
    val totalAddressReports = viewModel.totalAddressReports.collectAsStateWithLifecycle().value
    val totalExpensesReport = viewModel.totalExpensesReports.collectAsStateWithLifecycle().value

    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest {
            when (it) {
                is UiEvent.OnError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(it.errorMessage)
                    }
                }

                is UiEvent.OnSuccess -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(it.successMessage)
                    }
                }
            }
        }
    }

    ReportScreenContent(
        modifier = modifier,
        reportState = reportState,
        reportBarState = reportBarState,
        productState = productState,
        categoryState = categoryState,
        addressState = addressState,
        customerState = customerState,
        expensesState = expensesState,
        selectedDate = selectedDate,
        productOrderType = productOrderType,
        categoryOrderType = categoryOrderType,
        selectedCategory = selectedCategory,
        totalCustomerReports = totalCustomerReports,
        totalAddressReports = totalAddressReports,
        totalExpensesReport = totalExpensesReport,
        onAddressClick = onClickAddress,
        onClickCustomer = onClickCustomer,
        onClickProduct = onClickProduct,
        onPrintReport = printReport,
        onReportEvent = viewModel::onReportEvent,
        onBackClick = navigator::popBackStack,
        snackbarHostState = snackbarHostState,
        onOrderClick = {
            navigator.navigate(Screens.ORDER_SCREEN)
        },
        onExpensesClick = {
            navigator.navigate(Screens.EXPENSES_SCREEN)
        },
        onClickViewMore = {
            navigator.navigate(ViewLastSevenDaysReportsDestination)
        },
    )
}

@VisibleForTesting
@Composable
internal fun ReportScreenContent(
    reportState: UiState<Reports>,
    reportBarState: UiState<List<HorizontalBarData>>,
    productState: UiState<List<HorizontalBarData>>,
    categoryState: UiState<List<CategoryWiseReport>>,
    addressState: UiState<List<AddressWiseReport>>,
    customerState: UiState<List<CustomerWiseReport>>,
    expensesState: UiState<List<ExpensesReport>>,
    selectedDate: String,
    productOrderType: String,
    categoryOrderType: String,
    selectedCategory: String,
    totalCustomerReports: TotalOrders,
    totalAddressReports: TotalOrders,
    totalExpensesReport: TotalExpenses,
    onAddressClick: (Int) -> Unit,
    onClickCustomer: (Int) -> Unit,
    onClickProduct: (Int) -> Unit,
    onPrintReport: () -> Unit,
    onOrderClick: () -> Unit,
    onExpensesClick: () -> Unit,
    onClickViewMore: () -> Unit,
    onReportEvent: (ReportsEvent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Reports",
    lazyListState: LazyListState = rememberLazyListState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val scope = rememberCoroutineScope()
    val dialogState = rememberMaterialDialogState()

    var categoryWiseRepExpanded by remember { mutableStateOf(false) }
    var productWiseRepExpanded by remember { mutableStateOf(false) }
    var customerWiseRepExpanded by remember { mutableStateOf(false) }
    var addressWiseRepExpanded by remember { mutableStateOf(false) }
    var expensesRepExpanded by remember { mutableStateOf(false) }

    var selectedBarData by remember {
        mutableStateOf("")
    }
    var selectedProductData by remember {
        mutableStateOf("")
    }

    val lastSevenStartDate = calculateStartOfDayTime(days = "-8")

    LaunchedEffect(key1 = selectedDate) {
        selectedBarData = ""
        selectedProductData = ""
    }

    TrackScreenViewEvent(screenName = Screens.REPORT_SCREEN)

    BackHandler { onBackClick() }

    PoposSecondaryScaffold(
        title = title,
        modifier = modifier,
        onBackClick = onBackClick,
        showBackButton = true,
        fabPosition = FabPosition.End,
        snackbarHostState = snackbarHostState,
        navActions = {
            if (!selectedDate.isToday) {
                PoposOutlinedAssistChip(
                    text = selectedDate.toPrettyDate(),
                    icon = PoposIcons.ArrowDropDown,
                    onClick = dialogState::show,
                )
                Spacer(modifier = Modifier.width(SpaceMini))
            } else {
                IconButton(
                    onClick = dialogState::show,
                ) {
                    Icon(imageVector = PoposIcons.Today, contentDescription = "Choose Date")
                }
            }

            IconButton(
                onClick = onPrintReport,
            ) {
                Icon(imageVector = PoposIcons.Print, contentDescription = "Print Reports")
            }
        },
        floatingActionButton = {
            ScrollToTop(
                visible = !lazyListState.isScrollingUp(),
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
            )
        },
    ) { paddingValues ->
        TrackScrollJank(scrollableState = lazyListState, stateName = "Reports::List")

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        ) {
            item("reportBoxData") {
                Spacer(modifier = Modifier.height(SpaceMini))

                TotalReports(
                    uiState = reportState,
                    onOrderClick = onOrderClick,
                    onExpensesClick = onExpensesClick,
                    onRegenerateReport = {
                        onReportEvent(ReportsEvent.GenerateReport)
                    },
                )
            }

            item("reportBarData") {
                ReportBarData(
                    reportBarState = reportBarState,
                    selectedBarData = selectedBarData,
                    onBarClick = {
                        selectedBarData = it
                    },
                    onClickViewMore = onClickViewMore,
                    onClickPrint = {
                        onReportEvent(ReportsEvent.PrintBarReport)
                    },
                )
            }

            item("categoryWiseReport") {
                CategoryWiseReport(
                    categoryState = categoryState,
                    orderType = categoryOrderType,
                    reportExpanded = categoryWiseRepExpanded,
                    selectedCategory = selectedCategory,
                    onCategoryExpandChanged = {
                        onReportEvent(ReportsEvent.OnSelectCategory(it))
                    },
                    onExpandChanged = {
                        categoryWiseRepExpanded = !categoryWiseRepExpanded
                    },
                    onClickOrderType = {
                        onReportEvent(ReportsEvent.OnChangeCategoryOrderType(it))
                    },
                    onProductClick = onClickProduct,
                    onPrintProductWiseReport = {
                        onReportEvent(ReportsEvent.PrintCategoryWiseReport)
                    },
                )
            }

            item("productWiseReport") {
                ProductWiseReport(
                    productState = productState,
                    orderType = productOrderType,
                    productRepExpanded = productWiseRepExpanded,
                    selectedProduct = selectedProductData,
                    onExpandChanged = {
                        productWiseRepExpanded = !productWiseRepExpanded
                    },
                    onClickOrderType = {
                        onReportEvent(ReportsEvent.OnChangeProductOrderType(it))
                    },
                    onBarClick = {
                        selectedProductData = it
                    },
                    onPrintProductWiseReport = {
                        onReportEvent(ReportsEvent.PrintProductWiseReport)
                    },
                )
            }

            item("addressWiseReport") {
                AddressWiseReport(
                    addressState = addressState,
                    totalReports = totalAddressReports,
                    addressWiseRepExpanded = addressWiseRepExpanded,
                    onExpandChanged = {
                        addressWiseRepExpanded = !addressWiseRepExpanded
                    },
                    onAddressClick = onAddressClick,
                    onPrintAddressWiseReport = {
                        onReportEvent(ReportsEvent.PrintAddressWiseReport)
                    },
                )
            }

            item("customerWiseReport") {
                CustomerWiseReport(
                    customerState = customerState,
                    totalReports = totalCustomerReports,
                    customerWiseRepExpanded = customerWiseRepExpanded,
                    onExpandChanged = {
                        customerWiseRepExpanded = !customerWiseRepExpanded
                    },
                    onCustomerClick = onClickCustomer,
                    onPrintCustomerWiseReport = {
                        onReportEvent(ReportsEvent.PrintCustomerWiseReport)
                    },
                )
            }

            item("expenses_report") {
                // Expenses Report
                ExpenseWiseReport(
                    uiState = expensesState,
                    totalReports = totalExpensesReport,
                    doesExpanded = expensesRepExpanded,
                    onExpandChanged = {
                        expensesRepExpanded = !expensesRepExpanded
                    },
                    onPrintExpenseWiseReport = {
                        onReportEvent(ReportsEvent.PrintExpenseWiseReport)
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item("end_of_the_list") {
                ItemNotFound(
                    title = "No more reports available",
                    btnText = "Place New Order",
                    onBtnClick = onBackClick,
                )
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
                (date.toMilliSecond >= lastSevenStartDate) && (date <= LocalDate.now())
            },
        ) { date ->
            onReportEvent(ReportsEvent.SelectDate(date.toMilliSecond))
        }
    }
}

@DevicePreviews
@Composable
private fun ReportScreenContentPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        ReportScreenContent(
            modifier = modifier,
            reportState = UiState.Success(ReportsPreviewData.reports),
            reportBarState = UiState.Success(ProductWiseReportPreviewData.lastDaysReports),
            productState = UiState.Success(ProductWiseReportPreviewData.productWiseReport),
            categoryState = UiState.Success(ReportsPreviewData.categoryWiseReport),
            addressState = UiState.Success(ReportsPreviewData.addressWiseReport),
            customerState = UiState.Success(ReportsPreviewData.customerWiseReport),
            expensesState = UiState.Success(ReportsPreviewData.expensesReport),
            selectedDate = getStartTime,
            productOrderType = "All",
            categoryOrderType = "All",
            selectedCategory = "All",
            totalCustomerReports = TotalOrders(5000L, 5),
            totalAddressReports = TotalOrders(5000L, 5),
            totalExpensesReport = TotalExpenses(5000L, 5),
            onAddressClick = {},
            onClickCustomer = {},
            onClickProduct = {},
            onPrintReport = {},
            onOrderClick = {},
            onExpensesClick = {},
            onClickViewMore = {},
            onReportEvent = {},
            onBackClick = {},
        )
    }
}
