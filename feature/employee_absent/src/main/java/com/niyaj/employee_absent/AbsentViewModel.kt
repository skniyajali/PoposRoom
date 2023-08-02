package com.niyaj.employee_absent

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.AbsentRepository
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AbsentViewModel @Inject constructor(
    private val absentRepository: AbsentRepository,
    @Dispatcher(PoposDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
): BaseViewModel() {

    override var totalItems: List<Int> = emptyList()

    @OptIn(ExperimentalCoroutinesApi::class)
    val absents = snapshotFlow { searchText.value }
        .flatMapLatest { it ->
            absentRepository.getAllEmployeeAbsents(it)
                .onStart { UiState.Loading }
                .map { items ->
                    totalItems = items.flatMap { item -> item.absents.map { it.absentId } }

                    if (items.all { it.absents.isEmpty() }) {
                        UiState.Empty
                    } else UiState.Success(items)
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    private val _selectedEmployee = MutableStateFlow(0)
    val selectedEmployee = _selectedEmployee.asStateFlow()

    fun selectEmployee(employeeId: Int) {
        viewModelScope.launch {
            if (_selectedEmployee.value == employeeId) {
                _selectedEmployee.value = 0
            }else {
                _selectedEmployee.value = employeeId
            }
        }
    }

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch(ioDispatcher) {
            when (val result = absentRepository.deleteAbsents(selectedItems.toList())) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                }
                is Resource.Success -> {
                    mEventFlow.emit(UiEvent.OnSuccess("${selectedItems.size} absents has been deleted"))
                }
            }

            mSelectedItems.clear()
        }
    }

}