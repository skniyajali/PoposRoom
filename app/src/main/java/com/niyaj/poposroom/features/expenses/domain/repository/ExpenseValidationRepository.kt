package com.niyaj.poposroom.features.expenses.domain.repository

import com.niyaj.poposroom.features.common.utils.ValidationResult

interface ExpenseValidationRepository {
    fun validateExpenseName(expenseName: String): ValidationResult

    fun validateExpenseDate(expenseDate: String): ValidationResult

    fun validateExpenseAmount(expenseAmount: String): ValidationResult
}