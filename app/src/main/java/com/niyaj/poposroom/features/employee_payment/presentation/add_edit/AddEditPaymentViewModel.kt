package com.niyaj.poposroom.features.employee_payment.presentation.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.employee.domain.model.Employee
import com.niyaj.poposroom.features.employee.domain.utils.PaymentMode
import com.niyaj.poposroom.features.employee_payment.domain.model.Payment
import com.niyaj.poposroom.features.employee_payment.domain.repository.PaymentRepository
import com.niyaj.poposroom.features.employee_payment.domain.repository.PaymentValidationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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
import java.util.Date
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AddEditPaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val validationRepository: PaymentValidationRepository,
    @Dispatcher(PoposDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
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

    val employeeError: StateFlow<String?> = snapshotFlow { state.employeeId }
        .mapLatest {
            validationRepository.validateEmployee(it).errorMessage
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
            is AddEditPaymentEvent.EmployeeChanged -> {
                state = state.copy(employeeId = event.employeeId)
            }

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
        viewModelScope.launch(ioDispatcher) {
            when(val result = paymentRepository.getPaymentById(itemId)) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError("Unable to find payment"))
                }
                is Resource.Success -> {
                    result.data?.let { payment ->
                        state = state.copy(
                            employeeId = payment.employeeId,
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
                _selectedEmployee.emit(employee)

                state = state.copy(employeeId = employee.employeeId)
            }
        }
    }

    private fun createOrUpdatePayment(paymentId: Int = 0) {
        viewModelScope.launch(ioDispatcher) {
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
                    employeeId = state.employeeId,
                    paymentAmount = state.paymentAmount,
                    paymentDate = state.paymentDate,
                    paymentType = state.paymentType,
                    paymentMode = state.paymentMode,
                    paymentNote = state.paymentNote,
                    createdAt = Date(),
                    updatedAt = if (paymentId != 0) Date() else null
                )

                when(paymentRepository.upsertPayment(newPayment)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError("Unable To Add New Payment."))
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