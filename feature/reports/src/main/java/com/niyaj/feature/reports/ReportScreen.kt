package com.niyaj.feature.reports

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.common.utils.calculateStartOfDayTime
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.chart.common.dimens.ChartDimens
import com.niyaj.feature.chart.horizontalbar.HorizontalBarChart
import com.niyaj.feature.chart.horizontalbar.axis.HorizontalAxisConfig
import com.niyaj.feature.chart.horizontalbar.config.HorizontalBarConfig
import com.niyaj.feature.chart.horizontalbar.config.StartDirection
import com.niyaj.feature.reports.components.AddressReportCard
import com.niyaj.feature.reports.components.CategoryWiseReportCard
import com.niyaj.feature.reports.components.CustomerReportCard
import com.niyaj.feature.reports.components.OrderTypeDropdown
import com.niyaj.feature.reports.destinations.ViewLastSevenDaysReportsDestination
import com.niyaj.model.Reports
import com.niyaj.ui.components.CountBox
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ReportBox
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.StandardOutlinedAssistChip
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.utils.Screens
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

    StandardScaffoldNew(
        navController = navController,
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
    ) {
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

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .padding(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceMedium)
        ) {
            item("reportBoxData") {
                Spacer(modifier = Modifier.height(SpaceMini))

                ReportBoxData(
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
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReportBoxData(
    report: Reports,
    onOrderClick: () -> Unit,
    onExpensesClick: () -> Unit,
    onRefreshReport: () -> Unit,
) {
    val totalAmount =
        report.expensesAmount.plus(report.dineInSalesAmount).plus(report.dineOutSalesAmount)
            .toString()

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(SpaceMedium, Alignment.CenterVertically),
    ) {
        ReportBox(
            title = "DineIn Sales",
            amount = report.dineInSalesAmount.toString(),
            icon = Icons.Default.RamenDining,
            onClick = onOrderClick,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )

        ReportBox(
            title = "DineOut Sales",
            amount = report.dineOutSalesAmount.toString(),
            icon = Icons.Default.DeliveryDining,
            onClick = onOrderClick,
            containerColor = MaterialTheme.colorScheme.errorContainer
        )

        ReportBox(
            title = "Expenses",
            amount = report.expensesAmount.toString(),
            icon = Icons.Default.Receipt,
            onClick = onExpensesClick,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )

        ReportBox(
            title = "Total Amount",
            amount = totalAmount,
            icon = Icons.Default.Money,
            enabled = false,
            onClick = {},
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    }

    Spacer(modifier = Modifier.height(SpaceMedium))

    StandardButton(
        modifier = Modifier.fillMaxWidth(),
        text = "Re-Generate Report",
        icon = Icons.Default.Sync,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
        onClick = onRefreshReport
    )
}

@Composable
fun ReportBarData(
    reportBarState: ReportsBarState,
    selectedBarData: String,
    onBarClick: (String) -> Unit,
    onClickViewMore: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Crossfade(
            targetState = reportBarState,
            label = "ReportBarState"
        ) { state ->
            when {
                state.isLoading -> LoadingIndicator()

                state.reportBarData.isNotEmpty() -> {
                    val reportBarData = state.reportBarData

                    Column(
                        modifier = Modifier
                            .padding(SpaceSmall)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            IconWithText(
                                text = "Last ${reportBarData.size} Days Reports",
                                icon = Icons.Default.AutoGraph,
                                secondaryText = selectedBarData.ifEmpty { null }
                            )

                            IconButton(
                                onClick = onClickViewMore
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowRightAlt,
                                    contentDescription = "View more"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(SpaceSmall))

                        HorizontalDivider(modifier = Modifier.fillMaxWidth())

                        Spacer(modifier = Modifier.height(SpaceSmall))

                        HorizontalBarChart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((reportBarData.size.times(60)).dp)
                                .padding(SpaceSmall),
                            onBarClick = {
                                onBarClick(
                                    "${it.yValue} - ${
                                        it.xValue.toString().substringBefore(".").toRupee
                                    }"
                                )
                            },
                            colors = listOf(
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.primaryContainer
                            ),
                            barDimens = ChartDimens(2.dp),
                            horizontalBarConfig = HorizontalBarConfig(
                                showLabels = false,
                                startDirection = StartDirection.Left,
                                productReport = false
                            ),
                            horizontalAxisConfig = HorizontalAxisConfig(
                                showAxes = true,
                                showUnitLabels = false
                            ),
                            horizontalBarData = reportBarData,
                        )
                    }
                }

                else -> {
                    ItemNotAvailable(
                        modifier = Modifier.padding(SpaceSmall),
                        text = state.error ?: "Reports are not available",
                        showImage = false,
                    )
                }
            }
        }
    }
}


