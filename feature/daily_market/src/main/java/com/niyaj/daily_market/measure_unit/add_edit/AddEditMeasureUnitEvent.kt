package com.niyaj.daily_market.measure_unit.add_edit

sealed class AddEditMeasureUnitEvent {

    data class MeasureUnitNameChanged(val unitName: String): AddEditMeasureUnitEvent()

    data class MeasureUnitValueChanged(val unitValue: String): AddEditMeasureUnitEvent()

    data object SaveOrUpdateMeasureUnit: AddEditMeasureUnitEvent()
}