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

package com.niyaj.employee.settings

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.EmployeeRepository
import com.niyaj.model.Employee
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
class EmployeeSettingsViewModel @Inject constructor(
    private val repository: EmployeeRepository,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel() {

    val employees = snapshotFlow { mSearchText.value }.flatMapLatest {
        repository.getAllEmployee(it)
    }.mapLatest { list ->
        totalItems = list.map { it.employeeId }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    private val _exportedItems = MutableStateFlow<List<Employee>>(emptyList())
    val exportedItems = _exportedItems.asStateFlow()

    private val _importedItems = MutableStateFlow<List<Employee>>(emptyList())
    val importedItems = _importedItems.asStateFlow()

    fun onEvent(event: EmployeeSettingsEvent) {
        when (event) {
            is EmployeeSettingsEvent.GetExportedItems -> {
                viewModelScope.launch {
                    if (mSelectedItems.isEmpty()) {
                        _exportedItems.value = employees.value
                    } else {
                        val list = mutableListOf<Employee>()

                        mSelectedItems.forEach { id ->
                            val category = employees.value.find { it.employeeId == id }

                            if (category != null) {
                                list.add(category)
                            }
                        }

                        _exportedItems.emit(list.toList())
                    }

                    analyticsHelper.logExportedEmployeeToFile(_exportedItems.value.size)
                }
            }

            is EmployeeSettingsEvent.OnImportEmployeeItemsFromFile -> {
                viewModelScope.launch {
                    _importedItems.value = emptyList()

                    if (event.data.isNotEmpty()) {
                        totalItems = event.data.map { it.employeeId }
                        _importedItems.value = event.data
                    }

                    analyticsHelper.logImportedEmployeeFromFile(event.data.size)
                }
            }

            is EmployeeSettingsEvent.ImportEmployeeItemsToDatabase -> {
                viewModelScope.launch {
                    val data = if (mSelectedItems.isNotEmpty()) {
                        mSelectedItems.flatMap { employeeId ->
                            _importedItems.value.filter { it.employeeId == employeeId }
                        }
                    } else {
                        _importedItems.value
                    }

                    when (val result = repository.importEmployeesToDatabase(data)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                        }

                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.OnSuccess("${data.size} employees has been imported successfully"))
                            analyticsHelper.logImportedEmployeeToDatabase(data.size)
                        }
                    }
                }
            }
        }
    }
}

internal fun AnalyticsHelper.logImportedEmployeeFromFile(totalEmployee: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "employee_imported_from_file",
            extras = listOf(
                com.niyaj.core.analytics.AnalyticsEvent.Param("employee_imported_from_file", totalEmployee.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logImportedEmployeeToDatabase(totalEmployee: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "employee_imported_to_database",
            extras = listOf(
                com.niyaj.core.analytics.AnalyticsEvent.Param("employee_imported_to_database", totalEmployee.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logExportedEmployeeToFile(totalEmployee: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "employee_exported_to_file",
            extras = listOf(
                com.niyaj.core.analytics.AnalyticsEvent.Param("employee_exported_to_file", totalEmployee.toString()),
            ),
        ),
    )
}
