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

package com.niyaj.settings.dataDeletion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.safeInt
import com.niyaj.data.repository.KeepDataConfigRepository
import com.niyaj.data.repository.validation.KeepDataConfigValidationRepository
import com.niyaj.model.KeepDataConfig
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataDeletionSettingsViewModel @Inject constructor(
    private val repository: KeepDataConfigRepository,
    private val validationRepository: KeepDataConfigValidationRepository,
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var state by mutableStateOf(DataDeletionSettingsState())
        private set

    val reportIntervalError = snapshotFlow { state.reportInterval }.mapLatest {
        validationRepository.validateReportInterval(it.safeInt()).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val orderIntervalError = snapshotFlow { state.orderInterval }.mapLatest {
        validationRepository.validateOrderInterval(it.safeInt()).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val cartIntervalError = snapshotFlow { state.cartInterval }.mapLatest {
        validationRepository.validateCartInterval(it.safeInt()).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val expenseIntervalError = snapshotFlow { state.expenseInterval }.mapLatest {
        validationRepository.validateExpenseInterval(it.safeInt()).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val marketListIntervalError = snapshotFlow { state.marketListInterval }.mapLatest {
        validationRepository.validateMarketListInterval(it.safeInt()).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    init {
        getDataDeletionSettings()
    }

    fun onEvent(event: DataDeletionEvent) {
        when (event) {
            is DataDeletionEvent.OnReportIntervalChange -> {
                state = state.copy(reportInterval = event.interval)
            }

            is DataDeletionEvent.OnCartIntervalChange -> {
                state = state.copy(cartInterval = event.interval)
            }

            is DataDeletionEvent.OnOrderIntervalChange -> {
                state = state.copy(orderInterval = event.interval)
            }

            is DataDeletionEvent.OnExpenseIntervalChange -> {
                state = state.copy(expenseInterval = event.interval)
            }

            is DataDeletionEvent.OnMarketListIntervalChange -> {
                state = state.copy(marketListInterval = event.interval)
            }

            is DataDeletionEvent.OnDeleteDataBeforeIntervalChange -> {
                state = state.copy(deleteDataBeforeInterval = !state.deleteDataBeforeInterval)
            }

            is DataDeletionEvent.OnSave -> {
                updateKeepDataConfig()
            }
        }
    }

    private fun updateKeepDataConfig() {
        viewModelScope.launch {
            val result = repository.updateKeepDataConfig(
                KeepDataConfig(
                    reportInterval = state.reportInterval.safeInt(),
                    orderInterval = state.orderInterval.safeInt(),
                    cartInterval = state.cartInterval.safeInt(),
                    expenseInterval = state.expenseInterval.safeInt(),
                    marketListInterval = state.marketListInterval.safeInt(),
                    deleteDataBeforeInterval = state.deleteDataBeforeInterval,
                ),
            )

            when (result) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError(result.message.toString()))
                }

                is Resource.Success -> {
                    _eventFlow.emit(UiEvent.OnSuccess("Data deletion settings updated successfully"))
                }
            }
        }
    }

    private fun getDataDeletionSettings() {
        viewModelScope.launch {
            val data = repository.keepDataConfig.collectLatest { data ->
                state = state.copy(
                    reportInterval = data.reportInterval.toString(),
                    orderInterval = data.orderInterval.toString(),
                    cartInterval = data.cartInterval.toString(),
                    expenseInterval = data.expenseInterval.toString(),
                    marketListInterval = data.marketListInterval.toString(),
                    deleteDataBeforeInterval = data.deleteDataBeforeInterval,
                )
            }
        }
    }
}
