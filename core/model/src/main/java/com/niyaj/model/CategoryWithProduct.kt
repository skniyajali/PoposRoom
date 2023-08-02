package com.niyaj.model


data class CategoryWithProduct(
    val categoryId: Int,

    val productId: Int
)


data class CategoryWithProducts(
    val category: Category,

    val products: List<Product> = emptyList()
)