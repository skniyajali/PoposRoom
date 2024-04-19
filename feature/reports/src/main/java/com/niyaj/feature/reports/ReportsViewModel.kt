package com.niyaj.feature.reports

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.calculateEndOfDayTime
import com.niyaj.common.utils.calculateStartOfDayTime
import com.niyaj.common.utils.getEndTime
import com.niyaj.common.utils.getStartTime
import com.niyaj.common.utils.toBarDate
import com.niyaj.common.utils.toFormattedDate
import com.niyaj.data.repository.ReportsRepository
import com.niyaj.feature.chart.horizontalbar.model.HorizontalBarData
import com.niyaj.feature.printer.bluetooth_printer.BluetoothPrinter
import com.niyaj.model.Reports
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ReportsViewModel
 * @author Sk Niyaj Ali
 */
@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val reportsRepository: ReportsRepository,
    bluetoothPrinter: BluetoothPrinter,
) : ViewModel() {

    private val escposPrinter = bluetoothPrinter.printer
    private val info = bluetoothPrinter.printerInfo.value

    private val _reportState = MutableStateFlow(ReportState())
    val reportState = _reportState.asStateFlow()

    private val _reportsBarData = MutableStateFlow(ReportsBarState())
    val reportsBarData = _reportsBarData.asStateFlow()

    private val _productWiseData = MutableStateFlow(ProductWiseReportState())
    val productWiseData = _productWiseData.asStateFlow()

    private val _categoryWiseData = MutableStateFlow(CategoryWiseReportState())
    val categoryWiseData = _categoryWiseData.asStateFlow()

    private val _customerWiseData = MutableStateFlow(CustomerWiseReportState())
    val customerWiseData = _customerWiseData.asStateFlow()

    private val _addressWiseData = MutableStateFlow(AddressWiseReportState())
    val addressWiseData = _addressWiseData.asStateFlow()

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedDate = MutableStateFlow("")
    val selectedDate = _selectedDate.asStateFlow()

    private val date = _selectedDate.value.ifEmpty { getStartTime }
    private val endTime = calculateEndOfDayTime(date = date)

    init {
        generateReport()
        getReport(date)
        getReportBarData(endTime)
        getProductWiseReport()
        getCategoryWiseReport()
        getAddressWiseReport()
        getCustomerWiseReport()
    }

    fun onReportEvent(event: ReportsEvent) {
        when (event) {
            is ReportsEvent.SelectDate -> {
                viewModelScope.launch {
                    val startDate = calculateStartOfDayTime(date = event.date)
                    val endDate = calculateEndOfDayTime(date = event.date)

                    _selectedDate.emit(startDate)

                    getReportBarData(endDate)
                    getReport(startDate)
                    getProductWiseReport(startDate, endDate)
                    getCategoryWiseReport(startDate, endDate)
                    getAddressWiseReport(startDate, endDate)
                    getCustomerWiseReport(startDate, endDate)
                }
            }

            is ReportsEvent.OnChangeOrderType -> {
                if (event.orderType != _productWiseData.value.orderType) {
                    val startDate =
                        calculateStartOfDayTime(date = _selectedDate.value.ifEmpty { getStartTime })
                    val endDate = calculateEndOfDayTime(date = startDate)

                    getProductWiseReport(startDate, endDate, orderType = event.orderType)
                }
            }

            is ReportsEvent.OnChangeCategoryOrderType -> {
                if (event.orderType != _categoryWiseData.value.orderType) {
                    val startDate =
                        calculateStartOfDayTime(date = _selectedDate.value.ifEmpty { getStartTime })
                    val endDate = calculateEndOfDayTime(date = startDate)

                    getCategoryWiseReport(startDate, endDate, orderType = event.orderType)
                }
            }

            is ReportsEvent.PrintReport -> {
                printAllReports()
            }

            is ReportsEvent.RefreshReport -> {
                _selectedDate.value = ""
                generateReport()
                getReport(date)
                getReportBarData(endTime)
                getProductWiseReport()
                getCategoryWiseReport()
                getAddressWiseReport()
                getCustomerWiseReport()
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

    private fun getReportBarData(selectedDate: String = "") {
        viewModelScope.launch {
            reportsRepository.getReports(selectedDate).collectLatest { list ->
                _reportsBarData.value = _reportsBarData.value.copy(
                    reportBarData = list.map {
                        HorizontalBarData(
                            xValue = it.dineInSalesAmount.plus(it.dineOutSalesAmount)
                                .plus(it.expensesAmount).toFloat(),
                            yValue = it.reportDate.toBarDate
                        )
                    }
                )
            }
        }
    }

    private fun getProductWiseReport(
        startDate: String = date,
        endDate: String = endTime,
        orderType: String = "",
    ) {
        viewModelScope.launch {
            reportsRepository.getProductWiseReport(startDate, endDate, orderType)
                .collectLatest { result ->
                    result.let { data ->
                        _productWiseData.value = _productWiseData.value.copy(
                            data = data.map {
                                HorizontalBarData(
                                    xValue = it.quantity.toFloat(),
                                    yValue = it.productName
                                )
                            },
                            orderType = orderType
                        )
                    }
                }
        }
    }

    private fun getCategoryWiseReport(
        startDate: String = date,
        endDate: String = endTime,
        orderType: String = "",
    ) {
        viewModelScope.launch {
            reportsRepository.getCategoryWiseReport(startDate, endDate, orderType).collectLatest {
                _categoryWiseData.value = _categoryWiseData.value.copy(
                    categoryWiseReport = it,
                    orderType = orderType
                )
            }
        }
    }

    private fun getCustomerWiseReport(
        startDate: String = date,
        endDate: String = endTime,
    ) {
        viewModelScope.launch {
            reportsRepository.getCustomerWiseReport(startDate, endDate).collectLatest { result ->
                result.let { data ->
                    _customerWiseData.value = _customerWiseData.value.copy(
                        reports = data
                    )
                }
            }
        }
    }

    private fun getAddressWiseReport(
        startDate: String = date,
        endDate: String = endTime,
    ) {
        viewModelScope.launch {
            reportsRepository.getAddressWiseReport(startDate, endDate).collectLatest { result ->
                result.let { data ->
                    _addressWiseData.value = _addressWiseData.value.copy(
                        reports = data
                    )
                }
            }
        }
    }

    private fun generateReport() {
        viewModelScope.launch {
            reportsRepository.generateReport(getStartTime, getEndTime)
        }
    }

    private fun getReport(startDate: String = date) {
        viewModelScope.launch {
            reportsRepository.getReportByReportDate(startDate).collectLatest { result ->
                val data = result ?: Reports()

                _reportState.value = _reportState.value.copy(report = data)
            }
        }
    }

    private fun printAllReports() {
        try {
            var printItems = ""

            printItems += getPrintableHeader()
            printItems += getPrintableBoxReport()
            printItems += getPrintableBarReport()
            printItems += getPrintableCategoryWiseReport()
            printItems += getPrintableAddressWiseReport()
            printItems += getPrintableCustomerWiseReport()

            escposPrinter.printFormattedText(printItems, info.printerWidth)
        } catch (e: Exception) {
            Log.d("Printer", e.message ?: "Error printing")
        }
    }

    private fun getPrintableHeader(): String {
        var header = "[C]<b><font size='big'>REPORTS</font></b>\n\n"

        header += if (selectedDate.value.isEmpty()) {
            "[C]--------- ${System.currentTimeMillis().toString().toFormattedDate} --------\n"
        } else {
            "[C]----------${_selectedDate.value.toFormattedDate}---------\n"
        }

        header += "[L]\n"

        return header
    }

    private fun getPrintableBoxReport(): String {
        val report = _reportState.value.report
        val totalAmount =
            report.expensesAmount.plus(report.dineInSalesAmount).plus(report.dineOutSalesAmount)
                .toString()

        var boxReport = "[C]TOTAL EXPENSES & SALES\n\n"
        boxReport += "[L]-------------------------------\n"
        boxReport += "[L]DineIn Sales(${report.dineInSalesQty})[R]${report.dineInSalesAmount}\n"
        boxReport += "[L]DineOut Sales(${report.dineOutSalesQty})[R]${report.dineOutSalesAmount}\n"
        boxReport += "[L]Expenses(${report.expensesQty})[R]${report.expensesAmount}\n"
        boxReport += "[L]-------------------------------\n"
        boxReport += "[L]Total - [R]${totalAmount}\n"
        boxReport += "[L]-------------------------------\n\n"


        return boxReport
    }

    private fun getPrintableBarReport(): String {
        var barReport = "[C]LAST ${_reportsBarData.value.reportBarData.size} DAYS REPORTS\n\n" +
                "[L]-------------------------------\n"

        if (_reportsBarData.value.reportBarData.isNotEmpty()) {
            val barData = _reportsBarData.value.reportBarData

            barData.forEach { data ->
                barReport += "[L]${data.yValue}[R]${data.xValue.toString().substringBefore(".")}\n"
            }
        } else {
            barReport += "[C]Reports not available. \n"
        }

        barReport += "[L]-------------------------------\n\n"

        return barReport
    }

    private fun getPrintableProductWiseReport(): String {
        var productReport = "[C]TOP SALES PRODUCTS\n\n"

        productReport += "[L]-------------------------------\n"

        if (_productWiseData.value.data.isNotEmpty()) {
            val productWiseData = _productWiseData.value.data.take(info.productWiseReportLimit)

            productReport += "[L]Name[R]Qty\n"
            productReport += "[L]-------------------------------\n"

            productWiseData.forEach { data ->
                productReport += "[L]${data.yValue}[R]${
                    data.xValue.toString().substringBefore(".")
                }\n"
            }
        } else {
            productReport += "[C]Product report is not available"
        }

        productReport += "[L]-------------------------------\n"

        return productReport
    }

    private fun getPrintableCategoryWiseReport(): String {
        var report = "[C]TOP SALES PRODUCTS\n\n"

        report += "[L]-------------------------------\n"


        val categoryWiseReports = _categoryWiseData.value.categoryWiseReport

        if (categoryWiseReports.isNotEmpty()) {
            categoryWiseReports.forEach { (category, products) ->
                if (products.isNotEmpty()) {
                    val totalQuantity = products.sumOf { it.quantity }.toString()

                    report += "[L]-------------------------------\n"
                    report += "[L]${category.categoryName} [R]${totalQuantity}\n"
                    report += "[L]-------------------------------\n"

                    products.forEachIndexed { _, product ->
                        report += "[L]${product.productName}[R]${product.quantity}\n"
                    }

                    report += "[L]-------------------------------\n"
                }
            }
        } else {
            report += "[C]Product Report Not Available \n"
        }

        report += "[L]-------------------------------\n\n"

        return report
    }

    private fun getPrintableAddressWiseReport(): String {
        var report = "[C]TOP DELIVERY PLACES\n\n"

        report += "[L]-------------------------------\n"

        val addresses = _addressWiseData.value.reports.take(info.addressWiseReportLimit)

        if (addresses.isNotEmpty()) {
            addresses.forEachIndexed { _, address ->

                report += "[L]${address.address.addressName} [R]${address.orderQty}\n"

//                if (index != addresses.size - 1){
//                    report += "[L]-------------------------------\n"
//                }
            }

        } else {
            report += "[C]Address Report Not Available \n"
        }

        report += "[L]-------------------------------\n\n"

        return report
    }

    private fun getPrintableCustomerWiseReport(): String {
        var report = "[C]MOST ORDERED CUSTOMERS\n\n"

        report += "[L]-------------------------------\n"

        val customers = _customerWiseData.value.reports.take(info.customerWiseReportLimit)

        if (customers.isNotEmpty()) {
            customers.forEachIndexed { _, customerWiseReport ->
                val name =
                    if (customerWiseReport.customer.customerName != null) customerWiseReport.customer.customerName else ""

                report += "[L]${customerWiseReport.customer.customerPhone}[C]${name?.take(12)} [R]${customerWiseReport.orderQty}\n"

//                if (index != customers.size -1){
//                    report += "[L]-------------------------------\n"
//                }
            }
        } else {
            report += "[C]Customer Report Not Available \n"
        }

        report += "[L]-------------------------------\n\n"

        return report
    }

}