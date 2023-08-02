package com.niyaj.domain.utils

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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * BluetoothPrinter is a class that provides functionality for connecting to and interacting with a Bluetooth printer.
 * It allows retrieving available printers, connecting to specific printers, and printing data.
 *
 * @param printerRepository The repository for retrieving printer information.
 */
class BluetoothPrinter @Inject constructor(
    private val printerRepository: PrinterRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {

    private val _info = MutableStateFlow(Printer())
    val info = _info.asStateFlow()

    private val _printer = MutableStateFlow<EscPosPrinter?>(null)
    val printer = _printer.asStateFlow()

    private val _bluetoothConnection = MutableStateFlow<BluetoothConnection?>(null)

    private val bluetoothConnection: BluetoothConnection?
        get() = _bluetoothConnection.value

    private val connections = BluetoothPrintersConnections()

    init {
        runBlocking(ioDispatcher) {
            getPrinterInfo()
        }

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
        try {
            val device = connections.list?.find { it.device.address == address }
            device?.connect()

            _bluetoothConnection.value = device
            _printer.value = EscPosPrinter(
                device,
                _info.value.printerDpi,
                _info.value.printerWidth,
                _info.value.printerNbrLines
            )
        } catch (e: Exception) {
            Log.e("Bluetooth", "Failed to connect")
        }
    }

    private fun connectBluetoothPrinter() {
        try {
            val data = BluetoothPrintersConnections.selectFirstPaired()
            data?.connect()

            _bluetoothConnection.value = data
            _printer.value = EscPosPrinter(
                data,
                _info.value.printerDpi,
                _info.value.printerWidth,
                _info.value.printerNbrLines
            )
        } catch (e: Exception) {
            Log.e("Bluetooth", "Failed to connect")
        }
    }

    fun printTestData() {
        _printer.value?.printFormattedText("[C]<b><font size='big'>Testing</font></b> \n")
    }

    private suspend fun getPrinterInfo() {
        withContext(ioDispatcher) {
            _info.value = printerRepository.getPrinter()
        }
    }
}
