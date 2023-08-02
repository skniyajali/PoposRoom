package com.niyaj.model

import com.niyaj.common.utils.toJoinedDate
import java.util.Date

data class Expense(
    val expenseId: Int = 0,

    val expenseName: String = "",

    val expenseAmount: String = "",

    val expenseDate: String = "",

    val expenseNote: String = "",

    val createdAt: Date = Date(),

    val updatedAt: Date? = null,
)


fun List<Expense>.searchExpense(searchText: String): List<Expense> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.expenseAmount.toString().contains(searchText, true) ||
                    it.expenseName.contains(searchText, true) ||
                    it.expenseDate.toJoinedDate.contains(searchText, true)
        }
    } else this
}