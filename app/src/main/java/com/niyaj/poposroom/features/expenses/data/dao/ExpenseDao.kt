package com.niyaj.poposroom.features.expenses.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import com.niyaj.poposroom.features.expenses.domain.model.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query(value = """
        SELECT * FROM expense ORDER BY expenseDate DESC
    """)
    fun getAllExpense(): Flow<List<Expense>>

    @RawQuery(observedEntities = [Expense::class])
    fun getAllPagingExpenses(query: SupportSQLiteQuery): List<Expense>

    @Query(value = """
        SELECT DISTINCT expenseName FROM expense ORDER BY expenseDate DESC
    """)
    fun getAllExpenseName(): Flow<List<String>>

    @Query(value = """
        SELECT * FROM expense WHERE expenseId = :expenseId
    """)
    fun getExpenseById(expenseId: Int): Expense?

    /**
     * Inserts [Expense] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreExpense(expense: Expense): Long

    /**
     * Updates [Expense] in the db that match the primary key, and no-ops if they don't
     */
    @Update
    suspend fun updateExpense(expense: Expense): Int

    /**
     * Inserts or updates [Expense] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertExpense(expense: Expense): Long

    @Query(value = """
        DELETE FROM expense WHERE expenseId = :expenseId
    """)
    suspend fun deleteExpense(expenseId: Int): Int

    /**
     * Deletes rows in the db matching the specified [expenseIds]
     */
    @Query(
        value = """
            DELETE FROM expense
            WHERE expenseId in (:expenseIds)
        """,
    )
    suspend fun deleteExpense(expenseIds: List<Int>): Int

    @Query(value = """
        SELECT * FROM expense WHERE 
        expenseDate = :expenseDate AND expenseName = :expenseName LIMIT 1
    """)
    fun findExpenseByNameAndDate(expenseName: String, expenseDate: String): Expense?
}