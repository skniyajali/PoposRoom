package com.niyaj.data.mapper

import com.niyaj.database.model.ExpenseEntity
import com.niyaj.model.Expense

fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        expenseId = this.expenseId,
        expenseName = this.expenseName,
        expenseAmount = this.expenseAmount,
        expenseDate = this.expenseDate,
        expenseNote = this.expenseNote,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}