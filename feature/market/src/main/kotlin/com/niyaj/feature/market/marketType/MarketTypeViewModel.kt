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

package com.niyaj.feature.market.marketType

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.MarketTypeRepository
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketTypeViewModel @Inject constructor(
    private val repository: MarketTypeRepository,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel() {

    val marketTypes = snapshotFlow { searchText.value }.flatMapLatest { data ->
        repository.getAllMarketTypes(data)
    }.onStart { UiState.Loading }.map { items ->
        totalItems = items.map { it.typeId }
        if (items.isEmpty()) {
            UiState.Empty
        } else {
            UiState.Success(items)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch {
            when (val result = repository.deleteMarketTypes(selectedItems.toList())) {
                is Resource.Success -> {
                    mEventFlow.emit(
                        UiEvent.OnSuccess(
                            "${selectedItems.size} item deleted successfully",
                        ),
                    )
                    analyticsHelper.logDeletedMarketTypes(selectedItems.toList())
                }

                is Resource.Error -> {
                    mEventFlow.emit(
                        UiEvent.OnError(result.message ?: "Unable to delete items"),
                    )
                }
            }

            mSelectedItems.clear()
        }
    }
}

private fun AnalyticsHelper.logDeletedMarketTypes(data: List<Int>) {
    logEvent(
        event = AnalyticsEvent(
            type = "market_type_deleted",
            extras = listOf(
                AnalyticsEvent.Param("market_type_deleted", data.toString()),
            ),
        ),
    )
}
