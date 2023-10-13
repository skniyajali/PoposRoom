package com.niyaj.model

import com.niyaj.common.utils.toDateString

data class MarketListWithItems(
    val marketList: MarketList,
    val items: List<MarketListWithItem>,
)


fun List<MarketListWithItems>.searchMarketListItem(searchText: String): List<MarketListWithItems> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.marketList.marketDate.toDateString.contains(searchText, true) ||
                    it.marketList.createdAt.toDateString.contains(searchText, true) ||
                    it.marketList.updatedAt?.toDateString?.contains(searchText, true) == true
        }
    } else this
}