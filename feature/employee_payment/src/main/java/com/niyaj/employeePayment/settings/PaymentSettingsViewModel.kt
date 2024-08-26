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

package com.niyaj.employeePayment.settings

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.PaymentRepository
import com.niyaj.model.EmployeeWithPayments
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentSettingsViewModel @Inject constructor(
    private val repository: PaymentRepository,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel() {

    val items = snapshotFlow { mSearchText.value }.flatMapLatest {
        repository.getAllEmployeePayments(it)
    }.mapLatest { list ->
        totalItems = list.flatMap { item -> item.payments.map { it.paymentId } }
        if (list.all { it.payments.isEmpty() }) emptyList() else list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    private val _exportedItems = MutableStateFlow<List<EmployeeWithPayments>>(emptyList())
    val exportedItems = _exportedItems.asStateFlow()

    private val _importedItems = MutableStateFlow<List<EmployeeWithPayments>>(emptyList())
    val importedItems = _importedItems.asStateFlow()

    fun onEvent(event: PaymentSettingsEvent) {
        when (event) {
            is PaymentSettingsEvent.GetExportedItems -> {
                viewModelScope.launch {
                    if (mSelectedItems.isEmpty()) {
                        _exportedItems.value = items.value
                    } else {
                        val list = mutableListOf<EmployeeWithPayments>()

                        mSelectedItems.forEach { paymentId ->
                            items.value.find { list ->
                                list.payments.any { it.paymentId == paymentId }
                            }?.let {
                                list.add(it)
                            }
                        }

                        _exportedItems.emit(list.toList())
                    }

                    analyticsHelper.logExportedPaymentToFile(_exportedItems.value.size)
                }
            }

            is PaymentSettingsEvent.OnImportPaymentsFromFile -> {
                viewModelScope.launch {
                    _importedItems.value = emptyList()

                    if (event.data.isNotEmpty()) {
                        totalItems =
                            event.data.flatMap { item -> item.payments.map { it.paymentId } }
                        _importedItems.value = event.data
                    }

                    analyticsHelper.logImportedPaymentFromFile(event.data.size)
                }
            }

            is PaymentSettingsEvent.ImportPaymentsToDatabase -> {
                viewModelScope.launch {
                    val data = if (mSelectedItems.isNotEmpty()) {
                        mSelectedItems.flatMap { paymentId ->
                            _importedItems.value.filter { employeeWithAbsents ->
                                employeeWithAbsents.payments.any { it.paymentId == paymentId }
                            }
                        }
                    } else {
                        _importedItems.value
                    }

                    when (val result = repository.importPaymentsToDatabase(data)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                        }

                        is Resource.Success -> {
                            mEventFlow.emit(
                                UiEvent.OnSuccess(
                                    "${data.sumOf { it.payments.size }} payments has been imported successfully",
                                ),
                            )
                            analyticsHelper.logImportedPaymentToDatabase(data.sumOf { it.payments.size })
                        }
                    }
                }
            }
        }
    }
}

internal fun AnalyticsHelper.logImportedPaymentFromFile(totalPayment: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "payment_imported_from_file",
            extras = listOf(
                AnalyticsEvent.Param("payment_imported_from_file", totalPayment.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logImportedPaymentToDatabase(totalPayment: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "payment_imported_to_database",
            extras = listOf(
                AnalyticsEvent.Param("payment_imported_to_database", totalPayment.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logExportedPaymentToFile(totalPayment: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "payment_exported_to_file",
            extras = listOf(
                AnalyticsEvent.Param("payment_exported_to_file", totalPayment.toString()),
            ),
        ),
    )
}
