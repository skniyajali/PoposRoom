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

package com.niyaj.printerInfo.createOrUpdate

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.safeFloat
import com.niyaj.common.utils.safeInt
import com.niyaj.data.repository.PrinterRepository
import com.niyaj.data.repository.validation.PrinterValidationRepository
import com.niyaj.model.Printer
import com.niyaj.ui.utils.UiEvent
import com.samples.apps.core.analytics.AnalyticsEvent
import com.samples.apps.core.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UpdatePrinterInfoViewModel @Inject constructor(
    private val repository: PrinterRepository,
    private val validationRepository: PrinterValidationRepository,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    private val _state = mutableStateOf(Printer.defaultPrinterInfo)
    val state: Printer
        get() = _state.value

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getPrinterInfo()
    }

    val dpiError = snapshotFlow { _state.value.printerDpi }
        .mapLatest {
            validationRepository.validatePrinterDpi(it).errorMessage
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val widthError = snapshotFlow { _state.value.printerWidth }
        .mapLatest {
            validationRepository.validatePrinterWidth(it).errorMessage
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val nbrError = snapshotFlow { _state.value.printerNbrLines }
        .mapLatest {
            validationRepository.validatePrinterNbrLines(it).errorMessage
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val nameLengthError = snapshotFlow { _state.value.productNameLength }
        .mapLatest {
            validationRepository.validateProductNameLength(it).errorMessage
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val productLimitError = snapshotFlow { _state.value.productWiseReportLimit }
        .mapLatest {
            validationRepository.validateProductReportLimit(it).errorMessage
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val addressLimitError = snapshotFlow { _state.value.addressWiseReportLimit }
        .mapLatest {
            validationRepository.validateAddressReportLimit(it).errorMessage
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val customerLimitError = snapshotFlow { _state.value.customerWiseReportLimit }
        .mapLatest {
            validationRepository.validateCustomerReportLimit(it).errorMessage
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val hasError = combine(
        dpiError,
        widthError,
        nbrError,
        nameLengthError,
        productLimitError,
        addressLimitError,
        customerLimitError,
    ) { list ->
        list.any {
            it != null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)

    fun onEvent(event: UpdatePrinterInfoEvent) {
        when (event) {
            is UpdatePrinterInfoEvent.PrinterDpiChanged -> {
                _state.value = _state.value.copy(
                    printerDpi = event.printerDpi.safeInt(),
                )
            }

            is UpdatePrinterInfoEvent.PrinterNbrLinesChanged -> {
                _state.value = _state.value.copy(
                    printerNbrLines = event.printerNbrLines.safeInt(),
                )
            }

            is UpdatePrinterInfoEvent.PrinterWidthChanged -> {
                _state.value = _state.value.copy(
                    printerWidth = event.printerWidth.safeFloat(),
                )
            }

            is UpdatePrinterInfoEvent.ProductNameLengthChanged -> {
                _state.value = _state.value.copy(
                    productNameLength = event.length.safeInt(),
                )
            }

            is UpdatePrinterInfoEvent.ProductReportLimitChanged -> {
                _state.value = _state.value.copy(
                    productWiseReportLimit = event.limit.safeInt(),
                )
            }

            is UpdatePrinterInfoEvent.AddressReportLimitChanged -> {
                _state.value = _state.value.copy(
                    addressWiseReportLimit = event.limit.safeInt(),
                )
            }

            is UpdatePrinterInfoEvent.CustomerReportLimitChanged -> {
                _state.value = _state.value.copy(
                    customerWiseReportLimit = event.limit.safeInt(),
                )
            }

            is UpdatePrinterInfoEvent.PrintQrCodeChanged -> {
                _state.value = _state.value.copy(
                    printQRCode = !_state.value.printQRCode,
                )
            }

            is UpdatePrinterInfoEvent.PrintResLogoChanged -> {
                _state.value = _state.value.copy(
                    printResLogo = !_state.value.printResLogo,
                )
            }

            is UpdatePrinterInfoEvent.PrintWelcomeTextChanged -> {
                _state.value = _state.value.copy(
                    printWelcomeText = !_state.value.printWelcomeText,
                )
            }

            is UpdatePrinterInfoEvent.UpdatePrinterInfo -> {
                updatePrinterInfo()
            }
        }
    }

    private fun updatePrinterInfo() {
        viewModelScope.launch {
            if (!hasError.value) {
                when (repository.addOrUpdatePrinterInfo(_state.value)) {
                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.OnSuccess("Printer info updated successfully"))
                        analyticsHelper.updatedPrinterInfo()
                    }

                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError("Unable to update printer info"))
                    }
                }
            } else {
                return@launch
            }
        }
    }

    private fun getPrinterInfo() {
        viewModelScope.launch {
            repository.getPrinter().collectLatest {
                _state.value = _state.value.copy(
                    printerId = it.printerId,
                    printerDpi = it.printerDpi,
                    printerWidth = it.printerWidth,
                    printerNbrLines = it.printerNbrLines,
                    productNameLength = it.productNameLength,
                    productWiseReportLimit = it.productWiseReportLimit,
                    addressWiseReportLimit = it.addressWiseReportLimit,
                    customerWiseReportLimit = it.customerWiseReportLimit,
                    printQRCode = it.printQRCode,
                    printResLogo = it.printResLogo,
                    printWelcomeText = it.printWelcomeText,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt,
                )
            }
        }
    }
}

internal fun AnalyticsHelper.updatedPrinterInfo() {
    logEvent(
        event = AnalyticsEvent(
            type = "updated_printer_info",
            extras = listOf(
                AnalyticsEvent.Param("updated_printer_info", "Yes"),
            ),
        ),
    )
}
