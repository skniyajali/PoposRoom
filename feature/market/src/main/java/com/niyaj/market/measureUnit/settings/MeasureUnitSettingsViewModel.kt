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

package com.niyaj.market.measureUnit.settings

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeasureUnitSettingsViewModel @Inject constructor(
    private val repository: MeasureUnitRepository,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel() {

    val items = snapshotFlow { mSearchText.value }.flatMapLatest {
        repository.getAllMeasureUnits(it)
    }.mapLatest { list ->
        totalItems = list.map { it.unitId }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    private val _exportedItems = MutableStateFlow<List<MeasureUnit>>(emptyList())
    val exportedItems = _exportedItems.asStateFlow()

    private val _importedItems = MutableStateFlow<List<MeasureUnit>>(emptyList())
    val importedItems = _importedItems.asStateFlow()

    fun onEvent(event: MeasureUnitSettingsEvent) {
        when (event) {
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
                        analyticsHelper.logExportedUnitsToFile(list.size)
                    }
                }
            }

            is MeasureUnitSettingsEvent.OnImportItemsFromFile -> {
                viewModelScope.launch {
                    _importedItems.value = emptyList()

                    if (event.data.isNotEmpty()) {
                        totalItems = event.data.map { it.unitId }
                        _importedItems.value = event.data
                        analyticsHelper.logImportedUnitsFromFile(event.data.size)
                    }
                }
            }

            is MeasureUnitSettingsEvent.ImportItemsToDatabase -> {
                viewModelScope.launch {
                    mIsLoading.update { true }
                    val data = if (mSelectedItems.isNotEmpty()) {
                        mSelectedItems.flatMap { unitId ->
                            _importedItems.value.filter { it.unitId == unitId }
                        }
                    } else {
                        _importedItems.value
                    }

                    when (val result = repository.importDataFromFilesToDatabase(data)) {
                        is Resource.Error -> {
                            mIsLoading.update { false }
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                        }

                        is Resource.Success -> {
                            mIsLoading.update { false }
                            mEventFlow.emit(UiEvent.OnSuccess("${data.size} items imported successfully"))
                            analyticsHelper.logImportedUnitsToDatabase(data.size)
                        }
                    }
                }
            }
        }
    }
}

private fun AnalyticsHelper.logImportedUnitsFromFile(totalUnits: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "measure_unit_imported_from_file",
            extras = listOf(
                AnalyticsEvent.Param("measure_unit_imported_from_file", totalUnits.toString()),
            ),
        ),
    )
}

private fun AnalyticsHelper.logImportedUnitsToDatabase(totalUnits: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "measure_unit_imported_to_database",
            extras = listOf(
                AnalyticsEvent.Param("measure_unit_imported_to_database", totalUnits.toString()),
            ),
        ),
    )
}

private fun AnalyticsHelper.logExportedUnitsToFile(totalItems: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "measure_unit_exported_to_file",
            extras = listOf(
                AnalyticsEvent.Param("measure_unit_exported_to_file", totalItems.toString()),
            ),
        ),
    )
}
