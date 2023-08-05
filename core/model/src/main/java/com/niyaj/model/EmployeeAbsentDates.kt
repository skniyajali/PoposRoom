package com.niyaj.model

data class EmployeeAbsentDates(
    val startDate: String = "",
    val endDate: String = "",
    val absentDates: List<String> = emptyList(),
)
