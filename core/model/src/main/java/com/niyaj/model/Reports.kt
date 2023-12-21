package com.niyaj.model

import androidx.compose.runtime.Stable

@Stable
data class Reports(
    val reportId: Int = 0,

    val expensesQty: Long = 0,
    val expensesAmount: Long = 0,

    val dineInSalesQty: Long = 0,
    val dineInSalesAmount: Long = 0,

    val dineOutSalesQty: Long = 0,
    val dineOutSalesAmount: Long = 0,

    val reportDate: String = "",

    val createdAt: String = System.currentTimeMillis().toString(),

    val updatedAt: String? = null,
)