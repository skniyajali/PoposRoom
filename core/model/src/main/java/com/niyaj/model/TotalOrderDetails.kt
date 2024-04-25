package com.niyaj.model

data class TotalOrderDetails(
    val totalAmount: Long = 0,
    val totalOrder: Int = 0,
    val repeatedOrder: Int = 0,
    val datePeriod: Pair<String, String> = Pair("", ""),
)
