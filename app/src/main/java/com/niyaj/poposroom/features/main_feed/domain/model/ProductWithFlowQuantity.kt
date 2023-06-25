package com.niyaj.poposroom.features.main_feed.domain.model

import com.niyaj.poposroom.features.common.utils.getAllCapitalizedLetters
import kotlinx.coroutines.flow.Flow

data class ProductWithFlowQuantity(
    val categoryId: Int,
    val productId: Int,
    val productName: String,
    val productPrice: Int,
    val quantity: Flow<Int>
)

fun ProductWithFlowQuantity.filterByCategory(categoryId : Int): Boolean {
    return if (categoryId != 0) {
        this.categoryId == categoryId
    }else true
}

fun ProductWithFlowQuantity.filterBySearch(searchText: String): Boolean {
    return if (searchText.isNotEmpty()) {
        this.productName.contains(searchText, true) ||
                this.productPrice.toString().contains(searchText, true) ||
                getAllCapitalizedLetters(this.productName).contains(searchText, true)
    }else true
}