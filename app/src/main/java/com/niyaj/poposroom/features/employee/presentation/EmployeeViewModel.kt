package com.niyaj.poposroom.features.employee.presentation

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.poposroom.features.common.event.BaseViewModel
import com.niyaj.poposroom.features.common.event.UiState
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.employee.domain.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    @Dispatcher(PoposDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
): BaseViewModel() {

    override var totalItems: List<Int> = emptyList()

    @OptIn(ExperimentalCoroutinesApi::class)
    val employees = snapshotFlow { searchText.value }
        .flatMapLatest { it ->
            employeeRepository.getAllEmployee(it)
                .onStart { UiState.Loading }
                .map { items ->
                    totalItems = items.map { it.employeeId }
                    if (items.isEmpty()) {
                        UiState.Empty
                    } else UiState.Success(items)
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch(ioDispatcher) {
            when(employeeRepository.deleteEmployees(selectedItems.toList())) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError("Unable to delete employee"))
                }
                is Resource.Success -> {
                    mEventFlow.emit(
                        UiEvent.OnSuccess(
                            "${selectedItems.size} employee has been deleted"
                        )
                    )
                }
            }
            mSelectedItems.clear()
        }
    }

}