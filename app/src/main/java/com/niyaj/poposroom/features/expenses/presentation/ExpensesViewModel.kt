package com.niyaj.poposroom.features.expenses.presentation

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.poposroom.features.common.event.BaseViewModel
import com.niyaj.poposroom.features.common.event.UiState
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.common.utils.startOfDayTime
import com.niyaj.poposroom.features.expenses.domain.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    override var totalItems: List<Int> = emptyList()

    private val _totalAmount = MutableStateFlow("0")
    val totalAmount = _totalAmount.asStateFlow()

    private val _selectedDate = MutableStateFlow(startOfDayTime)
    val selectedDate = _selectedDate.asStateFlow()

    private val _text = snapshotFlow { searchText.value }

    @OptIn(ExperimentalCoroutinesApi::class)
    val expenses = _text.combine(_selectedDate) { text, date ->
        expenseRepository.getAllExpense(text, date)
    }
        .flatMapLatest { it ->
            it.map { items ->
                totalItems = items.map { it.expenseId }
                _totalAmount.value = items.sumOf { it.expenseAmount.toInt() }.toString()

                if (items.isEmpty()) {
                    UiState.Empty
                } else UiState.Success(items)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    fun selectDate(selectedDate: String) {
        viewModelScope.launch {
            _selectedDate.value = selectedDate
        }
    }

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch(ioDispatcher) {
            when (val result = expenseRepository.deleteExpenses(selectedItems.toList())) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable to delete expenses"))
                }

                is Resource.Success -> {
                    mEventFlow.emit(UiEvent.OnSuccess("${selectedItems.size} expenses has been deleted"))
                }
            }

            mSelectedItems.clear()
        }
    }
}