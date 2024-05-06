/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.market.measure_unit.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.safeDouble
import com.niyaj.common.utils.safeString
import com.niyaj.data.repository.MeasureUnitRepository
import com.niyaj.data.repository.validation.MeasureUnitValidationRepository
import com.niyaj.model.MeasureUnit
import com.niyaj.ui.utils.UiEvent
import com.samples.apps.core.analytics.AnalyticsEvent
import com.samples.apps.core.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditMeasureUnitViewModel @Inject constructor(
    private val repository: MeasureUnitRepository,
    private val validationRepository: MeasureUnitValidationRepository,
    private val analyticsHelper: AnalyticsHelper,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val unitId = savedStateHandle.get<Int>("unitId") ?: 0

    var state by mutableStateOf(AddEditMeasureUnitState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<Int>("unitId")?.let {
            getMeasureUnitById(it)
        }
    }

    val nameError = snapshotFlow { state.unitName }.mapLatest {
        validationRepository.validateUnitName(it, unitId).errorMessage
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val valueError = snapshotFlow { state.unitValue }.mapLatest {
        validationRepository.validateUnitValue(it).errorMessage
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun onEvent(event: AddEditMeasureUnitEvent) {
        when(event) {
            is AddEditMeasureUnitEvent.MeasureUnitNameChanged -> {
                state = state.copy(
                    unitName = event.unitName
                )
            }

            is AddEditMeasureUnitEvent.MeasureUnitValueChanged -> {
                state = state.copy(
                    unitValue = event.unitValue
                )
            }

            is AddEditMeasureUnitEvent.SaveOrUpdateMeasureUnit -> {
                saveOrUpdateMeasureUnit(unitId)
            }
        }
    }

    private fun saveOrUpdateMeasureUnit(unitId: Int) {
        viewModelScope.launch {
            if (listOf(nameError, valueError).all { it.value == null }) {
                val newUnit = MeasureUnit(
                    unitId = unitId,
                    unitName = state.unitName.trimEnd().lowercase(),
                    unitValue = state.unitValue.safeDouble()
                )

                when(val result = repository.upsertMeasureUnit(newUnit)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                    }
                    is Resource.Success -> {
                        val message = if (unitId == 0) "Created" else "Updated"
                        _eventFlow.emit(UiEvent.OnSuccess("Item $message successfully."))
                        analyticsHelper.logOnCreateOrUpdateMeasureUnit(unitId, message)
                    }
                }
            }
        }
    }

    private fun getMeasureUnitById(unitId: Int) {
        viewModelScope.launch {
            repository.getMeasureUnitById(unitId).data?.let {
                state = state.copy(
                    unitName = it.unitName,
                    unitValue = it.unitValue.safeString
                )
            }
        }
    }

}

private fun AnalyticsHelper.logOnCreateOrUpdateMeasureUnit(data: Int, message: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "measure_unit_$message",
            extras = listOf(
                AnalyticsEvent.Param("measure_unit_$message", data.toString()),
            ),
        ),
    )
}