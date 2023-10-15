package com.niyaj.daily_market.market_list.add_edit

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.safeDouble
import com.niyaj.common.utils.safeString
import com.niyaj.data.repository.MarketItemRepository
import com.niyaj.data.repository.MarketListRepository
import com.niyaj.ui.event.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditMarketListViewModel @Inject constructor(
    private val repository: MarketListRepository,
    private val itemRepository: MarketItemRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    private val marketId = savedStateHandle.get<Int>("marketId") ?: 0

    private val _itemWithQuantity = mutableStateListOf<MarketItemWithQuantityState>()
    val itemWithQuantity: SnapshotStateList<MarketItemWithQuantityState> = _itemWithQuantity

    private val _removedItems = mutableStateListOf<Int>()
    val removedItems: SnapshotStateList<Int> = _removedItems
    

    val items = snapshotFlow { _searchText.value }.flatMapLatest { searchText ->
        itemRepository.getAllMarketItems(searchText)
    }.mapLatest { itemList ->
        totalItems = itemList.map { it.itemId }
        itemList
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )


    fun removeItem(itemId: Int) {
        viewModelScope.launch {
            delay(500)

            if (!_removedItems.contains(itemId)) {
                _removedItems.add(itemId)
            }
        }
    }

    fun increaseQuantity(itemId: Int) {
        if (!mSelectedItems.contains(itemId)) {
            mSelectedItems.add(itemId)
        }

        val item = _itemWithQuantity.find { it.itemId == itemId }
        if (item == null) {
            _itemWithQuantity.add(
                MarketItemWithQuantityState(
                    itemId = itemId,
                    quantity = "0.5"
                )
            )
        } else {
            _itemWithQuantity[_itemWithQuantity.indexOf(item)] = MarketItemWithQuantityState(
                itemId = itemId,
                quantity = (item.quantity.safeDouble().plus(0.5)).safeString
            )
        }
    }

    fun decreaseQuantity(itemId: Int) {
        val item = _itemWithQuantity.find { it.itemId == itemId }

        if (item != null && item.quantity.safeDouble() > 0) {
            _itemWithQuantity[_itemWithQuantity.indexOf(item)] = MarketItemWithQuantityState(
                itemId = itemId,
                quantity = (item.quantity.safeDouble() - 0.5).safeString
            )
        }
    }

    fun onValueChanged(itemId: Int, quantity: String) {
        if (!mSelectedItems.contains(itemId)) {
            mSelectedItems.add(itemId)
        }

        val item = _itemWithQuantity.find { it.itemId == itemId }
        if (item == null) {
            _itemWithQuantity.add(
                MarketItemWithQuantityState(
                    itemId = itemId,
                    quantity = quantity
                )
            )
        } else {
            _itemWithQuantity[_itemWithQuantity.indexOf(item)] = MarketItemWithQuantityState(
                itemId = itemId,
                quantity = quantity
            )
        }
    }

    private fun getItemListById(marketId: Int) {
        viewModelScope.launch {

        }
    }
}