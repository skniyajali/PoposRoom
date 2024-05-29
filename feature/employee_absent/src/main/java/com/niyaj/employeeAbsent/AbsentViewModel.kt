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

package com.niyaj.employeeAbsent

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.AbsentRepository
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import com.samples.apps.core.analytics.AnalyticsEvent
import com.samples.apps.core.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AbsentViewModel @Inject constructor(
    private val absentRepository: AbsentRepository,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel() {

    override var totalItems: List<Int> = emptyList()

    @OptIn(ExperimentalCoroutinesApi::class)
    val absents = snapshotFlow { searchText.value }
        .flatMapLatest { it ->
            absentRepository.getAllEmployeeAbsents(it)
                .onStart { UiState.Loading }
                .map { items ->
                    totalItems = items.flatMap { item -> item.absents.map { it.absentId } }

                    if (items.all { it.absents.isEmpty() }) {
                        UiState.Empty
                    } else {
                        UiState.Success(items)
                    }
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading,
        )

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

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch {
            when (val result = absentRepository.deleteAbsents(selectedItems.toList())) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                }

                is Resource.Success -> {
                    mEventFlow.emit(UiEvent.OnSuccess("${selectedItems.size} absentees has been deleted"))
                    analyticsHelper.logDeletedAbsentees(selectedItems.toList())
                }
            }

            mSelectedItems.clear()
        }
    }
}

internal fun AnalyticsHelper.logDeletedAbsentees(data: List<Int>) {
    logEvent(
        event = AnalyticsEvent(
            type = "absent_employee_deleted",
            extras = listOf(
                AnalyticsEvent.Param("absent_employee_deleted", data.toString()),
            ),
        ),
    )
}
