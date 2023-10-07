package com.niyaj.employee_payment.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.PaymentRepository
import com.niyaj.data.repository.validation.PaymentValidationRepository
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
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AddEditPaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val validationRepository: PaymentValidationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var state by mutableStateOf(AddEditPaymentState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val employees = paymentRepository.getAllEmployee().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _selectedEmployee = MutableStateFlow(Employee())
    val selectedEmployee = _selectedEmployee.asStateFlow()

    init {
        savedStateHandle.get<Int>("paymentId")?.let { paymentId ->
            getPaymentById(paymentId)
        }

        savedStateHandle.get<Int>("employeeId")?.let { employeeId ->
            getEmployeeById(employeeId)
        }
    }

    val employeeError: StateFlow<String?> = _selectedEmployee
        .mapLatest {
            validationRepository.validateEmployee(it.employeeId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val amountError: StateFlow<String?> = snapshotFlow { state.paymentAmount }
        .mapLatest {
            validationRepository.validateGivenAmount(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val dateError: StateFlow<String?> = snapshotFlow { state.paymentDate }
        .mapLatest {
            validationRepository.validateGivenDate(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val paymentTypeError: StateFlow<String?> = snapshotFlow { state.paymentType }
        .mapLatest {
            validationRepository.validatePaymentType(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val paymentModeError: StateFlow<String?> = snapshotFlow { state.paymentMode }
        .mapLatest {
            validationRepository.validatePaymentMode(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val paymentNoteError: StateFlow<String?> = snapshotFlow { state.paymentMode }
        .mapLatest {
            validationRepository.validatePaymentNote(
                paymentNote = state.paymentNote,
                isRequired = it == PaymentMode.Both
            ).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
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
                createOrUpdatePayment(event.paymentId)
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
            when(val result = paymentRepository.getPaymentById(itemId)) {
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
                paymentTypeError
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
                    updatedAt = if (paymentId != 0) System.currentTimeMillis() else null
                )

                when(paymentRepository.upsertPayment(newPayment)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError("Unable To Add Payment."))
                    }
                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.OnSuccess("Payment Added Successfully."))
                    }
                }

                state = AddEditPaymentState()
            }
        }
    }
}