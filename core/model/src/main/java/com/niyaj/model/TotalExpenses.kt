package com.niyaj.model

import androidx.compose.runtime.Stable

@Stable
data class TotalExpenses(
    val totalQuantity: Long = 0,
    val totalExpenses: Long = 0
)
