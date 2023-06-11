package com.niyaj.poposroom.features.expenses.presentation.add_edit

sealed interface AddEditExpenseEvent {

    data class ExpensesNameChanged(val expenseName: String): AddEditExpenseEvent

    data class ExpensesAmountChanged(val expenseAmount: String): AddEditExpenseEvent

    data class ExpensesDateChanged(val expenseDate: String): AddEditExpenseEvent

    data class ExpensesNoteChanged(val expenseNote: String): AddEditExpenseEvent

    data class AddOrUpdateExpense(val expenseId: Int = 0): AddEditExpenseEvent
}