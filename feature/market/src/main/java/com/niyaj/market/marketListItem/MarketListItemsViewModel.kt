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

package com.niyaj.market.marketListItem

import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.toFormattedDate
import com.niyaj.common.utils.toListString
import com.niyaj.common.utils.toSafeString
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.MarketListItemRepository
import com.niyaj.feature.printer.bluetoothPrinter.BluetoothPrinter
import com.niyaj.model.MarketItemAndQuantity
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketListItemsViewModel @Inject constructor(
    private val repository: MarketListItemRepository,
    private val analyticsHelper: AnalyticsHelper,
    savedStateHandle: SavedStateHandle,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
    private val bluetoothPrinter: BluetoothPrinter,
) : BaseViewModel() {

    private val listTypeIds = savedStateHandle.get<IntArray>("listTypeIds") ?: intArrayOf()

    val marketDetail = snapshotFlow { listTypeIds }.flatMapLatest {
        repository.getMarketListById(it.first())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val marketItems = snapshotFlow { mSearchText.value }.flatMapLatest {
        repository.getMarketItemsByTypeIds(listTypeIds.asList(), it)
    }.mapLatest { list ->
        if (list.isEmpty()) UiState.Empty else UiState.Success(list)
    }.flowOn(ioDispatcher).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    val shareableMarketList = snapshotFlow { listTypeIds }.flatMapLatest {
        repository.getShareableMarketListByIds(it.asList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    fun onAddItem(listTypeId: Int, itemId: Int) {
        viewModelScope.launch(ioDispatcher) {
            when (val result = repository.addMarketListItem(listTypeId, itemId)) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message.toString()))
                }

                is Resource.Success -> {}
            }
        }
    }

    fun onRemoveItem(listTypeId: Int, itemId: Int) {
        viewModelScope.launch(ioDispatcher) {
            when (val result = repository.removeMarketListItem(listTypeId, itemId)) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message.toString()))
                }

                is Resource.Success -> {}
            }
        }
    }

    fun onIncreaseQuantity(listTypeId: Int, itemId: Int) {
        viewModelScope.launch(ioDispatcher) {
            when (val result = repository.increaseMarketListItemQuantity(listTypeId, itemId)) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message.toString()))
                }

                is Resource.Success -> {}
            }
        }
    }

    fun onDecreaseQuantity(listTypeId: Int, itemId: Int) {
        viewModelScope.launch(ioDispatcher) {
            when (val result = repository.decreaseMarketListItemQuantity(listTypeId, itemId)) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                }

                is Resource.Success -> {}
            }
        }
    }

    fun printMarketList() {
        viewModelScope.launch {
            try {
                val marketDate =
                    (marketDetail.value?.marketDate ?: System.currentTimeMillis()).toString()
                val marketList = repository
                    .getShareableMarketListByIds(listTypeIds.asList()).stateIn(this)

                bluetoothPrinter.connectBluetoothPrinter()
                val escposPrinter = bluetoothPrinter.printer

                escposPrinter?.let { printer ->
                    var printItems = ""

                    printItems += bluetoothPrinter.getPrintableHeader(
                        title = "MARKET LIST",
                        marketDate,
                    )
                    printItems += getPrintableItems(marketList.value)

                    printItems += "[L]-------------------------------\n"
                    printItems += "[C]{^..^}--END OF REPORTS--{^..^}\n"
                    printItems += "[L]-------------------------------\n"

                    printer.printFormattedTextAndCut(printItems, 10f)
                    analyticsHelper.logPrintMarketList(listTypeIds.asList(), marketDate)
                }
            } catch (e: Exception) {
                viewModelScope.launch {
                    mEventFlow.emit(UiEvent.OnError("Unable to print"))
                }
            }
        }
    }

    private fun getPrintableItems(marketList: List<MarketItemAndQuantity>): String {
        var printableString = ""

        val groupByType = marketList.groupBy {
            it.typeName
        }

        groupByType.forEach { (itemType, groupedByType) ->
            val groupByListType = groupedByType.groupBy { it.listType }

            if (groupByListType.isEmpty()) {
                printableString += "[L]You have not added any item in the list\n"
            }

            groupByListType.forEach { (listType, groupedByList) ->

                printableString += "[L]-------------------------------\n"
                printableString += "[L]$itemType [R]$listType[${groupedByList.size}]\n"
                printableString += "[L]-------------------------------\n"

                groupedByList.fastForEachIndexed { i, it ->
                    printableString += "[L]${it.itemName} [R]${it.itemQuantity?.toSafeString()} ${it.unitName}\n"

                    if (i != groupedByList.size - 1) {
                        printableString += "[L]-------------------------------\n"
                    }
                }

                printableString += "[L]\n\n\n"
            }
        }

        return printableString
    }
}

private fun AnalyticsHelper.logPrintMarketList(listTypeIds: List<Int>, marketDate: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "market_list_printed_for",
            extras = listOf(
                AnalyticsEvent.Param("market_list_printed_for", "${marketDate.toFormattedDate} - ${listTypeIds.toListString()}"),
            ),
        ),
    )
}
