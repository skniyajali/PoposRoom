package com.niyaj.daily_market.measure_unit.add_edit

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
                    unitName = state.unitName.lowercase(),
                    unitValue = state.unitValue.safeDouble()
                )

                when(val result = repository.upsertMeasureUnit(newUnit)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                    }
                    is Resource.Success -> {
                        val message = if (unitId == 0) "Created" else "Updated"
                        _eventFlow.emit(UiEvent.OnSuccess("Item $message successfully."))
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