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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.feature.printer.bluetoothPrinter.BluetoothPrinter
import com.niyaj.ui.event.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrinterInfoViewModel @Inject constructor(
    private val bluetoothPrinter: BluetoothPrinter,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    val eventFlow = bluetoothPrinter.eventFlow.shareIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
    )

    val info = bluetoothPrinter.printerInfo.mapLatest {
        if (it.printerId.isEmpty()) {
            UiState.Empty
        } else {
            UiState.Success(it)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        UiState.Loading,
    )

    val printers = bluetoothPrinter.getBluetoothPrintersAsFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList(),
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
                AnalyticsEvent.Param(
                    "connected_bluetooth_printer",
                    address,
                ),
            ),
        ),
    )
}
