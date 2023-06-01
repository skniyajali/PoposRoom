package com.niyaj.poposroom.features.common.utils

sealed class SheetScreen(val type: String) {
    object CreateNewAddOnItem : SheetScreen("Create New AddOn Item")
    data class UpdateAddOnItem(val itemId: Int) : SheetScreen("Update AddOn Item")
}