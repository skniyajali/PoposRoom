package com.niyaj.poposroom.features.expenses.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niyaj.poposroom.features.common.utils.toJoinedDate
import java.util.Date

@Entity
data class Expense(
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


fun List<Expense>.searchExpense(searchText: String): List<Expense> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.expenseAmount.toString().contains(searchText, true) ||
                    it.expenseName.contains(searchText, true) ||
                    it.expenseDate.toJoinedDate.contains(searchText, true)
        }
    }else this
}