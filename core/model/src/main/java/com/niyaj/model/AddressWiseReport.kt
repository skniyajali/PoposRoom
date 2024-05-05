package com.niyaj.model

data class AddressWiseReport(
    val addressId: Int,
    val addressName: String,
    val shortName: String,
    val totalSales: Int,
    val totalOrders: Int,
)
