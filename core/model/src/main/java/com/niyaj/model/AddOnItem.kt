package com.niyaj.model

import java.util.Date

data class AddOnItem(
    val itemId: Int,

    val itemName: String,

    val itemPrice: Int,

    val isApplicable: Boolean,

    val createdAt: Date,

    val updatedAt: Date? = null,
)

data class AddOnPriceWithApplicable(val itemPrice: Int, val isApplicable: Boolean)


fun List<AddOnItem>.searchAddOnItem(searchText: String): List<AddOnItem> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.itemName.contains(searchText, true) ||
                    it.itemPrice.toString().contains(searchText, true)
        }
    } else this
}