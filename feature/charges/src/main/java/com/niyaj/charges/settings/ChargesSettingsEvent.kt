package com.niyaj.charges.settings

import com.niyaj.model.AddOnItem
import com.niyaj.model.Charges

sealed class ChargesSettingsEvent {
    data object GetExportedItems: ChargesSettingsEvent()

    data class OnImportChargesItemsFromFile(val data: List<Charges>): ChargesSettingsEvent()

    data object ImportChargesItemsToDatabase: ChargesSettingsEvent()
}