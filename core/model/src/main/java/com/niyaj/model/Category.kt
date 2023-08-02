package com.niyaj.model

import java.util.Date

data class Category(
    val categoryId: Int = 0,

    val categoryName: String = "",

    val isAvailable: Boolean = true,

    val createdAt: Date = Date(),

    val updatedAt: Date? = null,
)


fun List<Category>.searchCategory(searchText: String): List<Category> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.categoryName.contains(searchText, true)
        }
    }else this
}

fun Category.filterCategory(searchText: String): Boolean {
    return if (searchText.isNotEmpty()) {
        this.categoryName.contains(searchText, true)
    }else true
}