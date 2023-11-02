package com.niyaj.model

import com.niyaj.common.utils.toDateString
import kotlinx.coroutines.flow.Flow

data class MarketListWithItemsAndQuantity(
    val marketList: MarketList,
    val items: List<MarketItemWithQuantity>,
)

data class MarketListWithItems(
    val marketList: MarketList,
    val items: List<MarketListItem>,
)

data class MarketItemWithQuantity(
    val item: MarketItem,
    val doesExist: Flow<Boolean>,
    val quantity: Flow<ItemQuantityAndType?>
)


data class MarketItemAndQuantity(
    val item: MarketItem,
    val quantityAndType: ItemQuantityAndType
)

data class ItemQuantityAndType(
    val itemQuantity: Double = 0.0,
    val marketListType: MarketListType = MarketListType.Needed
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

fun List<MarketItemWithQuantity>.searchItems(searchText: String): List<MarketItemWithQuantity> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.item.itemName.contains(searchText, true) ||
                    it.item.itemDescription?.contains(searchText, true) == true ||
                    it.item.itemPrice?.contains(searchText, true) == true ||
                    it.item.itemType.contains(searchText, true) ||
                    it.item.createdAt.toDateString.contains(searchText, true)
        }
    }else this
}