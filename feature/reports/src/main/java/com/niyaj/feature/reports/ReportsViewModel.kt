package com.niyaj.feature.reports

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.getStartTime
import com.niyaj.common.utils.toBarDate
import com.niyaj.data.repository.ReportsRepository
import com.niyaj.feature.chart.horizontalbar.model.HorizontalBarData
import com.niyaj.feature.printer.bluetooth_printer.BluetoothPrinter
import com.niyaj.model.TotalExpenses
import com.niyaj.model.TotalOrders
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.logScreenView
import com.samples.apps.core.analytics.AnalyticsEvent
import com.samples.apps.core.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ReportsViewModel
 * @author Sk Niyaj Ali
 */
@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val reportsRepository: ReportsRepository,
    private val bluetoothPrinter: BluetoothPrinter,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    private val info = bluetoothPrinter.printerInfo.value

    private val _selectedDate = MutableStateFlow(getStartTime)
    val selectedDate = _selectedDate.asStateFlow()

    private val _productOrderType = MutableStateFlow("")
    val productOrderType = _productOrderType.asStateFlow()

    private val _categoryOrderType = MutableStateFlow("")
    val categoryOrderType = _categoryOrderType.asStateFlow()

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _totalCustomerReports = MutableStateFlow(TotalOrders())
    val totalCustomerReports = _totalCustomerReports.asStateFlow()

    private val _totalAddressReports = MutableStateFlow(TotalOrders())
    val totalAddressReports = _totalAddressReports.asStateFlow()

    private val _totalExpensesReports = MutableStateFlow(TotalExpenses())
    val totalExpensesReports = _totalExpensesReports.asStateFlow()

    init {
        analyticsHelper.logScreenView(Screens.REPORT_SCREEN)
        generateReport()
    }

    // Get report on selected date
    val reportState = _selectedDate.flatMapLatest {
        reportsRepository.getReportByReportDate(it)
    }.map {
        UiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    // Get list of reports until selected date
    val reportsBarData = _selectedDate.flatMapLatest {
        reportsRepository.getReports()
    }.map { reports ->
        reports.map {
            HorizontalBarData(
                xValue = it.dineInSalesAmount.plus(it.dineOutSalesAmount)
                    .plus(it.expensesAmount).toFloat(),
                yValue = it.reportDate.toBarDate,
            )
        }
    }.map {
        if (it.isEmpty()) UiState.Empty else UiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    // Get product wise report by selected date and order type
    val productWiseData = _selectedDate.combine(_productOrderType) { date, orderType ->
        reportsRepository.getProductWiseReport(date, orderType)
    }.flatMapLatest { result ->
        result.map { data ->
            data.map {
                HorizontalBarData(
                    xValue = it.quantity.toFloat(),
                    yValue = it.productName,
                )
            }
        }
    }.map {
        if (it.isEmpty()) UiState.Empty else UiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    // Get category wise report on selected date and order type
    val categoryWiseData = _selectedDate.combine(_categoryOrderType) { date, orderType ->
        reportsRepository.getCategoryWiseReport(date, orderType)
    }.flatMapLatest { listFlow ->
        listFlow.map { it }
    }.map {
        if (it.isEmpty()) UiState.Empty else UiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    // Get address wise report on selected date
    val customerWiseData = _selectedDate.flatMapLatest {
        reportsRepository.getCustomerWiseReport(it)
    }.map { list ->
        if (list.isEmpty()) {
            _totalCustomerReports.update { TotalOrders() }
            UiState.Empty
        } else {
            _totalCustomerReports.update {
                it.copy(
                    totalOrders = list.size.toLong(),
                    totalAmount = list.sumOf { report -> report.totalSales.toLong() },
                )
            }
            UiState.Success(list)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    // Get address wise report on selected date
    val addressWiseData = _selectedDate.flatMapLatest {
        reportsRepository.getAddressWiseReport(it)
    }.map { list ->
        if (list.isEmpty()) {
            _totalAddressReports.update { TotalOrders() }
            UiState.Empty
        } else {
            _totalAddressReports.update {
                it.copy(
                    totalOrders = list.size.toLong(),
                    totalAmount = list.sumOf { report -> report.totalSales.toLong() },
                )
            }
            UiState.Success(list)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    // Get all the expenses reports on selected date
    val expensesReports = _selectedDate.flatMapLatest {
        reportsRepository.getExpensesReports(it)
    }.map { list ->
        if (list.isEmpty()) {
            _totalExpensesReports.update { TotalExpenses() }
            UiState.Empty
        } else {
            _totalExpensesReports.update {
                it.copy(
                    totalExpenses = list.sumOf { report -> report.expenseAmount.toLong() },
                    totalQuantity = list.size.toLong(),
                )
            }
            UiState.Success(list)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    fun onReportEvent(event: ReportsEvent) {
        when (event) {
            is ReportsEvent.SelectDate -> {
                viewModelScope.launch {
                    if (_selectedDate.value != event.date) {
                        _selectedDate.update { event.date }
                        generateReport(event.date)
                    }
                }
            }

            is ReportsEvent.OnChangeProductOrderType -> {
                viewModelScope.launch {
                    if (event.orderType != _productOrderType.value) {
                        _productOrderType.update { event.orderType }
                    }
                }
            }

            is ReportsEvent.OnChangeCategoryOrderType -> {
                viewModelScope.launch {
                    if (event.orderType != _categoryOrderType.value) {
                        _categoryOrderType.update { event.orderType }
                    }
                }
            }

            is ReportsEvent.PrintReport -> {
                printAllReports()
            }

            is ReportsEvent.OnSelectCategory -> {
                viewModelScope.launch {
                    if (_selectedCategory.value == event.categoryName) {
                        _selectedCategory.emit("")
                    } else {
                        _selectedCategory.emit(event.categoryName)
                    }
                }
            }

            is ReportsEvent.GenerateReport -> {
                generateReport()
            }
        }
    }

    private fun generateReport(reportDate: String = _selectedDate.value) {
        viewModelScope.launch {
            reportsRepository.generateReport(reportDate)
            analyticsHelper.logRegenerateReport()
        }
    }

    private fun printAllReports() {
        viewModelScope.launch {
            try {
                bluetoothPrinter.connectBluetoothPrinter()
                val escposPrinter = bluetoothPrinter.printer

                escposPrinter?.let { printer ->
                    var printItems = ""

                    printItems += bluetoothPrinter.getPrintableHeader(
                        title = "REPORTS",
                        _selectedDate.value,
                    )
                    printItems += getPrintableBoxReport() + "\n"
                    printItems += getPrintableBarReport() + "\n"
                    printItems += getPrintableCategoryWiseReport() + "\n"
                    printItems += getPrintableAddressWiseReport() + "\n"
                    printItems += getPrintableCustomerWiseReport() + "\n"
                    printItems += getPrintableExpensesReport() + "\n"
                    printItems += "[L]-------------------------------\n"
                    printItems += "[C]{^..^}--END OF REPORTS--{^..^}\n"
                    printItems += "[L]-------------------------------\n"

                    printer.printFormattedTextAndCut(printItems, 10f)
                    analyticsHelper.logPrintReport()
                } ?: return@launch
            } catch (e: Exception) {
                Log.d("Printer", e.message ?: "Error printing")
            }
        }
    }

    private fun getPrintableBoxReport(): String {
        var printableString = ""
        val report = (reportState.value as UiState.Success).data

        val totalAmount =
            report.expensesAmount.plus(report.dineInSalesAmount).plus(report.dineOutSalesAmount)
                .toString()

        printableString += "[L]-------------------------------\n"
        printableString += "[C]TOTAL EXPENSES & SALES\n"
        printableString += "[L]-------------------------------\n"

        printableString += "[L]DineIn Sales(${report.dineInSalesQty})[R]Rs.${report.dineInSalesAmount}\n"
        printableString += "[L]DineOut Sales(${report.dineOutSalesQty})[R]Rs.${report.dineOutSalesAmount}\n"
        printableString += "[L]Expenses(${report.expensesQty})[R]Rs.${report.expensesAmount}\n"

        printableString += "[L]-------------------------------\n"
        printableString += "[L]Total - [R]Rs.₹${totalAmount}\n"
        printableString += "[L]-------------------------------\n"


        return printableString
    }

    private fun getPrintableBarReport(): String {
        var printableString = ""

        val reports = try {
            (reportsBarData.value as UiState.Success).data
        } catch (e: Exception) {
            emptyList()
        }

        printableString += "[L]-------------------------------\n"
        printableString += "[C]LAST ${reports.size} DAYS REPORTS\n"
        printableString += "[L]-------------------------------\n"

        if (reports.isNotEmpty()) {
            reports.forEach { data ->
                printableString += "[L]${data.yValue}[R]Rs.${
                    data.xValue.toString().substringBefore(".")
                }\n"
            }
        } else {
            printableString += "[C]Reports not available. \n"
        }

        printableString += "[L]-------------------------------\n"

        return printableString
    }

    private fun getPrintableCategoryWiseReport(): String {
        var printableString = ""

        printableString += "[L]-------------------------------\n"
        printableString += "[C]TOP SALES PRODUCTS\n"


        val categoryWiseReports = try {
            (categoryWiseData.value as UiState.Success).data
        } catch (e: Exception) {
            emptyList()
        }

        if (categoryWiseReports.isNotEmpty()) {
            categoryWiseReports.forEach { (categoryName, products) ->
                if (products.isNotEmpty()) {
                    val totalQuantity = products.sumOf { it.quantity }.toString()

                    printableString += "[L]-------------------------------\n"
                    printableString += "[L]${categoryName} [R]${totalQuantity}\n"
                    printableString += "[L]-------------------------------\n"

                    products.forEachIndexed { _, product ->
                        printableString += "[L]${product.productName}[R]${product.quantity}\n"
                    }

//                    printableString += "[L]\n"
                }
            }
        } else {
            printableString += "[L]-------------------------------\n"
            printableString += "[C]Product Report Not Available \n"
        }

        printableString += "[L]-------------------------------\n"

        return printableString
    }

    private fun getPrintableAddressWiseReport(): String {
        var printableString = ""
        val totalOrders = _totalAddressReports.value

        printableString += "[L]-------------------------------\n"
        printableString += "[L]TOP PLACES[R]Rs.${totalOrders.totalAmount} | ${totalOrders.totalOrders}\n"
        printableString += "[L]-------------------------------\n"

        val addresses = try {
            (addressWiseData.value as UiState.Success).data.take(info.addressWiseReportLimit)
        } catch (e: Exception) {
            emptyList()
        }

        if (addresses.isNotEmpty()) {
            addresses.forEachIndexed { _, report ->
                printableString += "[L]${report.addressName}[C]${report.totalOrders}[R]Rs.${report.totalSales}\n"
            }
        } else {
            printableString += "[C]Address Report Not Available \n"
        }

        printableString += "[L]-------------------------------\n"

        return printableString
    }

    private fun getPrintableCustomerWiseReport(): String {
        var report = ""
        val totalOrders = _totalCustomerReports.value

        report += "[L]-------------------------------\n"
        report += "[L]TOP CUSTOMERS[R]Rs.${totalOrders.totalAmount} | ${totalOrders.totalOrders}\n"
        report += "[L]-------------------------------\n"

        val customers = try {
            (customerWiseData.value as UiState.Success).data.take(info.customerWiseReportLimit)
        } catch (e: Exception) {
            emptyList()
        }

        if (customers.isNotEmpty()) {
            customers.forEachIndexed { _, data ->
                report += "[L]${data.customerPhone}[C]${data.totalOrders}[R]Rs.${data.totalSales} \n"
            }
        } else {
            report += "[C]Customer Report Not Available \n"
        }

        report += "[L]-------------------------------\n"

        return report
    }

    private fun getPrintableExpensesReport(): String {
        val totalExpenses = _totalExpensesReports.value
        var printableText = ""

        printableText += "[L]-------------------------------\n"
        printableText += "[L]TOTAL EXPENSES[R]Rs.${totalExpenses.totalExpenses} | ${totalExpenses.totalQuantity}\n"
        printableText += "[L]-------------------------------\n"

        val reports = try {
            (expensesReports.value as UiState.Success).data.take(10)
        } catch (e: Exception) {
            emptyList()
        }

        if (reports.isNotEmpty()) {
            reports.forEachIndexed { _, data ->

                printableText += "[L]${data.expenseName} [R]Rs.${data.expenseAmount}\n"

//                if (index != customers.size -1){
//                    report += "[L]-------------------------------\n"
//                }
            }
        } else {
            printableText += "[C]Expenses Report Not Available \n"
        }

        printableText += "[L]-------------------------------\n"

        return printableText
    }
}

private fun AnalyticsHelper.logRegenerateReport() {
    logEvent(
        event = AnalyticsEvent(
            type = "report_generated",
            extras = listOf(
                AnalyticsEvent.Param("report_generated", "true"),
            ),
        ),
    )
}

private fun AnalyticsHelper.logPrintReport() {
    logEvent(
        event = AnalyticsEvent(
            type = "report_printed",
            extras = listOf(
                AnalyticsEvent.Param("report_printed", "true"),
            ),
        ),
    )
}
