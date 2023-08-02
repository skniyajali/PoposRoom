package com.niyaj.data.mapper

import com.niyaj.database.model.ProductEntity
import com.niyaj.model.Product

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        productId = this.productId,
        categoryId = this.categoryId,
        productName = this.productName,
        productPrice = this.productPrice,
        productDescription = this.productDescription,
        productAvailability = this.productAvailability,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}