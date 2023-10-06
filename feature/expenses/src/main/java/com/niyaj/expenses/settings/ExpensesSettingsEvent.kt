package com.niyaj.expenses.settings

import com.niyaj.model.Expense

sealed class ExpensesSettingsEvent {

    data object GetExportedItems: ExpensesSettingsEvent()

    data class OnImportExpensesItemsFromFile(val data: List<Expense>): ExpensesSettingsEvent()

    data object ImportExpensesItemsToDatabase: ExpensesSettingsEvent()
}