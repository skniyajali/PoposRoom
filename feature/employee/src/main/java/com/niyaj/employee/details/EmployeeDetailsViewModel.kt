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

    private val employeeId = savedStateHandle.get<Int>("employeeId") ?: 0

    private val _selectedSalaryDate = mutableStateOf<Pair<String, String>?>(null)
    val selectedSalaryDate: State<Pair<String, String>?> = _selectedSalaryDate

    val employeeDetails = snapshotFlow { employeeId }.mapLatest {
        val data = employeeRepository.getEmployeeById(it)

        if (data == null) UiState.Empty else UiState.Success(data)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    val salaryDates = snapshotFlow { employeeId }.mapLatest {
        employeeRepository.getSalaryCalculableDate(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val employeeAbsentDates = snapshotFlow { employeeId }.flatMapLatest { employeeId ->
        employeeRepository.getEmployeeAbsentDates(employeeId).mapLatest {
            if (it.isEmpty()) UiState.Empty else UiState.Success(it)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    val salaryEstimation = snapshotFlow { _selectedSalaryDate.value }.flatMapLatest { date ->
        employeeRepository.getEmployeeSalaryEstimation(employeeId, date).mapLatest { result ->
            UiState.Success(result)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    val payments = snapshotFlow { employeeId }.flatMapLatest { employeeId ->
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
