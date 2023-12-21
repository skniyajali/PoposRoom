package com.niyaj.model

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList

@Stable
data class CategoryWiseReport(
    val category: Category,
    val productWithQuantity: ImmutableList<ProductWiseReport>,
)
