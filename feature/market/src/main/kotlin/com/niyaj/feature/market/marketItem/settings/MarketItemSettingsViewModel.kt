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

package com.niyaj.feature.market.marketItem.settings

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsEvent.Param
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.MarketItemRepository
import com.niyaj.model.MarketItem
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
class MarketItemSettingsViewModel @Inject constructor(
    private val repository: MarketItemRepository,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel() {

    val items = snapshotFlow { mSearchText.value }.flatMapLatest {
        repository.getAllMarketItems(it)
    }.mapLatest { list ->
        totalItems = list.map { it.itemId }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    private val _exportedItems = MutableStateFlow<List<MarketItem>>(emptyList())
    val exportedItems = _exportedItems.asStateFlow()

    private val _importedItems = MutableStateFlow<List<MarketItem>>(emptyList())
    val importedItems = _importedItems.asStateFlow()

    fun onEvent(event: MarketItemSettingsEvent) {
        when (event) {
            is MarketItemSettingsEvent.GetExportedMarketItem -> {
                viewModelScope.launch {
                    if (mSelectedItems.isEmpty()) {
                        _exportedItems.value = items.value
                    } else {
                        val list = mutableListOf<MarketItem>()

                        mSelectedItems.forEach { id ->
                            val item = items.value.find { it.itemId == id }

                            if (item != null) {
                                list.add(item)
                            }
                        }

                        _exportedItems.emit(list.toList())
                        analyticsHelper.logExportedItemsToFile(list.size)
                    }
                }
            }

            is MarketItemSettingsEvent.OnImportMarketItemsFromFile -> {
                viewModelScope.launch {
                    _importedItems.value = emptyList()

                    if (event.data.isNotEmpty()) {
                        totalItems = event.data.map { it.itemId }
                        _importedItems.value = event.data
                        analyticsHelper.logImportedItemsFromFile(event.data.size)
                    }
                }
            }

            is MarketItemSettingsEvent.ImportMarketItemsToDatabase -> {
                viewModelScope.launch {
                    mIsLoading.update { true }
                    val data = if (mSelectedItems.isNotEmpty()) {
                        mSelectedItems.flatMap { itemId ->
                            _importedItems.value.filter { it.itemId == itemId }
                        }
                    } else {
                        _importedItems.value
                    }

                    when (val result = repository.importMarketItemsToDatabase(data)) {
                        is Resource.Error -> {
                            mIsLoading.update { false }
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                        }

                        is Resource.Success -> {
                            mIsLoading.update { false }
                            mEventFlow.emit(UiEvent.OnSuccess("${data.size} items imported successfully"))
                            analyticsHelper.logImportedItemsToDatabase(data.size)
                        }
                    }
                }
            }
        }
    }
}

internal fun AnalyticsHelper.logImportedItemsFromFile(totalItems: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "market_item_imported_from_file",
            extras = listOf(
                Param("market_item_imported_from_file", totalItems.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logImportedItemsToDatabase(totalItems: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "market_item_imported_to_database",
            extras = listOf(
                Param("market_item_imported_to_database", totalItems.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logExportedItemsToFile(totalItems: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "market_item_exported_to_file",
            extras = listOf(
                Param("market_item_exported_to_file", totalItems.toString()),
            ),
        ),
    )
}
