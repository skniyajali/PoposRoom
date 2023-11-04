package com.niyaj.daily_market.measure_unit

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.MeasureUnitRepository
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeasureUnitViewModel @Inject constructor(
    private val repository: MeasureUnitRepository,
): BaseViewModel() {


    val measureUnits = snapshotFlow { mSearchText.value }.flatMapLatest {
        repository.getAllMeasureUnits(it)
    }.mapLatest { list ->
        totalItems = list.map { it.unitId }
        if (list.isEmpty()) UiState.Empty else UiState.Success(list)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
    )

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch {
            when (val result = repository.deleteMeasureUnits(selectedItems.toList())) {
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