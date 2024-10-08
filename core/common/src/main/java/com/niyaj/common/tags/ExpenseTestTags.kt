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

package com.niyaj.common.tags

object ExpenseTestTags {

    const val EXPENSE_SCREEN_TITLE = "Expenses"
    const val EXPENSE_NOT_AVAILABLE = "Expenses Not Available"
    const val EXPENSE_SEARCH_PLACEHOLDER = "Search for Expenses..."

    const val CREATE_NEW_EXPENSE = "Add New Expense"
    const val EDIT_EXPENSE_ITEM = "Update Expense"

    const val EXPENSE_NAME_FIELD = "Expense Name"
    const val EXPENSE_NAME_ERROR = "Expense Name Error"

    const val EXPENSE_AMOUNT_FIELD = "Expense Amount"
    const val EXPENSE_AMOUNT_ERROR = "Expense Amount Error"

    const val EXPENSE_DATE_FIELD = "Expense Date"
    const val EXPENSE_DATE_ERROR = "Expense Date Error"

    const val EXPENSE_NOTE_FIELD = "Expense Note"

    const val ADD_EDIT_EXPENSE_BUTTON = "AddEdit Expense"

    const val EXPENSE_NAME_EMPTY_ERROR = "Expense name must not be empty."
    const val EXPENSE_NAME_LENGTH_ERROR = "Expense name must be at least 3 characters long."

    const val EXPENSE_PRICE_EMPTY_ERROR = "Expense price must not be empty."
    const val EXPENSE_PRICE_LESS_THAN_TEN_ERROR = "Expense price must greater than 10 rupees."
    const val EXPENSES_PRICE_IS_NOT_VALID = "Expenses amount is not valid."

    const val EXPENSE_DATE_EMPTY_ERROR = "Expense date must not be empty."

    const val EXPENSES_AMOUNT_ALREADY_EXISTS =
        "Expenses already added on given expense name and chosen date."

    const val DELETE_EXPENSE_TITLE = "Delete Expense?"
    const val DELETE_EXPENSE_MESSAGE = "Are you sure to delete these expenses?"

    const val EXPENSE_TAG = "Expense-"
    const val EXPENSE_LIST = "ExpenseList"

    const val EXPENSE_SETTINGS_TITLE = "Expenses Settings"

    const val IMPORT_EXPENSE_TITLE = "Import Expenses"
    const val IMPORT_EXPENSE_SUB_TITLE = "Click here to import data from file."
    const val IMPORT_EXPENSE_NOTE_TEXT = "Make sure to open expenses.json file."

    const val EXPORT_EXPENSE_TITLE = "Export Expenses"
    const val EXPORT_EXPENSE_SUB_TITLE = "Click here to export expenses to file."

    const val EXPORT_EXPENSE_FILE_NAME = "expenses"
}
