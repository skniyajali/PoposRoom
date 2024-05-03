package com.niyaj.feature.printer.bluetooth_printer

import android.annotation.SuppressLint
import android.util.Log
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.repository.PrinterRepository
import com.niyaj.model.BluetoothDeviceState
import com.niyaj.model.Printer
import com.niyaj.ui.utils.UiEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class BluetoothPrinter @Inject constructor(
    private val repository: PrinterRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {
    private val _printerInfo = MutableStateFlow(Printer.defaultPrinterInfo)
    val printerInfo = _printerInfo.asStateFlow()

    private val bluetoothConnections by lazy { BluetoothPrintersConnections() }

    var printer: EscPosPrinter? = null

    val mEventFlow = MutableSharedFlow<UiEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    init {
        fetchPrinterInfo()
    }

    @SuppressLint("MissingPermission")
    fun getBluetoothPrintersAsFlow(): Flow<List<BluetoothDeviceState>> {
        return channelFlow {
            try {
                val data = bluetoothConnections.list?.map {
                    BluetoothDeviceState(
                        name = it.device.name,
                        address = it.device.address,
                        bondState = it.device.bondState,
                        type = it.device.type,
                        connected = it.isConnected,
                    )
                }

                data?.let { send(it) }
            } catch (e: IOException) {
                mEventFlow.tryEmit(UiEvent.OnError("Unable to connect printer"))
                send(emptyList())
            } catch (e: EscPosConnectionException) {
                mEventFlow.tryEmit(UiEvent.OnError("Unable to connect printer"))
                send(emptyList())
            } catch (e: Exception) {
                mEventFlow.tryEmit(UiEvent.OnError("Unable to connect printer"))
                send(emptyList())
            }
        }
    }

    fun connectBluetoothPrinter(address: String) {
        try {
            val device = bluetoothConnections.list?.find { it.device.address == address }
            device?.connect()

            printer = EscPosPrinter(
                device,
                _printerInfo.value.printerDpi,
                _printerInfo.value.printerWidth,
                _printerInfo.value.printerNbrLines,
            )
        } catch (e: Exception) {
            when (e) {
                is IOException -> {
                    mEventFlow.tryEmit(UiEvent.OnError("Unable to connect printer"))
                }

                is EscPosConnectionException -> {
                    mEventFlow.tryEmit(UiEvent.OnError("Unable to connect printer"))
                }

                else -> {
                    Log.e("Print Exception", e.message ?: "Unable to print")
                    mEventFlow.tryEmit(UiEvent.OnError("Something went wrong, please try again later."))
                }
            }
        }
    }

    fun connectBluetoothPrinter() {
        val bluetoothConnections = try {
            BluetoothPrintersConnections.selectFirstPaired()
        } catch (e: IOException) {
            null
        } catch (e: EscPosConnectionException) {
            null
        } catch (e: Exception) {
            null
        }

        bluetoothConnections?.let { data ->
            try {
                printer = EscPosPrinter(
                    data,
                    _printerInfo.value.printerDpi,
                    _printerInfo.value.printerWidth,
                    _printerInfo.value.printerNbrLines,
                )
            } catch (e: IOException) {
                mEventFlow.tryEmit(UiEvent.OnError("Unable to connect printer"))
            } catch (e: EscPosConnectionException) {
                mEventFlow.tryEmit(UiEvent.OnError("Unable to connect printer"))
            } catch (e: Exception) {
                mEventFlow.tryEmit(UiEvent.OnError("Unable to connect printer"))
            }
        }
    }

    suspend fun connectBluetoothPrinterAsync() {
        coroutineScope {
            kotlin.runCatching {
                val bluetoothConnections = try {
                    BluetoothPrintersConnections.selectFirstPaired()
                } catch (e: IOException) {
                    null
                } catch (e: EscPosConnectionException) {
                    null
                } catch (e: Exception) {
                    null
                }

                bluetoothConnections?.let { data ->
                    try {
                        printer = EscPosPrinter(
                            data,
                            _printerInfo.value.printerDpi,
                            _printerInfo.value.printerWidth,
                            _printerInfo.value.printerNbrLines,
                        )
                    } catch (e: IOException) {
                        null
                    } catch (e: EscPosConnectionException) {
                        null
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        }
    }

    fun printTestData() {
        try {
            printer?.printFormattedText("[C]<b><font size='big'>Testing</font></b> \n")
        } catch (e: Exception) {
            mEventFlow.tryEmit(UiEvent.OnError("Failed to print"))
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
}