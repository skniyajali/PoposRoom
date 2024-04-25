package com.niyaj.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddOnItem(
    val itemId: Int,

    val itemName: String,

    val itemPrice: Int,

    val isApplicable: Boolean,

    val createdAt: Long,

    val updatedAt: Long? = null,
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