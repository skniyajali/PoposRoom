package com.niyaj.daily_market.measure_unit.settings

import com.niyaj.model.MeasureUnit

sealed class MeasureUnitSettingsEvent {

    data object GetExportedItems: MeasureUnitSettingsEvent()

    data class OnImportItemsFromFile(val data: List<MeasureUnit>): MeasureUnitSettingsEvent()

    data object ImportItemsToDatabase: MeasureUnitSettingsEvent()
}