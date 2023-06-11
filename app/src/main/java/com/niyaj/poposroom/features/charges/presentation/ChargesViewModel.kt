package com.niyaj.poposroom.features.charges.presentation

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.poposroom.features.charges.domain.repository.ChargesRepository
import com.niyaj.poposroom.features.common.event.BaseViewModel
import com.niyaj.poposroom.features.common.event.UiState
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.common.utils.UiEvent
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
class ChargesViewModel @Inject constructor(
    private val chargesRepository: ChargesRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher
): BaseViewModel() {

    override var totalItems: List<Int> = emptyList()

    @OptIn(ExperimentalCoroutinesApi::class)
    val charges = snapshotFlow { searchText.value }
        .flatMapLatest { it ->
            chargesRepository.getAllCharges(it)
                .onStart { UiState.Loading }
                .map { items ->
                    totalItems = items.map { it.chargesId }
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
            when(chargesRepository.deleteCharges(selectedItems.toList())) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError("Unable to delete charges"))
                }
                is Resource.Success -> {
                    mEventFlow.emit(UiEvent.OnSuccess("${selectedItems.size} charges has been deleted"))
                }
            }

            mSelectedItems.clear()
        }
    }

}