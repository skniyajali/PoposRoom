package com.niyaj.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EmployeeWithPayments(
    val employee: Employee,

    val payments: List<Payment> = emptyList(),
)


fun List<EmployeeWithPayments>.searchEmployeeWithPayments(searchText: String): List<EmployeeWithPayments> {
    return if (searchText.isNotEmpty()) {
        this.filter { withSalary ->
            withSalary.employee.filterEmployee(searchText) ||
                    withSalary.payments.any { it.filterPayment(searchText) }
        }
    } else this
}