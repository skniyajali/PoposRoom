package com.niyaj.poposroom.features.addon_item.presentation.add_edit

sealed class AddEditAddOnItemEvent {

    data class ItemNameChanged(val itemName: String) : AddEditAddOnItemEvent()

    data class ItemPriceChanged(val itemPrice: String) : AddEditAddOnItemEvent()

    object ItemApplicableChanged: AddEditAddOnItemEvent()

    data class CreateUpdateAddOnItem(val addOnItemId: Int = 0) : AddEditAddOnItemEvent()
}
