package com.niyaj.printer_info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.feature.printer.bluetooth_printer.BluetoothPrinter
import com.niyaj.ui.event.UiState
import com.samples.apps.core.analytics.AnalyticsEvent
import com.samples.apps.core.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrinterInfoViewModel @Inject constructor(
    private val bluetoothPrinter: BluetoothPrinter,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    val info = bluetoothPrinter.printerInfo.mapLatest {
        if (it.printerId.isEmpty()) {
            UiState.Empty
        } else {
            UiState.Success(it)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        UiState.Loading
    )

    val printers = bluetoothPrinter.getBluetoothPrintersAsFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    fun printTestData() {
        viewModelScope.launch {
            bluetoothPrinter.printTestData()
            analyticsHelper.printedTestData()
        }
    }

    fun connectBluetoothPrinter(address: String) {
        viewModelScope.launch {
            bluetoothPrinter.connectBluetoothPrinter(address)
            analyticsHelper.connectedBluetoothPrinter(address)
        }
    }
}

internal fun AnalyticsHelper.printedTestData() {
    logEvent(
        event = AnalyticsEvent(
            type = "printed_test_data",
            extras = listOf(
                AnalyticsEvent.Param("printed_test_data", "Yes"),
            ),
        ),
    )
}

internal fun AnalyticsHelper.connectedBluetoothPrinter(address: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "connected_bluetooth_printer",
            extras = listOf(
                AnalyticsEvent.Param("connected_bluetooth_printer", address),
            ),
        ),
    )
}
