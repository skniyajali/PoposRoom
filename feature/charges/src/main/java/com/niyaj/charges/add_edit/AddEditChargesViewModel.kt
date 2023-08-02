package com.niyaj.charges.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.safeInt
import com.niyaj.data.repository.ChargesRepository
import com.niyaj.data.repository.validation.ChargesValidationRepository
import com.niyaj.model.Charges
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AddEditChargesViewModel @Inject constructor(
    private val chargesRepository: ChargesRepository,
    private val validationRepository: ChargesValidationRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val chargesId = savedStateHandle.get<Int>("chargesId") ?: 0

    var addEditState by mutableStateOf(AddEditChargesState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<Int>("chargesId")?.let { addOnItemId ->
            getChargesById(addOnItemId)
        }
    }

    val nameError: StateFlow<String?> = snapshotFlow { addEditState.chargesName }
        .mapLatest {
            validationRepository.validateChargesName(it, chargesId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val priceError: StateFlow<String?> = snapshotFlow { addEditState.chargesPrice }
        .mapLatest {
            validationRepository.validateChargesPrice(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun onEvent(event: AddEditChargesEvent) {
        when (event) {
            is AddEditChargesEvent.ChargesNameChanged -> {
                addEditState = addEditState.copy(chargesName = event.chargesName)
            }

            is AddEditChargesEvent.ChargesPriceChanged -> {
                addEditState = addEditState.copy(chargesPrice = event.chargesPrice.safeInt())

            }

            is AddEditChargesEvent.ChargesApplicableChanged -> {
                addEditState =
                    addEditState.copy(chargesApplicable = !addEditState.chargesApplicable)
            }

            is AddEditChargesEvent.CreateOrUpdateCharges -> {
                createOrUpdateCharges(event.chargesId)
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
                            addEditState = addEditState.copy(
                                chargesName = charges.chargesName,
                                chargesPrice = charges.chargesPrice,
                                chargesApplicable = charges.isApplicable
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
                    chargesName = addEditState.chargesName,
                    chargesPrice = addEditState.chargesPrice,
                    isApplicable = addEditState.chargesApplicable,
                    createdAt = Date(),
                    updatedAt = if (chargesId != 0) Date() else null
                )

                when (chargesRepository.upsertCharges(addOnItem)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError("Unable To Create Charges."))

                    }

                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.OnSuccess("Charges Created Successfully."))

                    }
                }

                addEditState = AddEditChargesState()
            }
        }
    }
}