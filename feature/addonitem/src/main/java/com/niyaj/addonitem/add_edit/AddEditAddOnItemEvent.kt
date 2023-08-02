package com.niyaj.addonitem.add_edit

sealed class AddEditAddOnItemEvent {

    data class ItemNameChanged(val itemName: String) : AddEditAddOnItemEvent()

    data class ItemPriceChanged(val itemPrice: String) : AddEditAddOnItemEvent()

    data object ItemApplicableChanged : AddEditAddOnItemEvent()

    data class CreateUpdateAddOnItem(val addOnItemId: Int = 0) : AddEditAddOnItemEvent()
}
