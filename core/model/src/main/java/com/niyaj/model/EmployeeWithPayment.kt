package com.niyaj.model

import com.squareup.moshi.JsonClass
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@JsonClass(generateAdapter = true)
data class EmployeeWithPayments(
    val employee: Employee,

    val payments: ImmutableList<Payment> = persistentListOf(),
)


fun List<EmployeeWithPayments>.searchEmployeeWithPayments(searchText: String): List<EmployeeWithPayments> {
    return if (searchText.isNotEmpty()) {
        this.filter { withSalary ->
            withSalary.employee.filterEmployee(searchText) ||
                    withSalary.payments.any { it.filterPayment(searchText) }
        }
    } else this
}