package com.niyaj.model

data class CategoryWiseReport(
    val category: Category,
    val productWithQuantity: List<ProductWiseReport>,
)
