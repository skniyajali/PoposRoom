package com.niyaj.employee.details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.data.repository.EmployeeRepository
import com.niyaj.ui.event.UiState
import com.samples.apps.core.analytics.AnalyticsEvent
import com.samples.apps.core.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


/**
 * Employee Details view model class
 *
 */
@HiltViewModel
class EmployeeDetailsViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val analyticsHelper: AnalyticsHelper,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _employeeId = savedStateHandle.get<Int>("employeeId") ?: 0

    private val _selectedSalaryDate = mutableStateOf<Pair<String, String>?>(null)
    val selectedSalaryDate: State<Pair<String, String>?> = _selectedSalaryDate

    val employeeDetails = snapshotFlow { _employeeId }.mapLatest {
        val data = employeeRepository.getEmployeeById(it)

        if (data == null) UiState.Empty else UiState.Success(data)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    val salaryDates = snapshotFlow { _employeeId }.mapLatest {
        employeeRepository.getSalaryCalculableDate(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val employeeAbsentDates = snapshotFlow { _employeeId }.flatMapLatest { employeeId ->
        employeeRepository.getEmployeeAbsentDates(employeeId).mapLatest {
            if (it.isEmpty()) UiState.Empty else UiState.Success(it)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    val salaryEstimation = snapshotFlow { _selectedSalaryDate.value }.flatMapLatest { date ->
        employeeRepository.getEmployeeSalaryEstimation(_employeeId, date).mapLatest { result ->
            UiState.Success(result)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    val payments = snapshotFlow { _employeeId }.flatMapLatest { employeeId ->
        employeeRepository.getEmployeePayments(employeeId).mapLatest { result ->
            if (result.isEmpty()) UiState.Empty else UiState.Success(result)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    init {
        savedStateHandle.get<Int>("employeeId")?.let {
            analyticsHelper.logEmployeeDetailsViewed(it)
        }
    }


    /**
     *
     */
    fun onEvent(event: EmployeeDetailsEvent) {
        when (event) {
            is EmployeeDetailsEvent.OnChooseSalaryDate -> {
                _selectedSalaryDate.value = event.date
            }
        }
    }
}

internal fun AnalyticsHelper.logEmployeeDetailsViewed(employeeId: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "employee_details_viewed",
            extras = listOf(
                AnalyticsEvent.Param("employee_details_viewed", employeeId.toString()),
            ),
        ),
    )
}