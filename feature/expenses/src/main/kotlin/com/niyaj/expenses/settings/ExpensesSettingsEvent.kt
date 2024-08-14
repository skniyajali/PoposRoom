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

package com.niyaj.expenses.settings

import com.niyaj.model.Expense

sealed class ExpensesSettingsEvent {

    data object GetExportedItems : ExpensesSettingsEvent()

    data class OnImportExpensesItemsFromFile(val data: List<Expense>) : ExpensesSettingsEvent()

    data object ImportExpensesItemsToDatabase : ExpensesSettingsEvent()
}
