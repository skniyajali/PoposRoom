package com.niyaj.addonitem

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.AddOnItemRepository
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddOnViewModel @Inject constructor(
    private val itemRepository: AddOnItemRepository,
) : BaseViewModel() {

    override var totalItems: List<Int> = emptyList()

    @OptIn(ExperimentalCoroutinesApi::class)
    val addOnItems = snapshotFlow { searchText.value }
        .flatMapLatest { it ->
            itemRepository.getAllAddOnItem(it)
                .onStart { UiState.Loading }
                .map { items ->
                    totalItems = items.map { it.itemId }
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

        viewModelScope.launch {
            when (val result = itemRepository.deleteAddOnItems(selectedItems.toList())) {
                is Resource.Success -> {
                    mEventFlow.emit(
                        UiEvent.OnSuccess(
                            "${selectedItems.size} item deleted successfully"
                        )
                    )
                }

                is Resource.Error -> {
                    mEventFlow.emit(
                        UiEvent.OnError(result.message ?: "Unable to delete items")
                    )
                }
            }

            mSelectedItems.clear()
        }
    }

}