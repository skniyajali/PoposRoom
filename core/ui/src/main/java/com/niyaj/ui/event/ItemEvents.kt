package com.niyaj.ui.event

sealed interface ItemEvents {

    data class SelectItem(val itemId: Int): ItemEvents

    data object SelectAllItems: ItemEvents

    data object DeselectAllItems: ItemEvents

    data object DeleteItems: ItemEvents

    data object OnSearchClick: ItemEvents

    data class OnSearchTextChanged(val text: String): ItemEvents

    data object OnSearchTextClearClick: ItemEvents

    data object OnSearchBarCloseClick: ItemEvents

}