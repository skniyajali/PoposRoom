package com.niyaj.poposroom.features.employee_absent.presentation.add_edit

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
import com.niyaj.poposroom.features.employee_absent.domain.model.Absent
import com.niyaj.poposroom.features.employee_absent.domain.repository.AbsentRepository
import com.niyaj.poposroom.features.employee_absent.domain.repository.AbsentValidationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AddEditAbsentViewModel @Inject constructor(
    private val absentRepository: AbsentRepository,
    private val validationRepository: AbsentValidationRepository,
    @Dispatcher(PoposDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val  absentId = savedStateHandle.get<Int>("absentId")

    var state by mutableStateOf(AddEditAbsentState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val employees = absentRepository.getAllEmployee().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _selectedEmployee = MutableStateFlow(Employee())
    val selectedEmployee = _selectedEmployee.asStateFlow()

    init {
        savedStateHandle.get<Int>("absentId")?.let { absentId ->
            getAbsentById(absentId)
        }

        savedStateHandle.get<Int>("employeeId")?.let { employeeId ->
            getEmployeeById(employeeId)
        }
    }

    val employeeError: StateFlow<String?> = snapshotFlow { state.employeeId }
        .mapLatest {
            validationRepository.validateAbsentEmployee(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val observeEmployee = snapshotFlow { state.employeeId }
    private val observeDate = snapshotFlow { state.absentDate }

    val dateError = observeEmployee.combine(observeDate) { emp, date ->
        validationRepository.validateAbsentDate(
            absentDate = date,
            employeeId = emp,
            absentId = absentId
        ).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    fun onEvent(event: AddEditAbsentEvent) {
        when (event) {
            is AddEditAbsentEvent.EmployeeChanged -> {
                state = state.copy(employeeId = event.employeeId)
            }

            is AddEditAbsentEvent.AbsentDateChanged -> {
                state = state.copy(absentDate = event.absentDate)
            }

            is AddEditAbsentEvent.AbsentReasonChanged -> {
                state = state.copy(absentReason = event.absentReason)
            }

            is AddEditAbsentEvent.CreateOrUpdateAbsent -> {
                createOrUpdateAbsent(event.absentId)
            }

            is AddEditAbsentEvent.OnSelectEmployee -> {
                viewModelScope.launch {
                    _selectedEmployee.value = event.employee
                }
            }
        }
    }

    private fun getAbsentById(itemId: Int) {
        viewModelScope.launch(ioDispatcher) {
            when(val result = absentRepository.getAbsentById(itemId)) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError("Unable to find employee absent"))
                }
                is Resource.Success -> {
                    result.data?.let { absent ->
                        state = state.copy(
                            employeeId = absent.employeeId,
                            absentDate = absent.absentDate,
                            absentReason = absent.absentReason
                        )
                    }
                }
            }
        }
    }

    private fun getEmployeeById(employeeId: Int) {
        viewModelScope.launch {
            absentRepository.getEmployeeById(employeeId)?.let { employee ->
                _selectedEmployee.emit(employee)

                state = state.copy(employeeId = employee.employeeId)
            }
        }
    }

    private fun createOrUpdateAbsent(absentId: Int = 0) {
        viewModelScope.launch(ioDispatcher) {
            val hasError = listOf(employeeError, dateError).all { it.value == null }

            if (hasError) {
                val newAbsent = Absent(
                    absentId = absentId,
                    employeeId = state.employeeId,
                    absentDate = state.absentDate,
                    absentReason = state.absentReason,
                    createdAt = Date(),
                    updatedAt = if (absentId == 0) null else Date()
                )

                when(absentRepository.upsertAbsent(newAbsent)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError("Unable To Mark Employee Absent."))
                    }

                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.OnSuccess("Employee Marked As Absent."))
                    }
                }

                state = AddEditAbsentState()
            }
        }
    }
}