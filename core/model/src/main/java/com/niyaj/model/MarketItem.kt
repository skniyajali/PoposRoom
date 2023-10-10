package com.niyaj.model

import com.niyaj.common.utils.toDateString
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MarketItem(
    val itemId: Int = 0,

    val itemType: String,

    val itemName: String,

    val itemPrice: String? = null,

    val itemDescription: String? = null,

    val itemMeasureUnit: String,

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long? = null,
)


fun List<MarketItem>.searchMarketItems(searchText: String): List<MarketItem> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.itemType.contains(searchText, true) ||
                    it.itemName.contains(searchText, true) ||
                    it.itemPrice?.contains(searchText, true) == true ||
                    it.itemDescription?.contains(searchText, true) == true ||
                    it.itemMeasureUnit.contains(searchText, true) ||
                    it.createdAt.toDateString.contains(searchText, true) ||
                    it.updatedAt?.toDateString?.contains(searchText, true) == true
        }
    } else this
}