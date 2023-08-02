package com.niyaj.data.mapper

import com.niyaj.database.model.CategoryEntity
import com.niyaj.model.Category

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        categoryId = this.categoryId,
        categoryName = this.categoryName,
        isAvailable = this.isAvailable,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}