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

package com.niyaj.feature.market.marketItem

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsEvent.Param
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.MarketItemRepository
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
class MarketItemViewModel @Inject constructor(
    private val marketItemRepository: MarketItemRepository,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel() {

    val marketItems = snapshotFlow { searchText.value }
        .flatMapLatest { result ->
            marketItemRepository.getAllMarketItems(result)
                .onStart { UiState.Loading }
                .map { items ->
                    totalItems = items.map { it.itemId }
                    if (items.isEmpty()) {
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

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch {
            when (val result = marketItemRepository.deleteMarketItems(selectedItems.toList())) {
                is Resource.Success -> {
                    mEventFlow.emit(
                        UiEvent.OnSuccess(
                            "${selectedItems.size} item deleted successfully",
                        ),
                    )
                    analyticsHelper.logDeletedMarketItems(selectedItems.toList())
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

private fun AnalyticsHelper.logDeletedMarketItems(data: List<Int>) {
    logEvent(
        event = AnalyticsEvent(
            type = "market_item_deleted",
            extras = listOf(Param("market_item_deleted", data.toString())),
        ),
    )
}
