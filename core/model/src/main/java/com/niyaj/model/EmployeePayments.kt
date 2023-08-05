package com.niyaj.model

data class EmployeePayments(
    val startDate: String = "",
    val endDate: String = "",
    val payments: List<Payment> = emptyList(),
)

data class EmployeeMonthlyDate(
    val startDate: String = "",
    val endDate: String = "",
)

data class EmployeeSalaryEstimation(
    val startDate: String = "",
    val endDate: String = "",
    val status: String = "",
    val message: String? = null,
    val remainingAmount: String = "0",
    val paymentCount: String = "",
    val absentCount: String = ""
)