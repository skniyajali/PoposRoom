package com.niyaj.employee.settings

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.EmployeeRepository
import com.niyaj.model.Employee
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
class EmployeeSettingsViewModel @Inject constructor(
    private val repository: EmployeeRepository,
) : BaseViewModel() {

    val employees = snapshotFlow { mSearchText.value }.flatMapLatest {
        repository.getAllEmployee(it)
    }.mapLatest { list ->
        totalItems = list.map { it.employeeId }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _exportedItems = MutableStateFlow<List<Employee>>(emptyList())
    val exportedItems = _exportedItems.asStateFlow()

    private val _importedItems = MutableStateFlow<List<Employee>>(emptyList())
    val importedItems = _importedItems.asStateFlow()

    fun onEvent(event: EmployeeSettingsEvent) {
        when (event) {
            is EmployeeSettingsEvent.GetExportedItems -> {
                viewModelScope.launch {
                    if (mSelectedItems.isEmpty()) {
                        _exportedItems.value = employees.value
                    } else {
                        val list = mutableListOf<Employee>()

                        mSelectedItems.forEach { id ->
                            val category = employees.value.find { it.employeeId == id }

                            if (category != null) {
                                list.add(category)
                            }
                        }

                        _exportedItems.emit(list.toList())
                    }
                }
            }

            is EmployeeSettingsEvent.OnImportEmployeeItemsFromFile -> {
                viewModelScope.launch {
                    _importedItems.value = emptyList()

                    if (event.data.isNotEmpty()) {
                        totalItems = event.data.map { it.employeeId }
                        _importedItems.value = event.data
                    }
                }
            }

            is EmployeeSettingsEvent.ImportEmployeeItemsToDatabase -> {
                viewModelScope.launch {
                    val data = if (mSelectedItems.isNotEmpty()) {
                        mSelectedItems.flatMap { employeeId ->
                            _importedItems.value.filter { it.employeeId == employeeId }
                        }
                    } else {
                        _importedItems.value
                    }

                    when (val result = repository.importEmployeesToDatabase(data)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                        }

                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.OnSuccess("${data.size} employees has been imported successfully"))
                        }
                    }
                }
            }
        }
    }
}