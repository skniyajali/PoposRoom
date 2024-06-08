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

package com.niyaj.feature.printer.bluetoothPrinter

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.niyaj.common.network.di.ApplicationScope
import com.niyaj.common.utils.toFormattedDate
import com.niyaj.data.repository.PrinterRepository
import com.niyaj.data.repository.UserDataRepository
import com.niyaj.feature.printer.bluetoothPrinter.utils.FileExtension.getImageFromDeviceOrDefault
import com.niyaj.model.BluetoothDeviceState
import com.niyaj.model.EmployeeNameAndId
import com.niyaj.model.Printer
import com.niyaj.model.Profile
import com.niyaj.ui.utils.UiEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.io.IOException
import javax.inject.Inject

class BluetoothPrinter @Inject constructor(
    repository: PrinterRepository,
    userDataRepository: UserDataRepository,
    private val application: Application,
    @ApplicationScope
    private val externalScope: CoroutineScope,
) {
    private val bluetoothConnections by lazy { BluetoothPrintersConnections() }

    private val profileInfo = userDataRepository.loggedInUserId.flatMapLatest {
        repository.getProfileInfo(it)
    }.stateIn(
        scope = externalScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Profile.defaultProfileInfo,
    )

    val printerInfo = repository.getPrinter(Printer.PRINTER_ID).stateIn(
        scope = externalScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Printer.defaultPrinterInfo,
    )

    var printer: EscPosPrinter? = null

    val mEventFlow = MutableSharedFlow<UiEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

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
                printerInfo.value.printerDpi,
                printerInfo.value.printerWidth,
                printerInfo.value.printerNbrLines,
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
                    printerInfo.value.printerDpi,
                    printerInfo.value.printerWidth,
                    printerInfo.value.printerNbrLines,
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

    fun connectBluetoothPrinterAsync() {
        externalScope.runCatching {
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
                        printerInfo.value.printerDpi,
                        printerInfo.value.printerWidth,
                        printerInfo.value.printerNbrLines,
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

    fun printTestData() {
        try {
            printer?.printFormattedText("[C]<b><font size='big'>Testing</font></b> \n")
        } catch (e: Exception) {
            mEventFlow.tryEmit(UiEvent.OnError("Failed to print"))
            Log.e("Bluetooth", "Failed to print", e)
        }
    }

    fun getPrintableRestaurantDetails(posPrinter: EscPosPrinter? = printer): String {
        var details = ""

        try {
            val logo = application.getImageFromDeviceOrDefault(profileInfo.value.printLogo)

            logo?.let {
                val imagePrint =
                    PrinterTextParserImg.bitmapToHexadecimalString(posPrinter, it)

                imagePrint?.let {
                    details += if (printerInfo.value.printResLogo) {
                        "[C]<img>$imagePrint</img>\n\n"
                    } else {
                        " \n"
                    }
                }
            }
        } catch (e: Exception) {
            return " \n"
        }

        return details
    }

    fun getPrintableQrCode(slogan: String = DEFAULT_SLOGAN): String {
        return if (printerInfo.value.printQRCode) {
            "[C]Pay by scanning this QR code\n\n" +
                "[L]\n" +
                "[C]<qrcode size ='40'>${profileInfo.value.paymentQrCode}</qrcode>\n\n\n" +
                "[C]$slogan \n\n" +
                "[L]-------------------------------\n"
        } else {
            ""
        }
    }

    fun getPrintableQrCode(
        usePartnerQr: Boolean,
        partner: EmployeeNameAndId? = null,
        slogan: String = DEFAULT_SLOGAN,
    ): String {
        val data: String = partner?.let {
            if (usePartnerQr) {
                if (partner.partnerQRCode.isNullOrEmpty()) profileInfo.value.paymentQrCode else partner.partnerQRCode
            } else {
                profileInfo.value.paymentQrCode
            }
        } ?: profileInfo.value.paymentQrCode

        val name = partner?.let {
            if (usePartnerQr) {
                if (partner.partnerQRCode.isNullOrEmpty()) profileInfo.value.name else partner.employeeName
            } else {
                profileInfo.value.name
            }
        } ?: profileInfo.value.name

        return if (printerInfo.value.printQRCode) {
            "[C]Pay by scanning this QR code\n" +
                "[C]${name}\n" +
                "[L]\n" +
                "[C]<qrcode size ='40'>$data</qrcode>\n\n\n" +
                "[C]$slogan \n\n" +
                "[L]-------------------------------\n"
        } else {
            ""
        }
    }

    fun getPrintableFooterInfo(): String {
        return if (printerInfo.value.printWelcomeText) {
            "[C]Thank you for ordering!\n" +
                "[C]For order and inquiry, Call.\n" +
                "[C]${profileInfo.value.primaryPhone} / ${profileInfo.value.secondaryPhone}\n\n"
        } else {
            ""
        }
    }

    fun getPrintableHeader(title: String, date: String): String {
        var header = "[C]<b><font size='big'>$title</font></b>\n\n"

        header += if (date.isEmpty()) {
            "[C]--------- ${System.currentTimeMillis().toString().toFormattedDate} --------\n"
        } else {
            "[C]----------${date.toFormattedDate}---------\n"
        }

        header += "[L]\n"

        return header
    }

    companion object {
        const val DEFAULT_SLOGAN = "Good Food, Good Mood"
    }
}
