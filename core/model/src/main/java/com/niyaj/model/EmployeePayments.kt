package com.niyaj.model

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class EmployeePayments(
    val startDate: String = "",
    val endDate: String = "",
    val payments: ImmutableList<Payment> = persistentListOf(),
)

@Stable
data class EmployeeMonthlyDate(
    val startDate: String = "",
    val endDate: String = "",
)

@Stable
data class EmployeeSalaryEstimation(
    val startDate: String = "",
    val endDate: String = "",
    val status: String = "",
    val message: String? = null,
    val remainingAmount: String = "0",
    val paymentCount: String = "",
    val absentCount: String = ""
)