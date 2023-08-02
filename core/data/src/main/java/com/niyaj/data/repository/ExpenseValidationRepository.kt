package com.niyaj.data.repository

import com.niyaj.common.result.ValidationResult

interface ExpenseValidationRepository {
    fun validateExpenseName(expenseName: String): ValidationResult

    fun validateExpenseDate(expenseDate: String): ValidationResult

    fun validateExpenseAmount(expenseAmount: String): ValidationResult
}