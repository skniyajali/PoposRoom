package com.niyaj.poposroom.features.addon_item.presentation.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.poposroom.features.addon_item.domain.model.AddOnItem
import com.niyaj.poposroom.features.addon_item.domain.repository.AddOnItemRepository
import com.niyaj.poposroom.features.addon_item.domain.repository.AddOnItemValidationRepository
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.common.utils.safeInt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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
class AddEditAddOnItemViewModel @Inject constructor(
    private val repository: AddOnItemRepository,
    private val validationRepository: AddOnItemValidationRepository,
    @Dispatcher(PoposDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var addOnItemId = savedStateHandle.get<Int>("addOnItemId")

    var addEditState by mutableStateOf(AddEditAddOnItemState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<Int>("addOnItemId")?.let { addOnItemId ->
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

    fun getAllAddOnItemById(itemId: Int) {
        viewModelScope.launch(ioDispatcher) {
            when (val result = repository.getAddOnItemById(itemId)) {
                is Resource.Success -> {
                    result.data?.let { addOnItem ->
                        addOnItemId = addOnItem.itemId

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

    fun resetFields() {
        addEditState = AddEditAddOnItemState()
    }

    private fun createOrUpdateAddOnItem(addOnItemId: Int = 0) {
        viewModelScope.launch {
            if (nameError.value == null && priceError.value == null) {
                val addOnItem = AddOnItem(
                    itemId = addOnItemId,
                    itemName = addEditState.itemName,
                    itemPrice = addEditState.itemPrice,
                    isApplicable = addEditState.isApplicable,
                    createdAt = Date(),
                    updatedAt = if (addOnItemId != 0) Date() else null
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
                        _eventFlow.emit(UiEvent.OnSuccess("AddOn Item Created Successfully."))
                    }
                }

                addEditState = AddEditAddOnItemState()
            }
        }
    }
}