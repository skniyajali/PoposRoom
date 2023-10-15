package com.niyaj.daily_market.settings

import com.niyaj.model.MarketItem

sealed class MarketItemSettingsEvent {

    data object GetExportedMarketItem: MarketItemSettingsEvent()

    data class OnImportMarketItemsFromFile(val data: List<MarketItem>): MarketItemSettingsEvent()

    data object ImportMarketItemsToDatabase: MarketItemSettingsEvent()
}