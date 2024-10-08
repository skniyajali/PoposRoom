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

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsEvent.Param
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.ExpenseRepository
import com.niyaj.model.Expense
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpensesSettingsViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel() {

    val expenses = snapshotFlow { mSearchText.value }.flatMapLatest {
        expenseRepository.getAllExpenses(it)
    }.mapLatest { list ->
        totalItems = list.map { it.expenseId }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    private val _exportedItems = MutableStateFlow<List<Expense>>(emptyList())
    val exportedItems = _exportedItems.asStateFlow()

    private val _importedItems = MutableStateFlow<List<Expense>>(emptyList())
    val importedItems = _importedItems.asStateFlow()

    fun onEvent(event: ExpensesSettingsEvent) {
        when (event) {
            is ExpensesSettingsEvent.GetExportedItems -> {
                viewModelScope.launch {
                    if (mSelectedItems.isEmpty()) {
                        _exportedItems.value = expenses.value
                    } else {
                        val list = mutableListOf<Expense>()

                        mSelectedItems.forEach { id ->
                            val category = expenses.value.find { it.expenseId == id }

                            if (category != null) {
                                list.add(category)
                            }
                        }

                        _exportedItems.emit(list.toList())
                    }

                    analyticsHelper.logExportedExpensesToFile(_exportedItems.value.size)
                }
            }

            is ExpensesSettingsEvent.OnImportExpensesItemsFromFile -> {
                viewModelScope.launch {
                    _importedItems.value = emptyList()

                    if (event.data.isNotEmpty()) {
                        totalItems = event.data.map { it.expenseId }
                        _importedItems.value = event.data
                    }

                    analyticsHelper.logImportedExpensesFromFile(event.data.size)
                }
            }

            is ExpensesSettingsEvent.ImportExpensesItemsToDatabase -> {
                viewModelScope.launch {
                    val data = if (mSelectedItems.isNotEmpty()) {
                        mSelectedItems.flatMap { expenseId ->
                            _importedItems.value.filter { it.expenseId == expenseId }
                        }
                    } else {
                        _importedItems.value
                    }

                    when (val result = expenseRepository.importExpensesDataToDatabase(data)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                        }

                        is Resource.Success -> {
                            mEventFlow.emit(
                                UiEvent.OnSuccess("${data.size} expenses has been imported successfully"),
                            )

                            analyticsHelper.logImportedExpensesToDatabase(data.size)
                        }
                    }
                }
            }
        }
    }
}

internal fun AnalyticsHelper.logImportedExpensesFromFile(totalExpenses: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "expenses_imported_from_file",
            extras = listOf(
                Param("expenses_imported_from_file", totalExpenses.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logImportedExpensesToDatabase(totalExpenses: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "expenses_imported_to_database",
            extras = listOf(
                Param("expenses_imported_to_database", totalExpenses.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logExportedExpensesToFile(totalExpenses: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "expenses_exported_to_file",
            extras = listOf(
                Param("expenses_exported_to_file", totalExpenses.toString()),
            ),
        ),
    )
}
