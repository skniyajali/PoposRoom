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

package com.niyaj.expenses.createOrUpdate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.common.tags.ExpenseTestTags.EXPENSES_AMOUNT_ALREADY_EXISTS
import com.niyaj.common.utils.capitalizeWords
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.ExpenseRepository
import com.niyaj.domain.expense.ValidateExpenseAmountUseCase
import com.niyaj.domain.expense.ValidateExpenseDateUseCase
import com.niyaj.domain.expense.ValidateExpenseNameUseCase
import com.niyaj.model.Expense
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.annotations.TestOnly
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AddEditExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val validateExpenseAmount: ValidateExpenseAmountUseCase,
    private val validateExpenseDate: ValidateExpenseDateUseCase,
    private val validateExpenseName: ValidateExpenseNameUseCase,
    private val analyticsHelper: AnalyticsHelper,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val expenseId = savedStateHandle.getStateFlow("expenseId", 0)

    var state by mutableStateOf(AddEditExpenseState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.shareIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        replay = 1,
    )

    private val observableName = snapshotFlow { state.expenseName }
    private val observableDate = snapshotFlow { state.expenseDate }

    init {
        savedStateHandle.get<Int>("expenseId")?.let { expenseId ->
            getExpenseById(expenseId)
        }
    }

    val nameError: StateFlow<String?> = observableName.mapLatest {
        validateExpenseName(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val priceError: StateFlow<String?> = snapshotFlow { state.expenseAmount }
        .mapLatest {
            validateExpenseAmount(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val dateError: StateFlow<String?> = observableDate.mapLatest {
        validateExpenseDate(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val existingData: StateFlow<String?> = observableName.combine(observableDate) { name, date ->
        if (expenseRepository.findExpenseByNameAndDate(name, date)) {
            EXPENSES_AMOUNT_ALREADY_EXISTS
        } else {
            null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val expensesName: StateFlow<List<String>> = snapshotFlow { state.expenseName }
        .flatMapLatest { name ->
            expenseRepository.getAllExpenseName(name)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun onEvent(event: AddEditExpenseEvent) {
        when (event) {
            is AddEditExpenseEvent.ExpensesNameChanged -> {
                state = state.copy(expenseName = event.expenseName)
            }

            is AddEditExpenseEvent.ExpensesAmountChanged -> {
                state = state.copy(expenseAmount = event.expenseAmount)
            }

            is AddEditExpenseEvent.ExpensesDateChanged -> {
                state = state.copy(expenseDate = event.expenseDate)
            }

            is AddEditExpenseEvent.ExpensesNoteChanged -> {
                state = state.copy(expenseNote = event.expenseNote)
            }

            is AddEditExpenseEvent.AddOrUpdateExpense -> {
                addOrUpdateExpense(expenseId.value)
            }
        }
    }

    private fun getExpenseById(expenseId: Int) {
        viewModelScope.launch {
            when (val result = expenseRepository.getExpenseById(expenseId)) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError("Unable to find expense"))
                }

                is Resource.Success -> {
                    result.data?.let { expense ->
                        state = state.copy(
                            expenseName = expense.expenseName,
                            expenseDate = expense.expenseDate,
                            expenseAmount = expense.expenseAmount,
                            expenseNote = expense.expenseNote,
                        )
                    }
                }
            }
        }
    }

    private fun addOrUpdateExpense(expenseId: Int = 0) {
        viewModelScope.launch {
            val hasError = listOf(nameError, dateError, priceError).all { it.value != null }

            if (!hasError) {
                val newExpense = Expense(
                    expenseId = expenseId,
                    expenseName = state.expenseName.trim().capitalizeWords,
                    expenseAmount = state.expenseAmount,
                    expenseDate = state.expenseDate,
                    expenseNote = state.expenseNote.trim().capitalizeWords,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = if (expenseId != 0) System.currentTimeMillis() else null,
                )

                when (val result = expenseRepository.upsertExpense(newExpense)) {
                    is Resource.Error -> {
                        _eventFlow.emit(
                            UiEvent.OnError(
                                result.message ?: "Unable to add or update expense",
                            ),
                        )
                    }

                    is Resource.Success -> {
                        val message = if (expenseId == 0) "added" else "updated"
                        _eventFlow.emit(UiEvent.OnSuccess("Expense $message successfully."))
                        analyticsHelper.logOnCreateOrUpdateExpenses(expenseId, message)
                    }
                }

                state = AddEditExpenseState()
            }
        }
    }

    @TestOnly
    internal fun setExpenseId(expenseId: Int) {
        savedStateHandle["expenseId"] = expenseId
    }
}

private fun AnalyticsHelper.logOnCreateOrUpdateExpenses(data: Int, message: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "expenses_$message",
            extras = listOf(
                AnalyticsEvent.Param("expenses_$message", data.toString()),
            ),
        ),
    )
}
