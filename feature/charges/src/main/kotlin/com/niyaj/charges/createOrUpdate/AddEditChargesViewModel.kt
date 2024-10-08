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

package com.niyaj.charges.createOrUpdate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.capitalizeWords
import com.niyaj.common.utils.safeInt
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.ChargesRepository
import com.niyaj.domain.charges.ValidateChargesNameUseCase
import com.niyaj.domain.charges.ValidateChargesPriceUseCase
import com.niyaj.model.Charges
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.annotations.TestOnly
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AddEditChargesViewModel @Inject constructor(
    private val chargesRepository: ChargesRepository,
    private val validateChargesName: ValidateChargesNameUseCase,
    private val validateChargesPrice: ValidateChargesPriceUseCase,
    private val analyticsHelper: AnalyticsHelper,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val chargesId = savedStateHandle.getStateFlow("chargesId", 0)

    var state by mutableStateOf(AddEditChargesState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.shareIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        replay = 1,
    )

    init {
        savedStateHandle.get<Int>("chargesId")?.let { chargesId ->
            if (chargesId != 0) getChargesById(chargesId)
        }
    }

    val nameError: StateFlow<String?> = snapshotFlow { state.chargesName }
        .mapLatest {
            validateChargesName(it, chargesId.value).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val priceError: StateFlow<String?> = snapshotFlow { state.chargesPrice }
        .mapLatest {
            validateChargesPrice(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    fun onEvent(event: AddEditChargesEvent) {
        when (event) {
            is AddEditChargesEvent.ChargesNameChanged -> {
                state = state.copy(chargesName = event.chargesName)
            }

            is AddEditChargesEvent.ChargesPriceChanged -> {
                state = state.copy(chargesPrice = event.chargesPrice.safeInt())
            }

            is AddEditChargesEvent.ChargesApplicableChanged -> {
                state =
                    state.copy(chargesApplicable = !state.chargesApplicable)
            }

            is AddEditChargesEvent.CreateOrUpdateCharges -> {
                createOrUpdateCharges(chargesId.value)
            }
        }
    }

    private fun getChargesById(itemId: Int) {
        if (itemId != 0) {
            viewModelScope.launch {
                when (val result = chargesRepository.getChargesById(itemId)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError("Unable to find charges"))
                    }

                    is Resource.Success -> {
                        result.data?.let { charges ->
                            state = state.copy(
                                chargesName = charges.chargesName,
                                chargesPrice = charges.chargesPrice,
                                chargesApplicable = charges.isApplicable,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createOrUpdateCharges(chargesId: Int = 0) {
        viewModelScope.launch {
            if (nameError.value == null && priceError.value == null) {
                val addOnItem = Charges(
                    chargesId = chargesId,
                    chargesName = state.chargesName.trimEnd().capitalizeWords,
                    chargesPrice = state.chargesPrice,
                    isApplicable = state.chargesApplicable,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = if (chargesId != 0) System.currentTimeMillis() else null,
                )

                when (chargesRepository.upsertCharges(addOnItem)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError("Unable To Create Charges."))
                    }

                    is Resource.Success -> {
                        val message = if (chargesId == 0) "Created" else "Updated"
                        _eventFlow.emit(UiEvent.OnSuccess("Charges $message Successfully."))
                        analyticsHelper.logOnCreateOrUpdateCharges(chargesId, message)
                    }
                }

                state = AddEditChargesState()
            }
        }
    }

    @TestOnly
    internal fun setChargesId(chargesId: Int) {
        savedStateHandle["chargesId"] = chargesId
    }
}

private fun AnalyticsHelper.logOnCreateOrUpdateCharges(chargesId: Int, message: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "charges_$message",
            extras = listOf(
                AnalyticsEvent.Param("charges_$message", chargesId.toString()),
            ),
        ),
    )
}
