package com.niyaj.addonitem.settings

import com.niyaj.model.AddOnItem

sealed class AddOnSettingsEvent {
    data object GetExportedItems: AddOnSettingsEvent()

    data class OnImportAddOnItemsFromFile(val data: List<AddOnItem>): AddOnSettingsEvent()

    data object ImportAddOnItemsToDatabase: AddOnSettingsEvent()
}