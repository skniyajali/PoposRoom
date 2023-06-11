package com.niyaj.poposroom.features.employee.presentation.add_edit

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
import com.niyaj.poposroom.features.employee.domain.repository.EmployeeRepository
import com.niyaj.poposroom.features.employee.domain.repository.EmployeeValidationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AddEditEmployeeViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val validationRepository: EmployeeValidationRepository,
    @Dispatcher(PoposDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private var employeeId = savedStateHandle.get<Int>("employeeId")

    var state by mutableStateOf(AddEditEmployeeState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<Int>("employeeId")?.let { employeeId ->
            getCustomerById(employeeId)
        }
    }

    val phoneError: StateFlow<String?> = snapshotFlow { state.employeePhone }
        .mapLatest {
            validationRepository.validateEmployeePhone(it, employeeId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val nameError: StateFlow<String?> = snapshotFlow { state.employeeName }
        .mapLatest {
            validationRepository.validateEmployeeName(it, employeeId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val salaryError: StateFlow<String?> = snapshotFlow { state.employeeSalary }
        .mapLatest {
            validationRepository.validateEmployeeSalary(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val positionError: StateFlow<String?> = snapshotFlow { state.employeePosition }
        .mapLatest {
            validationRepository.validateEmployeePosition(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun onEvent(event: AddEditEmployeeEvent) {
        when (event) {
            is AddEditEmployeeEvent.EmployeeNameChanged -> {
                state = state.copy(employeeName = event.employeeName)
            }

            is AddEditEmployeeEvent.EmployeePhoneChanged -> {
                state = state.copy(employeePhone = event.employeePhone)

            }

            is AddEditEmployeeEvent.EmployeeSalaryChanged -> {
                state = state.copy(employeeSalary = event.employeeSalary)
            }

            is AddEditEmployeeEvent.EmployeeEmailChanged -> {
                state = state.copy(employeeEmail = event.employeeEmail)
            }


            is AddEditEmployeeEvent.EmployeeJoinedDateChanged -> {
                state = state.copy(employeeJoinedDate = event.employeeJoinedDate)
            }

            is AddEditEmployeeEvent.EmployeePositionChanged -> {
                state = state.copy(employeePosition = event.employeePosition)
            }

            is AddEditEmployeeEvent.EmployeeSalaryTypeChanged -> {
                state = state.copy(employeeSalaryType = event.employeeSalaryType)
            }

            is AddEditEmployeeEvent.EmployeeTypeChanged -> {
                state = state.copy(employeeType = event.employeeType)
            }

            is AddEditEmployeeEvent.CreateOrUpdateEmployee -> {
                createOrUpdateCustomer(event.employeeId)
            }
        }
    }

    private fun getCustomerById(itemId: Int) {
        viewModelScope.launch(ioDispatcher) {
            when (val result = employeeRepository.getEmployeeById(itemId)) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError("Unable to find employee"))
                }
                is Resource.Success -> {
                    result.data?.let { employee ->
                        state = state.copy(
                            employeePhone = employee.employeePhone,
                            employeeName = employee.employeeName,
                            employeeEmail = employee.employeeEmail,
                            employeeSalary = employee.employeeSalary,
                            employeePosition = employee.employeePosition,
                            employeeSalaryType = employee.employeeSalaryType,
                            employeeType = employee.employeeType,
                            employeeJoinedDate = employee.employeeJoinedDate
                        )
                    }
                }
            }
        }
    }

    private fun createOrUpdateCustomer(employeeId: Int = 0) {
        viewModelScope.launch(ioDispatcher) {
            val hasError = listOf(phoneError, nameError, salaryError, positionError).all {
                it.value != null
            }

            if (!hasError) {
                val addOnItem = Employee(
                    employeeId = employeeId,
                    employeePhone = state.employeePhone,
                    employeeName = state.employeeName,
                    employeeSalary = state.employeeSalary,
                    employeeEmail = state.employeeEmail,
                    employeePosition = state.employeePosition,
                    employeeSalaryType = state.employeeSalaryType,
                    employeeType = state.employeeType,
                    employeeJoinedDate = state.employeeJoinedDate,
                    createdAt = Date(),
                    updatedAt = if (employeeId != 0) Date() else null
                )

                when(employeeRepository.upsertEmployee(addOnItem)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError("Unable To Create Employee."))
                    }
                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.OnSuccess("Employee Created Successfully."))
                    }
                }
                state = AddEditEmployeeState()
            }
        }
    }
}