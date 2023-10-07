package com.niyaj.employee_payment.settings

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
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
    private val repository: PaymentRepository
): BaseViewModel() {

    val items = snapshotFlow { _searchText.value }.flatMapLatest {
        repository.getAllEmployeePayments(it)
    }.mapLatest { list ->
        totalItems = list.flatMap { item -> item.payments.map { it.paymentId } }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _exportedItems = MutableStateFlow<List<EmployeeWithPayments>>(emptyList())
    val exportedItems = _exportedItems.asStateFlow()

    private val _importedItems = MutableStateFlow<List<EmployeeWithPayments>>(emptyList())
    val importedItems = _importedItems.asStateFlow()

    fun onEvent(event: PaymentSettingsEvent) {
        when(event) {
            is PaymentSettingsEvent.GetExportedItems -> {
                viewModelScope.launch {
                    if (mSelectedItems.isEmpty()) {
                        _exportedItems.value = items.value
                    } else {
                        val list = mutableListOf<EmployeeWithPayments>()

                        mSelectedItems.forEach { paymentId ->
                            items.value.find { it ->
                                it.payments.any { it.paymentId == paymentId }
                            }?.let {
                                list.add(it)
                            }
                        }

                        _exportedItems.emit(list.toList())
                    }
                }
            }

            is PaymentSettingsEvent.OnImportPaymentsFromFile -> {
                viewModelScope.launch {
                    _importedItems.value = emptyList()

                    if (event.data.isNotEmpty()) {
                        totalItems = event.data.flatMap { item -> item.payments.map { it.paymentId } }
                        _importedItems.value = event.data
                    }
                }
            }

            is PaymentSettingsEvent.ImportPaymentsToDatabase -> {
                viewModelScope.launch {
                    val data = if (mSelectedItems.isNotEmpty()) {
                        mSelectedItems.flatMap {paymentId ->
                            _importedItems.value.filter { employeeWithAbsents ->
                                employeeWithAbsents.payments.any { it.paymentId == paymentId }
                            }
                        }
                    }else {
                        _importedItems.value
                    }

                    when(val result = repository.importPaymentsToDatabase(data)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                        }
                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.OnSuccess("${data.sumOf { it.payments.size }} items has been imported successfully"))
                        }
                    }
                }
            }
        }
    }
}