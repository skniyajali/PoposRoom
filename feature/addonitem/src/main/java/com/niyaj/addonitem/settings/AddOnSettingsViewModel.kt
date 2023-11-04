package com.niyaj.addonitem.settings

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.AddOnItemRepository
import com.niyaj.model.AddOnItem
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
class AddOnSettingsViewModel @Inject constructor(
    private val addOnItemRepository: AddOnItemRepository
): BaseViewModel() {

    val addonItems = snapshotFlow { mSearchText.value }.flatMapLatest {
        addOnItemRepository.getAllAddOnItem(it)
    }.mapLatest { list ->
        totalItems = list.map { it.itemId }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _exportedItems = MutableStateFlow<List<AddOnItem>>(emptyList())
    val exportedItems = _exportedItems.asStateFlow()

    private val _importedItems = MutableStateFlow<List<AddOnItem>>(emptyList())
    val importedItems = _importedItems.asStateFlow()

    fun onEvent(event: AddOnSettingsEvent) {
        when(event) {
            is AddOnSettingsEvent.GetExportedItems -> {
                viewModelScope.launch {
                    if (mSelectedItems.isEmpty()) {
                        _exportedItems.value = addonItems.value
                    } else {
                        val list = mutableListOf<AddOnItem>()

                        mSelectedItems.forEach { id ->
                            val category = addonItems.value.find { it.itemId == id }

                            if (category != null) {
                                list.add(category)
                            }
                        }

                        _exportedItems.emit(list.toList())
                    }
                }
            }

            is AddOnSettingsEvent.OnImportAddOnItemsFromFile -> {
                viewModelScope.launch {
                    _importedItems.value = emptyList()

                    if (event.data.isNotEmpty()) {
                        totalItems = event.data.map { it.itemId }
                        _importedItems.value = event.data
                    }
                }
            }

            is AddOnSettingsEvent.ImportAddOnItemsToDatabase -> {
                viewModelScope.launch {
                    val data = if (mSelectedItems.isNotEmpty()) {
                        mSelectedItems.flatMap {itemId ->
                            _importedItems.value.filter { it.itemId == itemId }
                        }
                    }else {
                        _importedItems.value
                    }

                    when(val result = addOnItemRepository.importAddOnItemsToDatabase(data)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                        }
                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.OnSuccess("${data.size} items has been imported successfully"))
                        }
                    }
                }
            }
        }
    }
}