/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.feature.market.measureUnit

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsEvent.Param
import com.niyaj.core.analytics.AnalyticsHelper
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
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel() {

    val measureUnits = snapshotFlow { mSearchText.value }.flatMapLatest {
        repository.getAllMeasureUnits(it)
    }.mapLatest { list ->
        totalItems = list.map { it.unitId }
        if (list.isEmpty()) UiState.Empty else UiState.Success(list)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch {
            when (val result = repository.deleteMeasureUnits(selectedItems.toList())) {
                is Resource.Success -> {
                    mEventFlow.emit(
                        UiEvent.OnSuccess(
                            "${selectedItems.size} item deleted successfully",
                        ),
                    )
                }

                is Resource.Error -> {
                    mEventFlow.emit(
                        UiEvent.OnError(result.message ?: "Unable to delete items"),
                    )
                    analyticsHelper.logDeletedMeasureUnits(selectedItems.toList())
                }
            }

            mSelectedItems.clear()
        }
    }
}

internal fun AnalyticsHelper.logDeletedMeasureUnits(data: List<Int>) {
    logEvent(
        event = AnalyticsEvent(
            type = "measure_units_deleted",
            extras = listOf(
                Param("measure_units_deleted", data.toString()),
            ),
        ),
    )
}
