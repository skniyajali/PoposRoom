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

package com.niyaj.employeeAbsent.createOrUpdate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.capitalizeWords
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.AbsentRepository
import com.niyaj.domain.absent.ValidateAbsentDateUseCase
import com.niyaj.domain.absent.ValidateAbsentEmployeeUseCase
import com.niyaj.model.Absent
import com.niyaj.model.Employee
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.annotations.TestOnly
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AddEditAbsentViewModel @Inject constructor(
    private val absentRepository: AbsentRepository,
    private val validateAbsentDate: ValidateAbsentDateUseCase,
    private val validateAbsentEmployee: ValidateAbsentEmployeeUseCase,
    private val analyticsHelper: AnalyticsHelper,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val absentId = savedStateHandle.getStateFlow("absentId", 0)

    var state by mutableStateOf(AddEditAbsentState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.shareIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        replay = 1,
    )

    val employees = absentRepository.getAllEmployee().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    private val _selectedEmployee = MutableStateFlow(Employee())
    val selectedEmployee = _selectedEmployee.asStateFlow()

    init {
        savedStateHandle.get<Int>("absentId")?.let { absentId ->
            if (absentId != 0) getAbsentById(absentId)
        }

        savedStateHandle.get<Int>("employeeId")?.let { employeeId ->
            if (employeeId != 0) getEmployeeById(employeeId)
        }
    }

    val employeeError: StateFlow<String?> = _selectedEmployee
        .mapLatest {
            validateAbsentEmployee(it.employeeId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    private val observeDate = snapshotFlow { state.absentDate }

    val dateError = _selectedEmployee.combine(observeDate) { emp, date ->
        validateAbsentDate(
            absentDate = date,
            employeeId = emp.employeeId,
            absentId = absentId.value,
        ).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    fun onEvent(event: AddEditAbsentEvent) {
        when (event) {
            is AddEditAbsentEvent.AbsentDateChanged -> {
                state = state.copy(absentDate = event.absentDate)
            }

            is AddEditAbsentEvent.AbsentReasonChanged -> {
                state = state.copy(absentReason = event.absentReason)
            }

            is AddEditAbsentEvent.CreateOrUpdateAbsent -> {
                createOrUpdateAbsent(absentId.value)
            }

            is AddEditAbsentEvent.OnSelectEmployee -> {
                viewModelScope.launch {
                    _selectedEmployee.value = event.employee
                }
            }
        }
    }

    private fun getAbsentById(itemId: Int) {
        viewModelScope.launch {
            when (val result = absentRepository.getAbsentById(itemId)) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError("Unable to find employee absent"))
                }

                is Resource.Success -> {
                    result.data?.let { absent ->
                        getEmployeeById(absent.employeeId)

                        state = state.copy(
                            absentDate = absent.absentDate,
                            absentReason = absent.absentReason,
                        )
                    }
                }
            }
        }
    }

    private fun getEmployeeById(employeeId: Int) {
        viewModelScope.launch {
            absentRepository.getEmployeeById(employeeId)?.let { employee ->
                _selectedEmployee.value = employee
            }
        }
    }

    private fun createOrUpdateAbsent(absentId: Int = 0) {
        viewModelScope.launch {
            val hasError = listOf(employeeError, dateError).all { it.value == null }

            if (hasError) {
                val newAbsent = Absent(
                    absentId = absentId,
                    employeeId = _selectedEmployee.value.employeeId,
                    absentDate = state.absentDate,
                    absentReason = state.absentReason.trim().capitalizeWords,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = if (absentId == 0) null else System.currentTimeMillis(),
                )

                when (absentRepository.upsertAbsent(newAbsent)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError("Unable To Mark Employee Absent."))
                    }

                    is Resource.Success -> {
                        val message = if (absentId == 0) "created" else "updated"
                        _eventFlow.emit(UiEvent.OnSuccess("Employee absent date $message."))
                        analyticsHelper.logOnCreateOrUpdateAbsent(absentId, message)
                    }
                }

                state = AddEditAbsentState()
            }
        }
    }

    @TestOnly
    internal fun setAbsentId(id: Int) {
        savedStateHandle["absentId"] = id
    }
}

private fun AnalyticsHelper.logOnCreateOrUpdateAbsent(data: Int, message: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "employee_absent_$message",
            extras = listOf(
                AnalyticsEvent.Param("employee_absent_$message", data.toString()),
            ),
        ),
    )
}