@Composable
fun CategoryWiseReport(
    categoryState: CategoryWiseReportState,
    reportExpanded: Boolean,
    selectedCategory: String,
    onCategoryExpandChanged: (String) -> Unit,
    onExpandChanged: () -> Unit,
    onClickOrderType: (String) -> Unit,
    onProductClick: (productId: Int) -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors()
    ) {
        StandardExpandable(
            modifier = Modifier
                .padding(SpaceSmall)
                .fillMaxWidth(),
            expanded = reportExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                IconWithText(
                    text = "Category Wise Report",
                    icon = Icons.Default.Category
                )
            },
            trailing = {
                OrderTypeDropdown(
                    text = categoryState.orderType.ifEmpty { "All" }
                ) {
                    onClickOrderType(it)
                }
            },
            expand = null,
            contentDesc = "Category wise report",
            content = {
                Crossfade(
                    targetState = categoryState,
                    label = "CategoryState"
                ) { state ->
                    when {
                        state.isLoading -> LoadingIndicator()
                        state.categoryWiseReport.isNotEmpty() -> {
                            CategoryWiseReportCard(
                                report = state.categoryWiseReport,
                                selectedCategory = selectedCategory,
                                onExpandChanged = onCategoryExpandChanged,
                                onProductClick = onProductClick
                            )
                        }

                        else -> {
                            ItemNotAvailable(
                                text = state.hasError ?: "Category wise report not available",
                                showImage = false,
                            )
                        }
                    }
                }
            },
        )
    }
}


@Composable
fun ProductWiseReport(
    productState: ProductWiseReportState,
    productRepExpanded: Boolean,
    selectedProduct: String,
    onExpandChanged: () -> Unit,
    onClickOrderType: (String) -> Unit,
    onBarClick: (String) -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors()
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = productRepExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                IconWithText(
                    text = "Product Wise Report",
                    secondaryText = selectedProduct.ifEmpty { null },
                    icon = Icons.Default.Dns,
                )
            },
            trailing = {
                OrderTypeDropdown(
                    text = productState.orderType.ifEmpty { "All" },
                    onItemClick = onClickOrderType
                )
            },
            expand = null,
            contentDesc = "Product wise report",
            content = {
                Crossfade(
                    targetState = productState,
                    label = "ProductState"
                ) { state ->
                    when {
                        state.isLoading -> LoadingIndicator()

                        state.data.isNotEmpty() -> {
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            HorizontalBarChart(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((state.data.size.times(50)).dp)
                                    .padding(SpaceSmall),
                                onBarClick = {
                                    onBarClick(
                                        "${it.yValue} - ${
                                            it.xValue.toString().substringBefore(".")
                                        } Qty"
                                    )
                                },
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.secondaryContainer
                                ),
                                barDimens = ChartDimens(2.dp),
                                horizontalBarConfig = HorizontalBarConfig(
                                    showLabels = false,
                                    startDirection = StartDirection.Left
                                ),
                                horizontalAxisConfig = HorizontalAxisConfig(
                                    showAxes = true,
                                    showUnitLabels = false
                                ),
                                horizontalBarData = state.data,
                            )
                        }

                        else -> {
                            ItemNotAvailable(
                                text = state.error ?: "Product wise report not available",
                                showImage = false,
                            )
                        }
                    }
                }
            },
        )
    }
}


@Composable
fun AddressWiseReport(
    addressState: AddressWiseReportState,
    addressWiseRepExpanded: Boolean,
    onExpandChanged: () -> Unit,
    onAddressClick: (addressId: Int) -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors()
    ) {
        StandardExpandable(
            modifier = Modifier
                .padding(SpaceSmall)
                .fillMaxWidth(),
            expanded = addressWiseRepExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                IconWithText(
                    text = "Address Wise Report",
                    icon = Icons.Default.Business,
                )
            },
            trailing = {
                CountBox(count = addressState.reports.size.toString())
            },
            rowClickable = true,
            expand = null,
            contentDesc = "Address wise report",
            content = {
                Crossfade(targetState = addressState, label = "AddressState") { state ->
                    when {
                        state.isLoading -> LoadingIndicator()
                        state.reports.isNotEmpty() -> {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                state.reports.forEachIndexed { index, report ->
                                    AddressReportCard(
                                        report = report,
                                        onAddressClick = onAddressClick
                                    )

                                    if (index != state.reports.size - 1) {
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                    }
                                }
                            }
                        }

                        else -> {
                            ItemNotAvailable(
                                text = state.error ?: "Address wise report not available",
                                showImage = false
                            )
                        }
                    }
                }
            },
        )
    }
}


@Composable
fun CustomerWiseReport(
    customerState: CustomerWiseReportState,
    customerWiseRepExpanded: Boolean,
    onExpandChanged: () -> Unit,
    onCustomerClick: (Int) -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors()
    ) {
        StandardExpandable(
            modifier = Modifier
                .padding(SpaceSmall)
                .fillMaxWidth(),
            expanded = customerWiseRepExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                IconWithText(
                    text = "Customer Wise Report",
                    icon = Icons.Default.PeopleAlt,
                )
            },
            trailing = {
                CountBox(count = customerState.reports.size.toString())
            },
            rowClickable = true,
            expand = null,
            content = {
                Crossfade(targetState = customerState, label = "CustomerState") { state ->
                    when {
                        state.isLoading -> LoadingIndicator()
                        state.reports.isNotEmpty() -> {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                state.reports.forEachIndexed { index, report ->
                                    CustomerReportCard(
                                        customerReport = report,
                                        onClickCustomer = onCustomerClick
                                    )

                                    if (index != state.reports.size - 1) {
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                    }
                                }
                            }
                        }

                        else -> {
                            ItemNotAvailable(
                                text = "Customer wise report not available",
                                showImage = false
                            )
                        }
                    }
                }
            },
            contentDesc = "Customer wise report"
        )
    }
}