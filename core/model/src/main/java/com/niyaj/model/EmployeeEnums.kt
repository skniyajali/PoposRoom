package com.niyaj.model

import androidx.compose.runtime.Stable

@Stable
enum class EmployeeSalaryType {
    Daily,
    Monthly,
    Weekly
}

@Stable
enum class EmployeeType{
    PartTime,
    FullTime
}

@Stable
enum class PaymentType {
    Salary,
    Advanced
}

@Stable
enum class PaymentMode {
    Cash,
    Online,
    Both
}