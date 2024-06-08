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

package com.niyaj.employeeAbsent.settings

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.AbsentRepository
import com.niyaj.model.EmployeeWithAbsents
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
class AbsentSettingsViewModel @Inject constructor(
    private val repository: AbsentRepository,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel() {

    val items = snapshotFlow { mSearchText.value }.flatMapLatest {
        repository.getAllEmployeeAbsents(it)
    }.mapLatest { list ->
        totalItems = list.flatMap { item -> item.absents.map { it.absentId } }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    private val _exportedItems = MutableStateFlow<List<EmployeeWithAbsents>>(emptyList())
    val exportedItems = _exportedItems.asStateFlow()

    private val _importedItems = MutableStateFlow<List<EmployeeWithAbsents>>(emptyList())
    val importedItems = _importedItems.asStateFlow()

    private val _selectedEmployee = MutableStateFlow(0)
    val selectedEmployee = _selectedEmployee.asStateFlow()

    fun selectEmployee(employeeId: Int) {
        viewModelScope.launch {
            if (_selectedEmployee.value == employeeId) {
                _selectedEmployee.value = 0
            } else {
                _selectedEmployee.value = employeeId
            }
        }
    }

    fun onEvent(event: AbsentSettingsEvent) {
        when (event) {
            is AbsentSettingsEvent.GetExportedItems -> {
                viewModelScope.launch {
                    if (mSelectedItems.isEmpty()) {
                        _exportedItems.value = items.value
                    } else {
                        val list = mutableListOf<EmployeeWithAbsents>()

                        mSelectedItems.forEach { absentId ->
                            items.value.find { it ->
                                it.absents.any { it.absentId == absentId }
                            }?.let {
                                list.add(it)
                            }
                        }

                        _exportedItems.emit(list.toList())
                    }

                    analyticsHelper.logExportedAbsentToFile(_exportedItems.value.size)
                }
            }

            is AbsentSettingsEvent.OnImportAbsentItemsFromFile -> {
                viewModelScope.launch {
                    _importedItems.value = emptyList()

                    if (event.data.isNotEmpty()) {
                        totalItems = event.data.flatMap { item -> item.absents.map { it.absentId } }
                        _importedItems.value = event.data
                    }

                    analyticsHelper.logImportedAbsentFromFile(_importedItems.value.size)
                }
            }

            is AbsentSettingsEvent.ImportAbsentItemsToDatabase -> {
                viewModelScope.launch {
                    val data = if (mSelectedItems.isNotEmpty()) {
                        mSelectedItems.flatMap { absentId ->
                            _importedItems.value.filter { employeeWithAbsents ->
                                employeeWithAbsents.absents.any { it.absentId == absentId }
                            }
                        }
                    } else {
                        _importedItems.value
                    }

                    when (val result = repository.importAbsentDataToDatabase(data)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                        }

                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.OnSuccess("${data.sumOf { it.absents.size }} items has been imported successfully"))
                            analyticsHelper.logImportedAbsentToDatabase(data.sumOf { it.absents.size })
                        }
                    }
                }
            }
        }
    }
}

internal fun AnalyticsHelper.logImportedAbsentFromFile(totalAbsent: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "absent_imported_from_file",
            extras = listOf(
                com.niyaj.core.analytics.AnalyticsEvent.Param("absent_imported_from_file", totalAbsent.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logImportedAbsentToDatabase(totalAbsent: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "absent_imported_to_database",
            extras = listOf(
                com.niyaj.core.analytics.AnalyticsEvent.Param("absent_imported_to_database", totalAbsent.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logExportedAbsentToFile(totalAbsent: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "absent_exported_to_file",
            extras = listOf(
                com.niyaj.core.analytics.AnalyticsEvent.Param("absent_exported_to_file", totalAbsent.toString()),
            ),
        ),
    )
}
