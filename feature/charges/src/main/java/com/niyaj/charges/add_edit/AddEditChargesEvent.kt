package com.niyaj.charges.add_edit


sealed interface AddEditChargesEvent {

    data class ChargesNameChanged(val chargesName: String) : AddEditChargesEvent

    data class ChargesPriceChanged(val chargesPrice: String) : AddEditChargesEvent

    data object ChargesApplicableChanged: AddEditChargesEvent

    data class CreateOrUpdateCharges(val chargesId: Int = 0) : AddEditChargesEvent
}