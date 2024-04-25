package com.niyaj.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class CategoryWithProducts(
    val category: Category,

    val products: ImmutableList<Product> = persistentListOf(),
)