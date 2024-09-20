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

package com.niyaj.feature.market.marketListItem

import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.toFormattedDate
import com.niyaj.common.utils.toSafeString
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsEvent.Param
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.MarketListItemRepository
import com.niyaj.feature.printer.bluetoothPrinter.BluetoothPrinter
import com.niyaj.model.MarketItemAndQuantity
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketListItemViewModel @Inject constructor(
    private val repository: MarketListItemRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val bluetoothPrinter: BluetoothPrinter,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    private val listTypeId = savedStateHandle.get<Int>("listTypeId") ?: 0

    val marketDetail = snapshotFlow { listTypeId }.flatMapLatest {
        repository.getMarketListById(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val marketItems = snapshotFlow { mSearchText.value }.flatMapLatest {
        repository.getAllMarketItemByTypeId(listTypeId, it)
    }.mapLatest { list ->
        if (list.isEmpty()) UiState.Empty else UiState.Success(list)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    val shareableMarketList = snapshotFlow { listTypeId }.flatMapLatest {
        repository.getShareableMarketListById(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    fun onAddItem(itemId: Int) {
        viewModelScope.launch {
            when (val result = repository.addMarketListItem(listTypeId, itemId)) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message.toString()))
                }

                is Resource.Success -> {}
            }
        }
    }

    fun onRemoveItem(itemId: Int) {
        viewModelScope.launch {
            when (val result = repository.removeMarketListItem(listTypeId, itemId)) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message.toString()))
                }

                is Resource.Success -> {}
            }
        }
    }

    fun onIncreaseQuantity(itemId: Int) {
        viewModelScope.launch {
            when (val result = repository.increaseMarketListItemQuantity(listTypeId, itemId)) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message.toString()))
                }

                is Resource.Success -> {}
            }
        }
    }

    fun onDecreaseQuantity(itemId: Int) {
        viewModelScope.launch {
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
                    .getShareableMarketListById(listTypeId).stateIn(this)

                bluetoothPrinter
                    .connectAndGetBluetoothPrinterAsync()
                    .onSuccess {
                        it?.let { printer ->
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
                            analyticsHelper.logPrintMarketList(listTypeId, marketDate)
                        }
                    }.onFailure {
                        mEventFlow.emit(UiEvent.OnError("Printer Not Connected"))
                    }
            } catch (e: Exception) {
                mEventFlow.emit(UiEvent.OnError("Printer Not Connected"))
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
                printableString += "[L]<b>$itemType</b> [R]$listType[${groupedByList.size}]\n"
                printableString += "[L]-------------------------------\n"

                groupedByList.fastForEachIndexed { i, item ->
                    printableString += "[L]${item.itemName} [R]${item.itemQuantity?.toSafeString()} ${item.unitName}\n"

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

private fun AnalyticsHelper.logPrintMarketList(listTypeId: Int, marketDate: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "market_list_printed_for",
            extras = listOf(
                Param(
                    "market_list_printed_for",
                    "${marketDate.toFormattedDate} - $listTypeId",
                ),
            ),
        ),
    )
}
