package com.niyaj.poposroom.features.expenses.presentation.add_edit

import com.niyaj.poposroom.features.common.utils.toMilliSecond
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