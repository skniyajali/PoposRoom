package com.niyaj.poposroom.features.common.utils

sealed class SheetScreen(val type: String) {
    object CreateNewAddOnItem : SheetScreen("Create New AddOn Item")
    data class UpdateAddOnItem(val itemId: Int) : SheetScreen("Update AddOn Item")

    object CreateNewAddress : SheetScreen("Create New Address")
    data class UpdateAddress(val addressId: Int) : SheetScreen("Update Address")

    object CreateNewCharges : SheetScreen("Create New Charges")
    data class UpdateCharges(val chargesId: Int) : SheetScreen("Update Charges")

    object CreateNewCategory : SheetScreen("Create New Category")
    data class UpdateCategory(val categoryId: Int) : SheetScreen("Update Category")
}