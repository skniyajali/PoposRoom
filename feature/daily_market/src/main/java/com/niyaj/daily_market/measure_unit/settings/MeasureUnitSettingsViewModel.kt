package com.niyaj.daily_market.measure_unit.settings

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.MeasureUnitRepository
import com.niyaj.model.MeasureUnit
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
class MeasureUnitSettingsViewModel @Inject constructor(
    private val repository: MeasureUnitRepository,
): BaseViewModel() {

    val items = snapshotFlow { mSearchText.value }.flatMapLatest {
        repository.getAllMeasureUnits(it)
    }.mapLatest { list ->
        totalItems = list.map { it.unitId }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _exportedItems = MutableStateFlow<List<MeasureUnit>>(emptyList())
    val exportedItems = _exportedItems.asStateFlow()

    private val _importedItems = MutableStateFlow<List<MeasureUnit>>(emptyList())
    val importedItems = _importedItems.asStateFlow()

    fun onEvent(event: MeasureUnitSettingsEvent) {
        when(event) {
            is MeasureUnitSettingsEvent.GetExportedItems -> {
                viewModelScope.launch {
                    if (mSelectedItems.isEmpty()) {
                        _exportedItems.value = items.value
                    } else {
                        val list = mutableListOf<MeasureUnit>()

                        mSelectedItems.forEach { id ->
                            val item = items.value.find { it.unitId == id }

                            if (item != null) {
                                list.add(item)
                            }
                        }

                        _exportedItems.emit(list.toList())
                    }
                }
            }

            is MeasureUnitSettingsEvent.OnImportItemsFromFile -> {
                viewModelScope.launch {
                    _importedItems.value = emptyList()

                    if (event.data.isNotEmpty()) {
                        totalItems = event.data.map { it.unitId }
                        _importedItems.value = event.data
                    }
                }
            }

            is MeasureUnitSettingsEvent.ImportItemsToDatabase -> {
                viewModelScope.launch {
                    val data = if (mSelectedItems.isNotEmpty()) {
                        mSelectedItems.flatMap {unitId ->
                            _importedItems.value.filter { it.unitId == unitId }
                        }
                    }else {
                        _importedItems.value
                    }

                    when(val result = repository.importDataFromFilesToDatabase(data)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                        }
                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.OnSuccess("${data.size} items imported successfully"))
                        }
                    }
                }
            }
        }
    }
}