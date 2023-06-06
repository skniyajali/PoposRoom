package com.niyaj.poposroom.features.employee_payment.domain.model

data class SalaryCalculation(
    val startDate: String = "",
    val endDate: String = "",
    val status: String = "",
    val message: String? = null,
    val payments: List<Payment> = emptyList(),
)

data class SalaryCalculableDate(
    val startDate: String = "",
    val endDate: String = "",
)

data class CalculatedSalary(
    val startDate: String = "",
    val endDate: String = "",
    val status: String = "",
    val message: String? = null,
    val remainingAmount: String = "0",
    val paymentCount: String = "",
    val absentCount: String = ""
)