package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niyaj.model.Expense
import java.util.Date

@Entity(tableName = "expense")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val expenseId: Int = 0,

    val expenseName: String = "",

    val expenseAmount: String = "",

    val expenseDate: String = "",

    val expenseNote: String = "",

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date = Date(),

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,
)

fun ExpenseEntity.asExternalModel(): Expense {
    return Expense(
        expenseId = this.expenseId,
        expenseName = this.expenseName,
        expenseAmount = this.expenseAmount,
        expenseDate = this.expenseDate,
        expenseNote = this.expenseNote,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}