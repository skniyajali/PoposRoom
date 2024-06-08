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

package com.niyaj.market.marketList.createOrUpdate

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.getStartTime
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.MarketListRepository
import com.niyaj.model.MarketList
import com.niyaj.model.MarketListWithType
import com.niyaj.model.MarketListWithTypes
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditMarketListViewModel @Inject constructor(
    private val repository: MarketListRepository,
    private val analyticsHelper: AnalyticsHelper,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val marketId = savedStateHandle.get<Int>("marketId") ?: 0

    private val _selectedDate = MutableStateFlow(getStartTime)
    val selectedDate = _selectedDate.asStateFlow()

    private val selectedListTypes = mutableStateListOf<MarketListWithType>()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val marketTypes = snapshotFlow { marketId }.flatMapLatest {
        repository.getAllMarketTypes()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    init {
        savedStateHandle.get<Int>("marketId")?.let {
            if (it != 0) getMarketListByMarketId(it)
        }
    }

    val isError = snapshotFlow { selectedListTypes.size }.mapLatest {
        selectedListTypes.isEmpty()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = true,
    )

    fun updateSelectedDate(date: String) {
        viewModelScope.launch {
            _selectedDate.update { date }
        }
    }

    fun updateSelectedListTypes(typeId: Int, listName: String) {
        viewModelScope.launch {
            val newType = MarketListWithType(
                typeId = typeId,
                listType = listName,
                listWithTypeId = 0,
                typeName = "",
            )

            if (selectedListTypes.any { it.typeId == typeId && it.listType == listName }) {
                selectedListTypes.removeIf { it.typeId == typeId && it.listType == listName }
            } else {
                selectedListTypes.add(newType)
            }
        }
    }

    fun isListTypeChecked(typeId: Int, listName: String): Boolean {
        return selectedListTypes.any { it.typeId == typeId && it.listType == listName }
    }

    fun isTypeChecked(typeId: Int): Boolean {
        return selectedListTypes.any { it.typeId == typeId }
    }

    fun createOrUpdateMarketList() {
        viewModelScope.launch {
            if (selectedListTypes.isNotEmpty()) {
                val marketList = MarketListWithTypes(
                    marketList = MarketList(
                        marketId = marketId,
                        marketDate = _selectedDate.value.toLong(),
                        createdAt = System.currentTimeMillis(),
                        updatedAt = if (marketId == 0) System.currentTimeMillis() else null,
                    ),
                    marketTypes = selectedListTypes,
                )

                val result = repository.upsertMarketList(marketList)
                val message = if (marketId == 0) "created" else "updated"

                when (result) {
                    is Resource.Error -> {
                        _eventFlow.emit(
                            UiEvent.OnError(result.message.toString()),
                        )
                    }

                    is Resource.Success -> {
                        _eventFlow.emit(
                            UiEvent.OnSuccess("Market list $message successfully"),
                        )
                        analyticsHelper.logOnCreateOrUpdateMarketList(marketId, message)
                    }
                }

                selectedListTypes.clear()
            }
        }
    }

    private fun getMarketListByMarketId(marketId: Int) {
        viewModelScope.launch {
            repository.getMarketListById(marketId).collect { marketList ->
                marketList?.let { withTypes ->
                    _selectedDate.value = withTypes.marketList.marketDate.toString()

                    selectedListTypes.addAll(withTypes.marketTypes)
                }
            }
        }
    }
}

private fun AnalyticsHelper.logOnCreateOrUpdateMarketList(data: Int, message: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "market_list_$message",
            extras = listOf(
                com.niyaj.core.analytics.AnalyticsEvent.Param("market_list_$message", data.toString()),
            ),
        ),
    )
}
