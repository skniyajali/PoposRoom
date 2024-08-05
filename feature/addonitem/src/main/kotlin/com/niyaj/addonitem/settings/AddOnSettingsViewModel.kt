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

package com.niyaj.addonitem.settings

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.AddOnItemRepository
import com.niyaj.model.AddOnItem
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
class AddOnSettingsViewModel @Inject constructor(
    private val addOnItemRepository: AddOnItemRepository,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel() {

    val addonItems = snapshotFlow { mSearchText.value }.flatMapLatest {
        addOnItemRepository.getAllAddOnItem(it)
    }.mapLatest { list ->
        totalItems = list.map { it.itemId }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    private val _exportedItems = MutableStateFlow<List<AddOnItem>>(emptyList())
    val exportedItems = _exportedItems.asStateFlow()

    private val _importedItems = MutableStateFlow<List<AddOnItem>>(emptyList())
    val importedItems = _importedItems.asStateFlow()

    fun onEvent(event: AddOnSettingsEvent) {
        when (event) {
            is AddOnSettingsEvent.GetExportedItems -> {
                viewModelScope.launch {
                    if (mSelectedItems.isEmpty()) {
                        _exportedItems.value = addonItems.value
                    } else {
                        val list = mutableListOf<AddOnItem>()

                        mSelectedItems.forEach { id ->
                            val category = addonItems.value.find { it.itemId == id }

                            if (category != null) {
                                list.add(category)
                            }
                        }

                        _exportedItems.emit(list.toList())
                        analyticsHelper.logExportedAddOnsToFile(list.size)
                    }
                }
            }

            is AddOnSettingsEvent.OnImportAddOnItemsFromFile -> {
                viewModelScope.launch {
                    _importedItems.value = emptyList()

                    if (event.data.isNotEmpty()) {
                        totalItems = event.data.map { it.itemId }
                        _importedItems.value = event.data
                    }

                    analyticsHelper.logImportedAddOnsFromFile(event.data.size)
                }
            }

            is AddOnSettingsEvent.ImportAddOnItemsToDatabase -> {
                viewModelScope.launch {
                    mIsLoading.update { true }
                    val data = if (mSelectedItems.isNotEmpty()) {
                        mSelectedItems.flatMap { itemId ->
                            _importedItems.value.filter { it.itemId == itemId }
                        }
                    } else {
                        _importedItems.value
                    }

                    when (val result = addOnItemRepository.importAddOnItemsToDatabase(data)) {
                        is Resource.Error -> {
                            mIsLoading.update { false }
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                        }

                        is Resource.Success -> {
                            mIsLoading.update { false }
                            mEventFlow.emit(UiEvent.OnSuccess("${data.size} items has been imported successfully"))
                            analyticsHelper.logImportedAddOnsToDatabase(data.size)
                        }
                    }
                }
            }
        }
    }
}

private fun AnalyticsHelper.logImportedAddOnsFromFile(totalAddOns: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "addon_item_imported_from_file",
            extras = listOf(
                AnalyticsEvent.Param("addon_item_imported_from_file", totalAddOns.toString()),
            ),
        ),
    )
}

private fun AnalyticsHelper.logImportedAddOnsToDatabase(totalAddOns: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "addon_item_imported_to_database",
            extras = listOf(
                AnalyticsEvent.Param("addon_item_imported_to_database", totalAddOns.toString()),
            ),
        ),
    )
}

private fun AnalyticsHelper.logExportedAddOnsToFile(totalAddOns: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "addon_item_exported_to_file",
            extras = listOf(
                AnalyticsEvent.Param("addon_item_exported_to_file", totalAddOns.toString()),
            ),
        ),
    )
}
