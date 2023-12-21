package com.niyaj.model

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class CategoryWithProducts(
    val category: Category,

    val products: ImmutableList<Product> = persistentListOf()
)