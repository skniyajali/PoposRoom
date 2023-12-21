package com.niyaj.model

import androidx.compose.runtime.Stable

@Stable
data class AddressWiseReport(
    val address: Address,
    val orderQty: Int,
)
