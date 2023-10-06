package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {

    suspend fun getAllExpensesOnSpecificDate(searchText: String, givenDate: String): Flow<List<Expense>>

    suspend fun getAllExpenses(searchText: String): Flow<List<Expense>>

    suspend fun getAllPagingExpenses(searchText: String, limit: Int = 0, offset: Int = 0): List<Expense>

    suspend fun getAllExpenseName(searchText: String): Flow<List<String>>

    suspend fun findExpenseByNameAndDate(expenseName: String, expenseDate: String): Boolean

    suspend fun getExpenseById(expenseId: Int): Resource<Expense?>

    suspend fun addOrIgnoreExpense(newExpense: Expense): Resource<Boolean>

    suspend fun updateExpense(newExpense: Expense): Resource<Boolean>

    suspend fun upsertExpense(newExpense: Expense): Resource<Boolean>

    suspend fun deleteExpense(expenseId: Int): Resource<Boolean>

    suspend fun deleteExpenses(expenseIds: List<Int>): Resource<Boolean>

    suspend fun importExpensesDataToDatabase(expenses: List<Expense>): Resource<Boolean>
}