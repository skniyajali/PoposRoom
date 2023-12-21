package com.niyaj.model

import androidx.compose.runtime.Stable

@Stable
data class CustomerWiseReport(
    val customer: Customer,
    val orderQty: Int = 0,
)
