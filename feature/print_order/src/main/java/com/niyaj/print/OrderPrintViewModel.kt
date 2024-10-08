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

package com.niyaj.print

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.data.utils.WorkMonitor
import com.niyaj.feature.printer.bluetoothPrinter.BluetoothPrinter
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A ViewModel that handles printing orders and delivery reports.
 *
 * @param bluetoothPrinter The Bluetooth printer for printing orders.
 */
@HiltViewModel
class OrderPrintViewModel @Inject constructor(
    private val bluetoothPrinter: BluetoothPrinter,
    private val workMonitor: WorkMonitor,
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            bluetoothPrinter
                .connectAndGetBluetoothPrinterAsync()
                .onSuccess {
                    _eventFlow.emit(UiEvent.OnSuccess("Printer Connected"))
                }
                .onFailure {
                    _eventFlow.emit(UiEvent.OnError("Unable to Connect Printer"))
                }
        }
    }

    fun onPrintEvent(event: PrintEvent) {
        when (event) {
            is PrintEvent.PrintOrder -> {
                viewModelScope.launch {
                    try {
                        bluetoothPrinter.printOrder(event.orderId)
                    } catch (e: Exception) {
                        _eventFlow.emit(UiEvent.OnError("Printer Not Connected"))
                    }
                }
            }

            is PrintEvent.PrintOrders -> {
                viewModelScope.launch {
                    try {
                        bluetoothPrinter.printOrders(event.orderIds)
                    } catch (e: Exception) {
                        _eventFlow.emit(UiEvent.OnError("Printer Not Connected"))
                    }
                }
            }

            is PrintEvent.PrintAllExpenses -> {
            }

            is PrintEvent.PrintDeliveryReport -> {
                viewModelScope.launch {
                    try {
                        bluetoothPrinter.printDeliveryReport(event.date, event.partnerId)
                    } catch (e: Exception) {
                        _eventFlow.emit(UiEvent.OnError("Printer Not Connected"))
                    }
                }
            }
        }
    }

    fun enqueuePrintOrderWorker(orderId: Int) {
        viewModelScope.launch {
            try {
                workMonitor.enqueuePrintOrderWorker(orderId)
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.OnError("Printer Not Connected"))
            }
        }
    }
}
