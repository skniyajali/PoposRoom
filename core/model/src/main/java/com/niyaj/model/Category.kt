package com.niyaj.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Category(
    val categoryId: Int = 0,

    val categoryName: String = "",

    val isAvailable: Boolean = true,

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long? = null,
)


fun List<Category>.searchCategory(searchText: String): List<Category> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.categoryName.contains(searchText, true)
        }
    } else this
}

fun Category.filterCategory(searchText: String): Boolean {
    return if (searchText.isNotEmpty()) {
        this.categoryName.contains(searchText, true)
    } else true
}