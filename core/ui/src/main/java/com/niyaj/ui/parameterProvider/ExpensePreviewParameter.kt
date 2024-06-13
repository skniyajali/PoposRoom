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
            expenseId = 10,
            expenseName = "Subscription",
            expenseAmount = "999",
            expenseDate = "${System.currentTimeMillis()}",
            expenseNote = "Streaming service subscription",
        ),
        Expense(
            expenseId = 1,
            expenseName = "Groceries",
            expenseAmount = "3500",
            expenseDate = "${System.currentTimeMillis()}",
            expenseNote = "Weekly grocery shopping",
        ),
        Expense(
            expenseId = 2,
            expenseName = "Rent",
            expenseAmount = "2000",
            expenseDate = "${System.currentTimeMillis()}",
            expenseNote = "Apartment rent for June",
        ),
        Expense(
            expenseId = 3,
            expenseName = "Rent",
            expenseAmount = "2400",
            expenseDate = "${System.currentTimeMillis() - 172800000}",
            expenseNote = "Fillup for the car",
        ),
        Expense(
            expenseId = 4,
            expenseName = "Groceries",
            expenseAmount = "1750",
            expenseDate = "${System.currentTimeMillis()}",
        ),
        Expense(
            expenseId = 5,
            expenseName = "Groceries",
            expenseAmount = "5300",
            expenseDate = "${System.currentTimeMillis()}",
        ),
        Expense(
            expenseId = 6,
            expenseName = "Utilities",
            expenseAmount = "8100",
            expenseDate = "${System.currentTimeMillis() - 86400000}",
            expenseNote = "Electricity bill",
        ),
        Expense(
            expenseId = 7,
            expenseName = "Utilities",
            expenseAmount = "1100",
            expenseDate = "${System.currentTimeMillis()}",
            expenseNote = "Movie tickets",
        ),
        Expense(
            expenseId = 8,
            expenseName = "Utilities",
            expenseAmount = "2200",
            expenseDate = "${System.currentTimeMillis()}",
            expenseNote = "Snacks and beverages",
        ),
        Expense(
            expenseId = 9,
            expenseName = "Transportation",
            expenseAmount = "850",
            expenseDate = "${System.currentTimeMillis()}",
            expenseNote = "Bus fare",
        ),
    )

    val expenseNames = listOf(
        "Groceries",
        "Rent",
        "Gas",
        "Dining",
        "Groceries",
        "Utilities",
        "Entertainment",
        "Groceries",
        "Transportation",
        "Subscription",
    )
}
