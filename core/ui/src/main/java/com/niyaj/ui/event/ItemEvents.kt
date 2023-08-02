package com.niyaj.ui.event

sealed interface ItemEvents {

    data class SelectItem(val itemId: Int): ItemEvents

    object SelectAllItems: ItemEvents

    object DeselectAllItems: ItemEvents

    object DeleteItems: ItemEvents

    object OnSearchClick: ItemEvents

    data class OnSearchTextChanged(val text: String): ItemEvents

    object OnSearchTextClearClick: ItemEvents

    object OnSearchBarCloseClick: ItemEvents

}