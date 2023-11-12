package com.niyaj.addonitem.add_edit

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
import com.niyaj.data.repository.AddOnItemRepository
import com.niyaj.data.repository.validation.AddOnItemValidationRepository
import com.niyaj.model.AddOnItem
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
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AddEditAddOnItemViewModel @Inject constructor(
    private val repository: AddOnItemRepository,
    private val validationRepository: AddOnItemValidationRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val addOnItemId = savedStateHandle.get<Int>("itemId") ?: 0

    var addEditState by mutableStateOf(AddEditAddOnItemState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<Int>("itemId")?.let { addOnItemId ->
            getAllAddOnItemById(addOnItemId)
        }
    }

    val nameError: StateFlow<String?> = snapshotFlow { addEditState.itemName }
        .mapLatest {
            validationRepository.validateItemName(it, addOnItemId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val priceError: StateFlow<String?> = snapshotFlow { addEditState.itemPrice }
        .mapLatest {
            validationRepository.validateItemPrice(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun onEvent(event: AddEditAddOnItemEvent) {
        when (event) {
            is AddEditAddOnItemEvent.ItemNameChanged -> {
                addEditState = addEditState.copy(itemName = event.itemName)
            }

            is AddEditAddOnItemEvent.ItemPriceChanged -> {
                addEditState = addEditState.copy(itemPrice = event.itemPrice.safeInt())
            }

            is AddEditAddOnItemEvent.ItemApplicableChanged -> {
                addEditState = addEditState.copy(isApplicable = !addEditState.isApplicable)
            }

            is AddEditAddOnItemEvent.CreateUpdateAddOnItem -> {
                createOrUpdateAddOnItem(event.addOnItemId)
            }
        }
    }

    private fun getAllAddOnItemById(itemId: Int) {
        if (itemId != 0) {
            viewModelScope.launch {
                when (val result = repository.getAddOnItemById(itemId)) {
                    is Resource.Success -> {
                        result.data?.let { addOnItem ->
                            addEditState = addEditState.copy(
                                itemName = addOnItem.itemName,
                                itemPrice = addOnItem.itemPrice,
                                isApplicable = addOnItem.isApplicable
                            )
                        }
                    }

                    is Resource.Error -> {
                        _eventFlow.emit(
                            UiEvent.OnError(result.message ?: "Unable to get addon item")
                        )
                    }
                }
            }
        }
    }

    private fun createOrUpdateAddOnItem(addOnItemId: Int = 0) {
        viewModelScope.launch {
            if (nameError.value == null && priceError.value == null) {
                val addOnItem = AddOnItem(
                    itemId = addOnItemId,
                    itemName = addEditState.itemName.capitalizeWords.trimEnd(),
                    itemPrice = addEditState.itemPrice,
                    isApplicable = addEditState.isApplicable,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = if (addOnItemId != 0) System.currentTimeMillis() else null
                )

                when (val result = repository.upsertAddOnItem(addOnItem)) {
                    is Resource.Error -> {
                        _eventFlow.emit(
                            UiEvent.OnError(
                                result.message ?: "Unable To Create AddOn Item."
                            )
                        )
                    }

                    is Resource.Success -> {
                        val message = if (addOnItemId == 0) "Created" else "Updated"
                        _eventFlow.emit(UiEvent.OnSuccess("AddOn Item $message Successfully."))
                    }
                }

                addEditState = AddEditAddOnItemState()
            }
        }
    }
}