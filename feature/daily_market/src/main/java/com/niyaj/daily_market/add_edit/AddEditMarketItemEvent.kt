package com.niyaj.daily_market.add_edit

import com.niyaj.model.MeasureUnit

sealed class AddEditMarketItemEvent {

    data class ItemTypeChanged(val type: String): AddEditMarketItemEvent()

    data class ItemNameChanged(val name: String): AddEditMarketItemEvent()

    data class ItemPriceChanged(val price: String): AddEditMarketItemEvent()

    data class ItemDescriptionChanged(val description: String): AddEditMarketItemEvent()

    data class ItemMeasureUnitChanged(val unit: MeasureUnit): AddEditMarketItemEvent()

    data class ItemMeasureUnitNameChanged(val unitName: String): AddEditMarketItemEvent()

    data object AddOrUpdateItem : AddEditMarketItemEvent()
}