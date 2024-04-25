package com.niyaj.model

import com.niyaj.model.utils.getCapitalWord

data class ProductWithQuantity(
    val categoryId: Int,
    val productId: Int,
    val productName: String,
    val productPrice: Int,
    val quantity: Int = 0,
)

fun List<ProductWithQuantity>.filterByCategory(categoryId: Int): List<ProductWithQuantity> {
    return this.filter {
        if (categoryId != 0) {
            it.categoryId == categoryId
        } else true
    }
}

fun List<ProductWithQuantity>.filterBySearch(searchText: String): List<ProductWithQuantity> {
    return this.filter {
        if (searchText.isNotEmpty()) {
            it.productName.contains(searchText, true) ||
                    it.productPrice.toString().contains(searchText, true) ||
                    it.productName.getCapitalWord().contains(searchText, true)
        } else true
    }
}