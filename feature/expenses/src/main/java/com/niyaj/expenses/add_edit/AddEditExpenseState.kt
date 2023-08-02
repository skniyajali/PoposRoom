package com.niyaj.expenses.add_edit

import com.niyaj.common.utils.toMilliSecond
import java.time.LocalDate

data class AddEditExpenseState(
    val expenseName: String = "",
    val expenseDate: String = LocalDate.now().toMilliSecond,
    val expenseAmount: String = "",
    val expenseNote: String = "",
)


data class ExpensesName(
    val names: List<String> = emptyList(),
)