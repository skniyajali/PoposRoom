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

package com.niyaj.market.marketType.settings

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.MarketTypeRepository
import com.niyaj.model.MarketType
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.utils.UiEvent
import com.samples.apps.core.analytics.AnalyticsEvent
import com.samples.apps.core.analytics.AnalyticsHelper
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
class MarketTypeSettingsViewModel @Inject constructor(
    private val repository: MarketTypeRepository,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel() {

    val items = snapshotFlow { mSearchText.value }.flatMapLatest {
        repository.getAllMarketTypes(it)
    }.mapLatest { list ->
        totalItems = list.map { it.typeId }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    private val _exportedItems = MutableStateFlow<List<MarketType>>(emptyList())
    val exportedItems = _exportedItems.asStateFlow()

    private val _importedItems = MutableStateFlow<List<MarketType>>(emptyList())
    val importedItems = _importedItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun onEvent(event: MarketTypeSettingsEvent) {
        when (event) {
            is MarketTypeSettingsEvent.GetExportedItems -> {
                viewModelScope.launch {
                    if (mSelectedItems.isEmpty()) {
                        _exportedItems.value = items.value
                    } else {
                        val list = mutableListOf<MarketType>()

                        mSelectedItems.forEach { id ->
                            val item = items.value.find { it.typeId == id }

                            if (item != null) {
                                list.add(item)
                            }
                        }

                        _exportedItems.emit(list.toList())
                        analyticsHelper.logExportedTypesToFile(list.size)
                    }
                }
            }

            is MarketTypeSettingsEvent.OnImportItemsFromFile -> {
                viewModelScope.launch {
                    _importedItems.value = emptyList()

                    if (event.data.isNotEmpty()) {
                        totalItems = event.data.map { it.typeId }
                        _importedItems.value = event.data
                        analyticsHelper.logImportedTypesFromFile(event.data.size)
                    }
                }
            }

            is MarketTypeSettingsEvent.ImportItemsToDatabase -> {
                viewModelScope.launch {
                    _isLoading.update { true }
                    val data = if (mSelectedItems.isNotEmpty()) {
                        mSelectedItems.flatMap { typeId ->
                            _importedItems.value.filter { it.typeId == typeId }
                        }
                    } else {
                        _importedItems.value
                    }

                    when (val result = repository.importDataFromFilesToDatabase(data)) {
                        is Resource.Error -> {
                            _isLoading.update { false }
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                        }

                        is Resource.Success -> {
                            _isLoading.update { false }
                            mEventFlow.emit(UiEvent.OnSuccess("${data.size} items imported successfully"))
                            analyticsHelper.logImportedTypesToDatabase(data.size)
                        }
                    }
                }
            }
        }
    }
}

private fun AnalyticsHelper.logImportedTypesFromFile(totalTypes: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "market_type_imported_from_file",
            extras = listOf(
                AnalyticsEvent.Param("market_type_imported_from_file", totalTypes.toString()),
            ),
        ),
    )
}

private fun AnalyticsHelper.logImportedTypesToDatabase(totalTypes: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "market_type_imported_to_database",
            extras = listOf(
                AnalyticsEvent.Param("market_type_imported_to_database", totalTypes.toString()),
            ),
        ),
    )
}

private fun AnalyticsHelper.logExportedTypesToFile(totalItems: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "market_type_exported_to_file",
            extras = listOf(
                AnalyticsEvent.Param("market_type_exported_to_file", totalItems.toString()),
            ),
        ),
    )
}
