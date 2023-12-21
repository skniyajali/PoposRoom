package com.niyaj.model

import androidx.compose.runtime.Stable

@Stable
data class TotalSales(
    val expenses: TotalExpenses,
    val dineInOrders: TotalOrders,
    val dineOutOrders: TotalOrders,
)
