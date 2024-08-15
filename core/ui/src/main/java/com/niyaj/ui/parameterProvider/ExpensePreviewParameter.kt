/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.ui.parameterProvider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.niyaj.common.utils.getDateInMilliseconds
import com.niyaj.common.utils.getStartTime
import com.niyaj.model.Expense
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.ExpensePreviewData.expenses

class ExpensePreviewParameter : PreviewParameterProvider<UiState<List<Expense>>> {
    override val values: Sequence<UiState<List<Expense>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(expenses),
        )
}

object ExpensePreviewData {
    val expenses = listOf(
        Expense(
            expenseId = 1,
            expenseName = "Groceries",
            expenseAmount = "3500",
            expenseDate = getStartTime,
            expenseNote = "Weekly grocery shopping",
        ),
        Expense(
            expenseId = 2,
            expenseName = "Rent",
            expenseAmount = "2000",
            expenseDate = getStartTime,
            expenseNote = "Apartment rent for June",
        ),
        Expense(
            expenseId = 3,
            expenseName = "Rent",
            expenseAmount = "2400",
            expenseDate = getStartTime,
            expenseNote = "Fill up for the car",
        ),
        Expense(
            expenseId = 4,
            expenseName = "Groceries",
            expenseAmount = "1750",
            expenseDate = getDateInMilliseconds(0, -2),
        ),
        Expense(
            expenseId = 5,
            expenseName = "Groceries",
            expenseAmount = "5300",
            expenseDate = getStartTime,
        ),
        Expense(
            expenseId = 6,
            expenseName = "Utilities",
            expenseAmount = "8100",
            expenseDate = getDateInMilliseconds(0, -2),
            expenseNote = "Electricity bill",
        ),
        Expense(
            expenseId = 7,
            expenseName = "Utilities",
            expenseAmount = "1100",
            expenseDate = getStartTime,
            expenseNote = "Movie tickets",
        ),
        Expense(
            expenseId = 8,
            expenseName = "Utilities",
            expenseAmount = "2200",
            expenseDate = getStartTime,
            expenseNote = "Snacks and beverages",
        ),
        Expense(
            expenseId = 9,
            expenseName = "Transportation",
            expenseAmount = "850",
            expenseDate = getStartTime,
            expenseNote = "Bus fare",
        ),
        Expense(
            expenseId = 10,
            expenseName = "Subscription",
            expenseAmount = "999",
            expenseDate = getStartTime,
            expenseNote = "Streaming service subscription",
        ),
    )

    val expenseNames = listOf(
        "Groceries",
        "Rent",
        "Gas",
        "Dining",
        "Utilities",
        "Entertainment",
        "Transportation",
        "Subscription",
    )
}
