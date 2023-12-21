package com.niyaj.model

import androidx.compose.runtime.Stable
import com.niyaj.common.utils.toJoinedDate
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Stable
data class Expense(
    val expenseId: Int = 0,

    val expenseName: String = "",

    val expenseAmount: String = "",

    val expenseDate: String = "",

    val expenseNote: String = "",

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long? = null,
)


fun List<Expense>.searchExpense(searchText: String): List<Expense> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.expenseAmount.contains(searchText, true) ||
                    it.expenseName.contains(searchText, true) ||
                    it.expenseDate.toJoinedDate.contains(searchText, true)
        }
    } else this
}