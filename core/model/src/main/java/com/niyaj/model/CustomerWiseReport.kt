package com.niyaj.model

data class CustomerWiseReport(
    val customer: Customer,
    val orderQty: Int = 0,
)
