package com.niyaj.model

import kotlinx.collections.immutable.ImmutableList

data class CategoryWiseReport(
    val categoryName: String,
    val productWithQuantity: ImmutableList<ProductWiseReport>,
)

data class CategoryWithProduct(
    val categoryName: String,
    val productId: Int,
    val productName: String,
    val quantity: Int,
)