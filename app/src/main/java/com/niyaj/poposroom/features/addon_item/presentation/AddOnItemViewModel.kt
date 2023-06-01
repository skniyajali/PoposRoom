package com.niyaj.poposroom.features.addon_item.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.poposroom.features.addon_item.dao.AddOnItemDao
import com.niyaj.poposroom.features.addon_item.domain.use_cases.GetAllAddOnItems
import com.niyaj.poposroom.features.common.event.ItemEvents
import com.niyaj.poposroom.features.common.event.UiState
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AddOnItemViewModel @Inject constructor(
    private val addOnItemDao: AddOnItemDao,
    private val getAllAddOnItems: GetAllAddOnItems,
    @Dispatcher(PoposDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _showSearchBar = MutableStateFlow(false)
    val showSearchBar = _showSearchBar.asStateFlow()

    private val _searchText = mutableStateOf("")
    val searchText: State<String> = _searchText

    private val _selectedAddOnItems = mutableStateListOf<Int>()
    val selectedAddOnItems: SnapshotStateList<Int> = _selectedAddOnItems

    private var _totalItems: List<Int> = emptyList()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var count: Int = 0

    val addOnItems = snapshotFlow { _searchText.value }
        .flatMapLatest { it ->
            getAllAddOnItems(it)
                .onStart { UiState.Loading }
                .map { items ->
                    _totalItems = items.map { it.itemId }
                    if (items.isEmpty()) {
                        UiState.Empty
                    } else UiState.Success(items)
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    fun onEvent(event: ItemEvents) {
        when (event) {
            is ItemEvents.SelectItem -> {
                viewModelScope.launch {
                    if (_selectedAddOnItems.contains(event.itemId)) {
                        _selectedAddOnItems.remove(event.itemId)
                    } else {
                        _selectedAddOnItems.add(event.itemId)
                    }
                }
            }

            is ItemEvents.SelectAllItems -> {
                viewModelScope.launch {
                    count += 1

                    if (_totalItems.isNotEmpty()) {
                        if (_totalItems.size == _selectedAddOnItems.size) {
                            _selectedAddOnItems.clear()
                        } else {
                            _totalItems.forEach { itemId ->
                                if (count % 2 != 0) {
                                    val selectedProduct = _selectedAddOnItems.find { it == itemId }

                                    if (selectedProduct == null) {
                                        _selectedAddOnItems.add(itemId)
                                    }
                                } else {
                                    _selectedAddOnItems.remove(itemId)
                                }
                            }
                        }

                    }
                }
            }

            is ItemEvents.DeselectAllItems -> {
                _selectedAddOnItems.clear()
            }

            is ItemEvents.DeleteItems -> {
                viewModelScope.launch(ioDispatcher) {
                    val result = addOnItemDao.deleteAddOnItems(_selectedAddOnItems.toList())
                    _selectedAddOnItems.clear()

                    if (result != 0) {
                        _eventFlow.emit(UiEvent.OnSuccess("$result items deleted"))
                    } else {
                        _eventFlow.emit(UiEvent.OnError("Unable to delete items"))
                    }
                }
            }

            is ItemEvents.OnSearchClick -> {
                viewModelScope.launch {
                    _showSearchBar.emit(true)
                }
            }

            is ItemEvents.OnSearchTextChanged -> {
                viewModelScope.launch {
                    _searchText.value = event.text
                }
            }

            is ItemEvents.OnSearchTextClearClick -> {
                viewModelScope.launch {
                    _searchText.value = ""
                }
            }

            is ItemEvents.OnSearchBarCloseClick -> {
                viewModelScope.launch {
                    _searchText.value = ""
                    _showSearchBar.emit(false)
                }
            }
        }
    }
}