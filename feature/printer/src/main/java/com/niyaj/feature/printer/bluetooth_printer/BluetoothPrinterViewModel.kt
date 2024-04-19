package com.niyaj.feature.printer.bluetooth_printer

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.niyaj.data.repository.PrinterRepository
import com.niyaj.model.BluetoothDeviceState
import com.niyaj.model.Printer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BluetoothPrinterViewModel @Inject constructor(
    repository: PrinterRepository,
) : ViewModel() {

    private val printerInfo = repository
        .getPrinter(Printer.defaultPrinterInfo.printerId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Printer.defaultPrinterInfo
        )

    private val _printer = MutableStateFlow(defaultPrinter())
    val printer = _printer.asStateFlow()

    private val _bluetoothConnection = MutableStateFlow<BluetoothConnection?>(null)

    private val bluetoothConnection: BluetoothConnection?
        get() = _bluetoothConnection.value

    private val connections = BluetoothPrintersConnections()

    init {
        connectBluetoothPrinter()
    }

    @SuppressLint("MissingPermission")
    fun getBluetoothPrinters(): Flow<List<BluetoothDeviceState>> {
        return channelFlow {
            try {
                val list = connections.list?.map {
                    BluetoothDeviceState(
                        name = it.device.name,
                        address = it.device.address,
                        bondState = it.device.bondState,
                        type = it.device.type,
                        connected = it.isConnected,
                    )
                }

                list?.let {
                    send(it)
                }
            } catch (e: Exception) {
                send(emptyList())
            }
        }
    }

    fun connectBluetoothPrinter(address: String) {
        viewModelScope.launch {
            try {
                val device = connections.list?.find { it.device.address == address }
                device?.connect()

                _bluetoothConnection.value = device
                _printer.value = EscPosPrinter(
                    device,
                    printerInfo.value.printerDpi,
                    printerInfo.value.printerWidth,
                    printerInfo.value.printerNbrLines
                )
            } catch (e: Exception) {
                Log.e("Bluetooth", "Failed to connect")
            }
        }
    }

    private fun connectBluetoothPrinter() {
        viewModelScope.launch {
            try {
                val data = BluetoothPrintersConnections.selectFirstPaired()
                data?.connect()

                _bluetoothConnection.value = data
                _printer.value = EscPosPrinter(
                    data,
                    printerInfo.value.printerDpi,
                    printerInfo.value.printerWidth,
                    printerInfo.value.printerNbrLines
                )
            } catch (e: Exception) {
                Log.e("Bluetooth", "Failed to connect")
            }
        }
    }

    fun printTestData() {
        viewModelScope.launch {
            try {
                _printer.value.printFormattedText("[C]<b><font size='big'>Testing</font></b> \n")
            } catch (e: Exception) {
                Log.e("Bluetooth", "Failed to print")
            }
        }
    }

    companion object {
        internal fun defaultPrinter(): EscPosPrinter {
            val data = BluetoothPrintersConnections.selectFirstPaired()
            if (data?.isConnected == false) {
                data.connect()
            }

            return EscPosPrinter(
                data,
                Printer.defaultPrinterInfo.printerDpi,
                Printer.defaultPrinterInfo.printerWidth,
                Printer.defaultPrinterInfo.printerNbrLines
            )
        }
    }
}