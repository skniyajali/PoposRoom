package com.niyaj.daily_market.market_list

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.startOfDayTime
import com.niyaj.data.repository.MarketListRepository
import com.niyaj.model.MarketList
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketListViewModel @Inject constructor(
    private val marketListRepository: MarketListRepository,
) : BaseViewModel() {

    val items = snapshotFlow { _searchText.value }.flatMapLatest {
        marketListRepository.getAllMarketLists(it)
    }.mapLatest { items ->
        totalItems = items.map { it.marketList.marketId }
        if (items.isEmpty()) UiState.Empty else UiState.Success(items)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        UiState.Loading
    )

    fun createNewList() {
        viewModelScope.launch {
            val newList = MarketList(
                marketDate = startOfDayTime.toLong(),
                createdAt = System.currentTimeMillis()
            )

            marketListRepository.addOrIgnoreMarketList(newList)
        }
    }
}