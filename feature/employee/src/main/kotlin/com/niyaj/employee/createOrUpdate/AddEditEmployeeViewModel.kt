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

package com.niyaj.employee.createOrUpdate

import android.graphics.Bitmap
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
import com.niyaj.data.repository.EmployeeRepository
import com.niyaj.data.repository.QRCodeScanner
import com.niyaj.domain.employee.ValidateEmployeeNameUseCase
import com.niyaj.domain.employee.ValidateEmployeePhoneUseCase
import com.niyaj.domain.employee.ValidateEmployeePositionUseCase
import com.niyaj.domain.employee.ValidateEmployeeSalaryUseCase
import com.niyaj.model.Employee
import com.niyaj.ui.utils.QRCodeEncoder
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.annotations.TestOnly
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AddEditEmployeeViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val validateEmployeePhone: ValidateEmployeePhoneUseCase,
    private val validateEmployeeName: ValidateEmployeeNameUseCase,
    private val validateEmployeePosition: ValidateEmployeePositionUseCase,
    private val validateEmployeeSalary: ValidateEmployeeSalaryUseCase,
    private val analyticsHelper: AnalyticsHelper,
    private val scanner: QRCodeScanner,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var employeeId = savedStateHandle.getStateFlow("employeeId", 0)

    var state by mutableStateOf(AddEditEmployeeState())

    private val _scannedBitmap = MutableStateFlow<Bitmap?>(null)
    val scannedBitmap = _scannedBitmap.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.shareIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        replay = 1,
    )

    init {
        savedStateHandle.get<Int>("employeeId")?.let { employeeId ->
            getEmployeeById(employeeId)
        }
    }

    val phoneError: StateFlow<String?> = snapshotFlow { state.employeePhone }
        .mapLatest {
            validateEmployeePhone(it, employeeId.value).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val nameError: StateFlow<String?> = snapshotFlow { state.employeeName }
        .mapLatest {
            validateEmployeeName(it, employeeId.value).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val salaryError: StateFlow<String?> = snapshotFlow { state.employeeSalary }
        .mapLatest {
            validateEmployeeSalary(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val positionError: StateFlow<String?> = snapshotFlow { state.employeePosition }
        .mapLatest {
            validateEmployeePosition(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
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

            is AddEditEmployeeEvent.UpdateDeliveryPartner -> {
                state = state.copy(
                    isDeliveryPartner = !state.isDeliveryPartner,
                )
            }

            is AddEditEmployeeEvent.ScanQRCode -> {
                viewModelScope.launch {
                    startScanning()
                }
            }

            is AddEditEmployeeEvent.CreateOrUpdateEmployee -> {
                createOrUpdateEmployee(employeeId.value)
            }
        }
    }

    private fun getEmployeeById(itemId: Int) {
        viewModelScope.launch {
            employeeRepository.getEmployeeById(itemId)?.let { employee ->
                state = state.copy(
                    employeePhone = employee.employeePhone,
                    employeeName = employee.employeeName,
                    employeeEmail = employee.employeeEmail,
                    employeeSalary = employee.employeeSalary,
                    employeePosition = employee.employeePosition,
                    employeeSalaryType = employee.employeeSalaryType,
                    employeeType = employee.employeeType,
                    employeeJoinedDate = employee.employeeJoinedDate,
                    isDeliveryPartner = employee.isDeliveryPartner,
                    partnerQRCode = employee.partnerQRCode,
                )

                employee.partnerQRCode?.let {
                    _scannedBitmap.value = QRCodeEncoder().encodeBitmap(it)
                }
            }
        }
    }

    private fun startScanning() {
        viewModelScope.launch {
            scanner.startScanning().collectLatest {
                if (!it.isNullOrEmpty()) {
                    _scannedBitmap.value = QRCodeEncoder().encodeBitmap(it)

                    state = state.copy(partnerQRCode = it)
                }
            }
        }
    }

    private fun createOrUpdateEmployee(employeeId: Int = 0) {
        viewModelScope.launch {
            val hasError = listOf(phoneError, nameError, salaryError, positionError).all {
                it.value != null
            }

            if (!hasError) {
                val newEmployee = Employee(
                    employeeId = employeeId,
                    employeeName = state.employeeName.trim().capitalizeWords,
                    employeePhone = state.employeePhone.trim(),
                    employeeSalary = state.employeeSalary.trim(),
                    employeeEmail = state.employeeEmail?.trim(),
                    employeePosition = state.employeePosition,
                    employeeSalaryType = state.employeeSalaryType,
                    employeeType = state.employeeType,
                    employeeJoinedDate = state.employeeJoinedDate,
                    isDeliveryPartner = state.isDeliveryPartner,
                    partnerQRCode = state.partnerQRCode,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = if (employeeId != 0) System.currentTimeMillis() else null,
                )

                when (employeeRepository.upsertEmployee(newEmployee)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError("Unable To Create Employee."))
                    }

                    is Resource.Success -> {
                        val message = if (employeeId == 0) "Created" else "Updated"
                        _eventFlow.emit(UiEvent.OnSuccess("Employee $message Successfully."))
                        analyticsHelper.logOnCreateOrUpdateEmployee(employeeId, message)
                    }
                }
                state = AddEditEmployeeState()
            }
        }
    }

    @TestOnly
    internal fun setEmployeeId(id: Int) {
        savedStateHandle["employeeId"] = id
    }
}

private fun AnalyticsHelper.logOnCreateOrUpdateEmployee(data: Int, message: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "employee_$message",
            extras = listOf(AnalyticsEvent.Param("employee_$message", data.toString())),
        ),
    )
}
