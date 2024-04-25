package com.niyaj.model

import kotlinx.collections.immutable.ImmutableList

data class CategoryWiseReport(
    val category: Category,
    val productWithQuantity: ImmutableList<ProductWiseReport>,
)
