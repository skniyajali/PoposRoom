package com.niyaj.daily_market.add_edit

sealed class AddEditMarketListEvent {

    data class ItemTypeChanged(val type: String): AddEditMarketListEvent()

    data class ItemNameChanged(val name: String): AddEditMarketListEvent()

    data class ItemPriceChanged(val price: String): AddEditMarketListEvent()

    data class ItemDescriptionChanged(val description: String): AddEditMarketListEvent()

    data class ItemMeasureUnitChanged(val unit: String): AddEditMarketListEvent()

    data object AddOrUpdateItem : AddEditMarketListEvent()
}