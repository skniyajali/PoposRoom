package com.niyaj.printer_info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.data.repository.PrinterRepository
import com.niyaj.domain.utils.BluetoothPrinter
import com.niyaj.ui.event.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrinterInfoViewModel @Inject constructor(
    repository: PrinterRepository,
    private val bluetoothPrinter: BluetoothPrinter,
) : ViewModel() {

    val info = bluetoothPrinter.info
        .mapLatest {
            if (it.printerId.isEmpty()) {
                UiState.Empty
            } else {
                UiState.Success(it)
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UiState.Loading
        )

    val printers = bluetoothPrinter.getBluetoothPrinters().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    fun connectPrinter(address: String) {
        viewModelScope.launch {
            bluetoothPrinter.connectBluetoothPrinter(address)
        }
    }

    fun testPrint() {
        viewModelScope.launch {
            bluetoothPrinter.printTestData()
        }
    }

}