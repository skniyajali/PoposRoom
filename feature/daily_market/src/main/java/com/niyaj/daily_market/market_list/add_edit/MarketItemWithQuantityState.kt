package com.niyaj.daily_market.market_list.add_edit

import com.niyaj.model.ItemQuantityAndType
import com.niyaj.model.MarketItem
import kotlinx.coroutines.flow.StateFlow

data class MarketItemWithQuantityState(
    val item: MarketItem,
    val doesExist: StateFlow<Boolean>,
    val quantity: StateFlow<ItemQuantityAndType>
)