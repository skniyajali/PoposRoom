package com.niyaj.feature.printer.bluetooth_printer

import android.annotation.SuppressLint
import android.util.Log
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.repository.PrinterRepository
import com.niyaj.model.BluetoothDeviceState
import com.niyaj.model.Printer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class BluetoothPrinter @Inject constructor(
    private val repository: PrinterRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {
    private val _printerInfo = MutableStateFlow(Printer.defaultPrinterInfo)
    val printerInfo = _printerInfo.asStateFlow()

    private val connections = BluetoothPrintersConnections()

    private var bluetoothConnection: BluetoothConnection? = null

    var printer: EscPosPrinter = defaultPrinter()

    init {
        fetchPrinterInfo()
        connectBluetoothPrinter()
    }

    @SuppressLint("MissingPermission")
    fun getBluetoothPrintersAsFlow(): Flow<List<BluetoothDeviceState>> {
        return channelFlow {
            try {
                val data = connections.list?.map {
                    BluetoothDeviceState(
                        name = it.device.name,
                        address = it.device.address,
                        bondState = it.device.bondState,
                        type = it.device.type,
                        connected = it.isConnected
                    )
                }

                data?.let { send(it) }
            } catch (e: Exception) {
                send(emptyList())
            }
        }
    }

    fun connectBluetoothPrinter(address: String) {
        try {
            val device = connections.list?.find { it.device.address == address }
            device?.connect()

            bluetoothConnection = device
            printer = EscPosPrinter(
                device,
                _printerInfo.value.printerDpi,
                _printerInfo.value.printerWidth,
                _printerInfo.value.printerNbrLines
            )
        } catch (e: Exception) {
            Log.e("Bluetooth", "Failed to connect", e)
        }
    }

    private fun connectBluetoothPrinter() {
        try {
            val data = BluetoothPrintersConnections.selectFirstPaired()
            data?.connect()

            bluetoothConnection = data
            printer = EscPosPrinter(
                data,
                _printerInfo.value.printerDpi,
                _printerInfo.value.printerWidth,
                _printerInfo.value.printerNbrLines
            )
        } catch (e: Exception) {
            Log.e("Bluetooth", "Failed to connect", e)
        }
    }

    fun printTestData() {
        try {
            printer.printFormattedText("[C]<b><font size='big'>Testing</font></b> \n")
        } catch (e: Exception) {
            Log.e("Bluetooth", "Failed to print", e)
        }
    }

    private fun fetchPrinterInfo() {
        CoroutineScope(ioDispatcher).launch {
            repository.getPrinter(Printer.defaultPrinterInfo.printerId)
                .collect { printer ->
                    _printerInfo.value = printer
                }
        }
    }

    companion object {
        fun defaultPrinter(): EscPosPrinter {
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