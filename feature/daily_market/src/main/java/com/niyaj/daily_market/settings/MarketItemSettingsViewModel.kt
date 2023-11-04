package com.niyaj.daily_market.settings

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.MarketItemRepository
import com.niyaj.model.MarketItem
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
class MarketItemSettingsViewModel @Inject constructor(
    private val repository: MarketItemRepository,
): BaseViewModel() {

    val items = snapshotFlow { mSearchText.value }.flatMapLatest {
        repository.getAllMarketItems(it)
    }.mapLatest { list ->
        totalItems = list.map { it.itemId }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _exportedItems = MutableStateFlow<List<MarketItem>>(emptyList())
    val exportedItems = _exportedItems.asStateFlow()

    private val _importedItems = MutableStateFlow<List<MarketItem>>(emptyList())
    val importedItems = _importedItems.asStateFlow()

    fun onEvent(event: MarketItemSettingsEvent) {
        when(event) {
            is MarketItemSettingsEvent.GetExportedMarketItem -> {
                viewModelScope.launch {
                    if (mSelectedItems.isEmpty()) {
                        _exportedItems.value = items.value
                    } else {
                        val list = mutableListOf<MarketItem>()

                        mSelectedItems.forEach { id ->
                            val item = items.value.find { it.itemId == id }

                            if (item != null) {
                                list.add(item)
                            }
                        }

                        _exportedItems.emit(list.toList())
                    }
                }
            }

            is MarketItemSettingsEvent.OnImportMarketItemsFromFile -> {
                viewModelScope.launch {
                    _importedItems.value = emptyList()

                    if (event.data.isNotEmpty()) {
                        totalItems = event.data.map { it.itemId }
                        _importedItems.value = event.data
                    }
                }
            }

            is MarketItemSettingsEvent.ImportMarketItemsToDatabase -> {
                viewModelScope.launch {
                    val data = if (mSelectedItems.isNotEmpty()) {
                        mSelectedItems.flatMap {itemId ->
                            _importedItems.value.filter { it.itemId == itemId }
                        }
                    }else {
                        _importedItems.value
                    }

                    when(val result = repository.importMarketItemsToDatabase(data)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                        }
                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.OnSuccess("${data.size} items imported successfully"))
                        }
                    }
                }
            }
        }
    }
}