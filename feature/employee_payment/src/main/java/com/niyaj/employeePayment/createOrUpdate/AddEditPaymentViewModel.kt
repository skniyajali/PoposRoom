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

package com.niyaj.employeePayment.createOrUpdate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.PaymentRepository
import com.niyaj.domain.payment.ValidateGivenAmountUseCase
import com.niyaj.domain.payment.ValidateGivenDateUseCase
import com.niyaj.domain.payment.ValidatePaymentEmployeeUseCase
import com.niyaj.domain.payment.ValidatePaymentModeUseCase
import com.niyaj.domain.payment.ValidatePaymentNoteUseCase
import com.niyaj.domain.payment.ValidatePaymentTypeUseCase
import com.niyaj.model.Employee
import com.niyaj.model.Payment
import com.niyaj.model.PaymentMode
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.annotations.TestOnly
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AddEditPaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val validateGivenAmount: ValidateGivenAmountUseCase,
    private val validateGivenDate: ValidateGivenDateUseCase,
    private val validatePaymentEmployee: ValidatePaymentEmployeeUseCase,
    private val validatePaymentMode: ValidatePaymentModeUseCase,
    private val validatePaymentNote: ValidatePaymentNoteUseCase,
    private val validatePaymentType: ValidatePaymentTypeUseCase,
    private val analyticsHelper: AnalyticsHelper,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val paymentId = savedStateHandle.getStateFlow("paymentId", 0)

    var state by mutableStateOf(AddEditPaymentState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.shareIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        replay = 1,
    )

    val employees = paymentRepository.getAllEmployee().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    private val _selectedEmployee = MutableStateFlow(Employee())
    val selectedEmployee = _selectedEmployee.asStateFlow()

    init {
        savedStateHandle.get<Int>("paymentId")?.let { paymentId ->
            if (paymentId != 0) getPaymentById(paymentId)
        }

        savedStateHandle.get<Int>("employeeId")?.let { employeeId ->
            if (employeeId != 0) getEmployeeById(employeeId)
        }
    }

    val employeeError: StateFlow<String?> = _selectedEmployee
        .mapLatest {
            validatePaymentEmployee(it.employeeId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val amountError: StateFlow<String?> = snapshotFlow { state.paymentAmount }
        .mapLatest {
            validateGivenAmount(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val dateError: StateFlow<String?> = snapshotFlow { state.paymentDate }
        .mapLatest {
            validateGivenDate(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val paymentTypeError: StateFlow<String?> = snapshotFlow { state.paymentType }
        .mapLatest {
            validatePaymentType(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val paymentModeError: StateFlow<String?> = snapshotFlow { state.paymentMode }
        .mapLatest {
            validatePaymentMode(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val paymentNoteError: StateFlow<String?> = snapshotFlow { state.paymentMode }
        .mapLatest {
            validatePaymentNote(
                paymentNote = state.paymentNote,
                isRequired = it == PaymentMode.Both,
            ).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    fun onEvent(event: AddEditPaymentEvent) {
        when (event) {
            is AddEditPaymentEvent.PaymentAmountChanged -> {
                state = state.copy(paymentAmount = event.paymentAmount)
            }

            is AddEditPaymentEvent.PaymentDateChanged -> {
                state = state.copy(paymentDate = event.paymentDate)
            }

            is AddEditPaymentEvent.PaymentModeChanged -> {
                state = state.copy(paymentMode = event.paymentMode)
            }

            is AddEditPaymentEvent.PaymentNoteChanged -> {
                state = state.copy(paymentNote = event.paymentNote)
            }

            is AddEditPaymentEvent.PaymentTypeChanged -> {
                state = state.copy(paymentType = event.paymentType)
            }

            is AddEditPaymentEvent.CreateOrUpdatePayment -> {
                createOrUpdatePayment(paymentId.value)
            }

            is AddEditPaymentEvent.OnSelectEmployee -> {
                viewModelScope.launch {
                    _selectedEmployee.value = event.employee
                }
            }
        }
    }

    private fun getPaymentById(itemId: Int) {
        viewModelScope.launch {
            when (val result = paymentRepository.getPaymentById(itemId)) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError("Unable to find payment"))
                }

                is Resource.Success -> {
                    result.data?.let { payment ->
                        getEmployeeById(payment.employeeId)

                        state = state.copy(
                            paymentAmount = payment.paymentAmount,
                            paymentDate = payment.paymentDate,
                            paymentMode = payment.paymentMode,
                            paymentType = payment.paymentType,
                            paymentNote = payment.paymentNote,
                        )
                    }
                }
            }
        }
    }

    private fun getEmployeeById(employeeId: Int) {
        viewModelScope.launch {
            paymentRepository.getEmployeeById(employeeId)?.let { employee ->
                _selectedEmployee.value = employee
            }
        }
    }

    private fun createOrUpdatePayment(paymentId: Int = 0) {
        viewModelScope.launch {
            val hasError = listOf(
                employeeError,
                amountError,
                dateError,
                paymentModeError,
                paymentNoteError,
                paymentTypeError,
            ).all { it.value != null }

            if (!hasError) {
                val newPayment = Payment(
                    paymentId = paymentId,
                    employeeId = _selectedEmployee.value.employeeId,
                    paymentAmount = state.paymentAmount,
                    paymentDate = state.paymentDate,
                    paymentType = state.paymentType,
                    paymentMode = state.paymentMode,
                    paymentNote = state.paymentNote,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = if (paymentId != 0) System.currentTimeMillis() else null,
                )

                when (paymentRepository.upsertPayment(newPayment)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError("Unable To Add Payment."))
                    }

                    is Resource.Success -> {
                        val message = if (paymentId == 0) "Added" else "Updated"
                        _eventFlow.emit(UiEvent.OnSuccess("Payment $message Successfully."))
                        analyticsHelper.logOnCreateOrUpdatePayment(paymentId, message)
                    }
                }

                state = AddEditPaymentState()
            }
        }
    }

    @TestOnly
    internal fun setPaymentId(paymentId: Int) {
        savedStateHandle["paymentId"] = paymentId
    }
}

private fun AnalyticsHelper.logOnCreateOrUpdatePayment(data: Int, message: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "employee_payment_$message",
            extras = listOf(
                AnalyticsEvent.Param("employee_payment_$message", data.toString()),
            ),
        ),
    )
}
