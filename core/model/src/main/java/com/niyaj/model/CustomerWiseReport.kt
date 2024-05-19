package com.niyaj.model

data class CustomerWiseReport(
    val customerId: Int,
    val customerPhone: String,
    val customerName: String? = null,
    val customerEmail: String? = null,
    val totalSales: Int,
    val totalOrders: Int,
)
