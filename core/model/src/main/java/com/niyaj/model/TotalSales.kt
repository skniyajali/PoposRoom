package com.niyaj.model

data class TotalSales(
    val expenses: TotalExpenses,
    val dineInOrders: TotalOrders,
    val dineOutOrders: TotalOrders,
)
