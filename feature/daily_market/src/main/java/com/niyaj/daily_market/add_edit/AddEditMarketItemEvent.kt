package com.niyaj.daily_market.add_edit

sealed class AddEditMarketItemEvent {

    data class ItemTypeChanged(val type: String): AddEditMarketItemEvent()

    data class ItemNameChanged(val name: String): AddEditMarketItemEvent()

    data class ItemPriceChanged(val price: String): AddEditMarketItemEvent()

    data class ItemDescriptionChanged(val description: String): AddEditMarketItemEvent()

    data class ItemMeasureUnitChanged(val unit: String): AddEditMarketItemEvent()

    data object AddOrUpdateItem : AddEditMarketItemEvent()
}